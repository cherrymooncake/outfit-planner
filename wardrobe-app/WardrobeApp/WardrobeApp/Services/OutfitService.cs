using Microsoft.EntityFrameworkCore;
using WardrobeApp.Data;
using WardrobeApp.DTOs;
using WardrobeApp.Entities;

namespace WardrobeApp.Services;

public class OutfitService : IOutfitService
{
    private readonly AppDbContext _context;

    public OutfitService(AppDbContext context)
    {
        _context = context;
    }

    public async Task<OutfitResponseDto> CreateAsync(Guid userId, CreateOutfitDto dto)
    {
        await ValidateCategoriesAsync(userId, dto.CategoryIds);
        await ValidateItemsAsync(userId, dto.Items);

        var outfit = new Outfit
        {
            Id = Guid.NewGuid(),
            UserId = userId,
            Name = dto.Name,
            Description = dto.Description,
            CanvasWidth = dto.CanvasWidth > 0 ? dto.CanvasWidth : 800,
            CanvasHeight = dto.CanvasHeight > 0 ? dto.CanvasHeight : 600,
            TemplateId = dto.TemplateId,
            CreatedAt = DateTime.UtcNow,
            UpdatedAt = DateTime.UtcNow
        };

        if (dto.Items != null && dto.Items.Any())
        {
            outfit.OutfitItems = dto.Items.Select(itemDto => new OutfitItem
            {
                Id = Guid.NewGuid(),
                OutfitId = outfit.Id,
                ItemId = itemDto.ItemId,
                XPosition = itemDto.X,
                YPosition = itemDto.Y,
                Scale = itemDto.Scale,
                Rotation = itemDto.Rotation,
                ZIndex = itemDto.ZIndex
            }).ToList();
        }
        
        if (dto.CategoryIds != null && dto.CategoryIds.Any())
        {
            outfit.OutfitCategories = dto.CategoryIds.Select(catId => new OutfitCategory
            {
                Id = Guid.NewGuid(),
                OutfitId = outfit.Id,
                CategoryId = catId
            }).ToList();
        }

        _context.Outfits.Add(outfit);
        await _context.SaveChangesAsync();
        
        return await GetByIdAsync(userId, outfit.Id) ?? throw new Exception("Ошибка при создании");
    }
    
    public async Task<List<OutfitResponseDto>> GetAllAsync(Guid userId, OutfitFilterDto filters)
    {
        var query = _context.Outfits
            .Include(o => o.Template)
            .Include(o => o.OutfitItems)
            .ThenInclude(oi => oi.Item)
            .Include(o => o.OutfitCategories)
            .ThenInclude(oc => oc.Category)
            .Where(o => o.UserId == userId)
            .AsQueryable();
        
        if (!string.IsNullOrWhiteSpace(filters.SearchTerm))
        {
            var term = filters.SearchTerm.ToLower();
            query = query.Where(o => o.Name.ToLower().Contains(term));
        }
        
        if (filters.CategoryId.HasValue)
        {
            query = query.Where(o => o.OutfitCategories.Any(oc => oc.CategoryId == filters.CategoryId.Value));
        }
        
        var outfits = await query
            .OrderByDescending(o => o.CreatedAt)
            .AsSplitQuery()
            .ToListAsync();

        return outfits.Select(MapToDto).ToList();
    }
    
    public async Task<OutfitResponseDto?> GetByIdAsync(Guid userId, Guid outfitId)
    {
        var outfit = await _context.Outfits
            .Include(o => o.OutfitItems)
            .ThenInclude(oi => oi.Item)
            .Include(o => o.OutfitCategories)
            .ThenInclude(oc => oc.Category)
            .FirstOrDefaultAsync(o => o.Id == outfitId && o.UserId == userId);

        if (outfit == null) return null;

        return MapToDto(outfit);
    }

    public async Task<bool> DeleteAsync(Guid userId, Guid outfitId)
    {
        var outfit = await _context.Outfits.FirstOrDefaultAsync(o => o.Id == outfitId && o.UserId == userId);
        if (outfit == null) return false;

        _context.Outfits.Remove(outfit);
        await _context.SaveChangesAsync();
        return true;
    }
    
    private OutfitResponseDto MapToDto(Outfit outfit)
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
    
    
    public async Task<OutfitResponseDto?> UpdateAsync(Guid userId, Guid outfitId, UpdateOutfitDto dto)
    {
        await ValidateCategoriesAsync(userId, dto.CategoryIds);
        await ValidateItemsAsync(userId, dto.Items);
        
        var outfit = await _context.Outfits
            .FirstOrDefaultAsync(o => o.Id == outfitId && o.UserId == userId);

        if (outfit == null) return null;
        
        outfit.Name = dto.Name;
        outfit.Description = dto.Description;
        outfit.CanvasWidth = dto.CanvasWidth;
        outfit.CanvasHeight = dto.CanvasHeight;
        outfit.TemplateId = dto.TemplateId;
        outfit.UpdatedAt = DateTime.UtcNow;

        
        await _context.OutfitItems
            .Where(oi => oi.OutfitId == outfitId)
            .ExecuteDeleteAsync();

        await _context.OutfitCategories
            .Where(oc => oc.OutfitId == outfitId)
            .ExecuteDeleteAsync();
        
        if (dto.Items != null && dto.Items.Any())
        {
            var newItems = dto.Items.Select(itemDto => new OutfitItem
            {
                Id = Guid.NewGuid(),
                OutfitId = outfit.Id,
                ItemId = itemDto.ItemId,
                XPosition = itemDto.X,
                YPosition = itemDto.Y,
                Scale = itemDto.Scale,
                Rotation = itemDto.Rotation,
                ZIndex = itemDto.ZIndex
            }).ToList();

            _context.OutfitItems.AddRange(newItems);
        }
        
        if (dto.CategoryIds != null && dto.CategoryIds.Any())
        {
            var newCategories = dto.CategoryIds.Select(catId => new OutfitCategory
            {
                Id = Guid.NewGuid(),
                OutfitId = outfit.Id,
                CategoryId = catId
            }).ToList();

            _context.OutfitCategories.AddRange(newCategories);
        }

        await _context.SaveChangesAsync();
        
        return await GetByIdAsync(userId, outfit.Id);
    }
    
    private async Task ValidateCategoriesAsync(Guid userId, List<Guid>? categoryIds)
    {
        if (categoryIds != null && categoryIds.Any())
        {
            var uniqueCatIds = categoryIds.Distinct().ToList();

            var existingIds = await _context.Categories
                .Where(c => uniqueCatIds.Contains(c.Id) 
                            && c.UserId == userId 
                            && c.IsOutfitCategory)
                .Select(c => c.Id)
                .ToListAsync();

            if (existingIds.Count != uniqueCatIds.Count)
            {
                throw new ArgumentException("Категория не найдена");
            }
        }
    }
    private async Task ValidateItemsAsync(Guid userId, List<OutfitItemDto>? itemsDto)
    {
        if (itemsDto != null && itemsDto.Any())
        {
            var uniqueItemIds = itemsDto.Select(i => i.ItemId).Distinct().ToList();

            var existingIds = await _context.Items
                .Where(i => uniqueItemIds.Contains(i.Id) 
                            && i.UserId == userId 
                            && !i.IsDeleted)
                .Select(i => i.Id)
                .ToListAsync();

            if (existingIds.Count != uniqueItemIds.Count)
            {
                var missingIds = uniqueItemIds.Except(existingIds);
                throw new ArgumentException($"Одна или несколько вещей не найдены или были удалены. ID: {string.Join(", ", missingIds)}");
            }
        }
    }
}