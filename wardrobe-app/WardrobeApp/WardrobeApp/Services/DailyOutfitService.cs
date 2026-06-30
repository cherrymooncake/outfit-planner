using Microsoft.EntityFrameworkCore;
using WardrobeApp.Data;
using WardrobeApp.DTOs;
using WardrobeApp.Entities;
using System.Text.Json;
using System.Net.Http.Json; 

namespace WardrobeApp.Services;

public class DailyOutfitService : IDailyOutfitService
{
    private readonly AppDbContext _context;
    private readonly IHttpClientFactory _httpClientFactory;

    public DailyOutfitService(AppDbContext context, IHttpClientFactory httpClientFactory) 
    {
        _context = context;
        _httpClientFactory = httpClientFactory;
    }

    public async Task<DailyOutfitDto?> GetByDateAsync(Guid userId, DateOnly date)
    {
        var dailyOutfit = await _context.DailyOutfits
            .Include(d => d.Outfit)
                .ThenInclude(o => o.Template)
            .Include(d => d.Outfit)
                .ThenInclude(o => o.OutfitItems).ThenInclude(oi => oi.Item)
            .Include(d => d.Outfit)
                .ThenInclude(o => o.OutfitCategories).ThenInclude(oc => oc.Category)
            .FirstOrDefaultAsync(d => d.UserId == userId && d.Date == date);

        if (dailyOutfit == null) return null;

        return new DailyOutfitDto
        {
            Date = dailyOutfit.Date,
            Outfit = MapOutfitToDto(dailyOutfit.Outfit)
        };
    }

    public async Task<List<DailyOutfitDto>> GetMonthAsync(Guid userId, int year, int month)
    {
        var startDate = new DateOnly(year, month, 1);
        var endDate = startDate.AddMonths(1).AddDays(-1);

        var dailyOutfits = await _context.DailyOutfits
            .Include(d => d.Outfit)
                .ThenInclude(o => o.Template)
            .Include(d => d.Outfit)
                .ThenInclude(o => o.OutfitItems).ThenInclude(oi => oi.Item)
            .Include(d => d.Outfit)
                .ThenInclude(o => o.OutfitCategories).ThenInclude(oc => oc.Category)
            .Where(d => d.UserId == userId && d.Date >= startDate && d.Date <= endDate)
            .OrderBy(d => d.Date)
            .ToListAsync();

        return dailyOutfits.Select(d => new DailyOutfitDto
        {
            Date = d.Date,
            Outfit = MapOutfitToDto(d.Outfit)
        }).ToList();
    }

    public async Task<DailyOutfitDto> SetOutfitAsync(Guid userId, SetDailyOutfitDto dto)
    {
        // Проверяем, существует ли указанный образ
        var outfitExists = await _context.Outfits.AnyAsync(o => o.Id == dto.OutfitId && o.UserId == userId);
        if (!outfitExists) throw new ArgumentException("Образ не найден");

        // Ищем, есть ли уже запись на этот день
        var existingDailyOutfit = await _context.DailyOutfits
            .FirstOrDefaultAsync(d => d.UserId == userId && d.Date == dto.Date);

        if (existingDailyOutfit != null)
        {
            // Обновляем существующий
            existingDailyOutfit.OutfitId = dto.OutfitId;
            existingDailyOutfit.UpdatedAt = DateTime.UtcNow;
        }
        else
        {
            // Создаем новый
            var dailyOutfit = new DailyOutfit
            {
                Id = Guid.NewGuid(),
                UserId = userId,
                OutfitId = dto.OutfitId,
                Date = dto.Date,
                CreatedAt = DateTime.UtcNow,
                UpdatedAt = DateTime.UtcNow
            };
            _context.DailyOutfits.Add(dailyOutfit);
        }

        await _context.SaveChangesAsync();

        return await GetByDateAsync(userId, dto.Date) ?? throw new Exception("Ошибка сохранения образа на день");
    }

    public async Task<bool> DeleteOutfitAsync(Guid userId, DateOnly date)
    {
        var dailyOutfit = await _context.DailyOutfits
            .FirstOrDefaultAsync(d => d.UserId == userId && d.Date == date);

        if (dailyOutfit == null) return false;

        _context.DailyOutfits.Remove(dailyOutfit);
        await _context.SaveChangesAsync();
        return true;
    }

    public async Task<OutfitResponseDto?> GetRandomOutfitAsync(Guid userId)
    {
        var randomOutfit = await _context.Outfits
            .Include(o => o.Template)
            .Include(o => o.OutfitItems).ThenInclude(oi => oi.Item)
            .Include(o => o.OutfitCategories).ThenInclude(oc => oc.Category)
            .Where(o => o.UserId == userId)
            .OrderBy(o => EF.Functions.Random())
            .FirstOrDefaultAsync();

        if (randomOutfit == null) return null;

        return MapOutfitToDto(randomOutfit);
    }
    
    public async Task<AiRecommendationResponseDto> GetAiRecommendationAsync(Guid userId, AiRecommendationRequestDto request)
    {
        var outfits = await _context.Outfits
            .Include(o => o.OutfitCategories).ThenInclude(oc => oc.Category)
            .Include(o => o.OutfitItems).ThenInclude(oi => oi.Item).ThenInclude(i => i.ItemTags).ThenInclude(it => it.Tag)
            .Include(o => o.OutfitItems).ThenInclude(oi => oi.Item).ThenInclude(i => i.ItemCategories).ThenInclude(ic => ic.Category)
            .Where(o => o.UserId == userId)
            .ToListAsync();

        if (!outfits.Any()) 
            throw new ArgumentException("У вас пока нет сохраненных образов для анализа.");

        var pythonPayload = new
        {
            prompt = request.Prompt,
            weather_context = request.WeatherContext,
            outfits = outfits.Select(o => new
            {
                id = o.Id.ToString(),
                text_description = GenerateOutfitTextDescription(o)
            }).ToList()
        };

        try
        {
            using var client = _httpClientFactory.CreateClient();
            var response = await client.PostAsJsonAsync("http://localhost:8001/recommend", pythonPayload);
            
            if (!response.IsSuccessStatusCode)
            {
                var error = await response.Content.ReadAsStringAsync();
                throw new Exception($"Python Error: {error}");
            }

            var resultDoc = await response.Content.ReadFromJsonAsync<JsonElement>();
            var recommendedId = Guid.Parse(resultDoc.GetProperty("recommended_outfit_id").GetString()!);
            var explanation = resultDoc.GetProperty("explanation").GetString()!;

            var selectedOutfit = outfits.First(o => o.Id == recommendedId);

            return new AiRecommendationResponseDto
            {
                RecommendedOutfitId = recommendedId,
                Explanation = explanation,
                Outfit = MapOutfitToDto(selectedOutfit)
            };
        }
        catch (HttpRequestException)
        {
            throw new Exception("ИИ-сервис временно недоступен.");
        }
    }

    private string GenerateOutfitTextDescription(Outfit o)
    {
        var outfitCats = string.Join(", ", o.OutfitCategories.Select(c => c.Category.Name));
        
        var itemsDesc = o.OutfitItems.Select(oi => 
        {
            var itemTags = string.Join(", ", oi.Item.ItemTags.Select(t => t.Tag.Name));
            var itemCats = string.Join(", ", oi.Item.ItemCategories.Select(c => c.Category.Name));
            return $"{oi.Item.Name} (Теги: {itemTags}, Категории: {itemCats}) {oi.Item.Description}";
        });

        return $"Название: {o.Name}. Категории образа: {outfitCats}. Описание: {o.Description}. Состоит из вещей: {string.Join("; ", itemsDesc)}.";
    }


    private OutfitResponseDto MapOutfitToDto(Outfit outfit)
    {
        return new OutfitResponseDto
        {
            Id = outfit.Id,
            Name = outfit.Name,
            Description = outfit.Description,
            CanvasWidth = outfit.CanvasWidth,
            CanvasHeight = outfit.CanvasHeight,
            TemplateId = outfit.TemplateId,
            TemplateName = outfit.Template?.Name,
            Items = outfit.OutfitItems?
                .Where(oi => !oi.Item.IsDeleted)
                .Select(oi => new OutfitItemResponseDto
                {
                    Id = oi.Id,
                    ItemId = oi.ItemId,
                    ItemImageUrl = oi.Item.ProcessedImageUrl,
                    X = oi.XPosition,
                    Y = oi.YPosition,
                    Scale = oi.Scale,
                    Rotation = oi.Rotation,
                    ZIndex = oi.ZIndex
                }).ToList() ?? new List<OutfitItemResponseDto>(),
            Categories = outfit.OutfitCategories?.Select(oc => new CategoryDto
            {
                Id = oc.Category.Id,
                Name = oc.Category.Name,
                IsItemCategory = oc.Category.IsItemCategory,
                IsOutfitCategory = oc.Category.IsOutfitCategory
            }).ToList() ?? new List<CategoryDto>()
        };
    }
}