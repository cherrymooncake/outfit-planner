using Microsoft.EntityFrameworkCore;
using WardrobeApp.Data;
using WardrobeApp.DTOs;
using WardrobeApp.Entities;

namespace WardrobeApp.Services;

public class TemplateService : ITemplateService
{
    private readonly AppDbContext _context;

    public TemplateService(AppDbContext context)
    {
        _context = context;
    }
    
    public async Task<TemplateResponseDto> CreateAsync(Guid userId, CreateTemplateDto dto)
    {
        await ValidateCategoryHintsAsync(userId, dto.Items);

        var template = new OutfitTemplate
        {
            Id = Guid.NewGuid(),
            UserId = userId,
            Name = dto.Name,
            Description = dto.Description,
            CreatedAt = DateTime.UtcNow,
            UpdatedAt = DateTime.UtcNow
        };

        if (dto.Items != null && dto.Items.Any())
        {
            template.TemplateItems = dto.Items.Select(i => new OutfitTemplateItem
            {
                Id = Guid.NewGuid(),
                TemplateId = template.Id,
                CategoryIdHint = i.CategoryIdHint,
                XPosition = i.X,
                YPosition = i.Y,
                Scale = i.Scale,
                Rotation = i.Rotation,
                ZIndex = i.ZIndex
            }).ToList();
        }

        _context.OutfitTemplates.Add(template);
        await _context.SaveChangesAsync();

        return await GetByIdAsync(userId, template.Id) ?? throw new Exception("Ошибка создания шаблона");
    }
    
    public async Task<List<TemplateResponseDto>> GetAllAsync(Guid userId)
    {
        var templates = await _context.OutfitTemplates
            .Include(t => t.TemplateItems)
                .ThenInclude(ti => ti.CategoryHint)
            .Where(t => t.UserId == userId)
            .OrderByDescending(t => t.CreatedAt)
            .ToListAsync();

        return templates.Select(MapToDto).ToList();
    }
    
    public async Task<TemplateResponseDto?> GetByIdAsync(Guid userId, Guid templateId)
    {
        var template = await _context.OutfitTemplates
            .Include(t => t.TemplateItems)
                .ThenInclude(ti => ti.CategoryHint)
            .FirstOrDefaultAsync(t => t.Id == templateId && t.UserId == userId);

        if (template == null) return null;

        return MapToDto(template);
    }
    
    public async Task<TemplateResponseDto?> UpdateAsync(Guid userId, Guid templateId, UpdateTemplateDto dto)
    {
        await ValidateCategoryHintsAsync(userId, dto.Items);

        var template = await _context.OutfitTemplates
            .FirstOrDefaultAsync(t => t.Id == templateId && t.UserId == userId);

        if (template == null) return null;
        
        template.Name = dto.Name;
        template.Description = dto.Description;
        template.UpdatedAt = DateTime.UtcNow;

        await _context.OutfitTemplateItems
            .Where(ti => ti.TemplateId == templateId)
            .ExecuteDeleteAsync();

        if (dto.Items != null && dto.Items.Any())
        {
            var newItems = dto.Items.Select(i => new OutfitTemplateItem
            {
                Id = Guid.NewGuid(),
                TemplateId = template.Id,
                CategoryIdHint = i.CategoryIdHint,
                XPosition = i.X,
                YPosition = i.Y,
                Scale = i.Scale,
                Rotation = i.Rotation,
                ZIndex = i.ZIndex
            }).ToList();

            _context.OutfitTemplateItems.AddRange(newItems);
        }

        await _context.SaveChangesAsync();
        return await GetByIdAsync(userId, templateId);
    }
    
    public async Task<bool> DeleteAsync(Guid userId, Guid templateId)
    {
        var template = await _context.OutfitTemplates
            .FirstOrDefaultAsync(t => t.Id == templateId && t.UserId == userId);

        if (template == null) return false;

        _context.OutfitTemplates.Remove(template);
        await _context.SaveChangesAsync();
        return true;
    }
    
    private TemplateResponseDto MapToDto(OutfitTemplate t)
    {
        return new TemplateResponseDto
        {
            Id = t.Id,
            Name = t.Name,
            Description = t.Description,
            CreatedAt = t.CreatedAt,
            UpdatedAt = t.UpdatedAt,
            Items = t.TemplateItems?.Select(ti => new TemplateItemResponseDto
            {
                Id = ti.Id,
                CategoryIdHint = ti.CategoryIdHint,
                CategoryName = ti.CategoryHint?.Name, 
                X = ti.XPosition,
                Y = ti.YPosition,
                Scale = ti.Scale,
                Rotation = ti.Rotation,
                ZIndex = ti.ZIndex
            }).ToList() ?? new List<TemplateItemResponseDto>()
        };
    }

    private async Task ValidateCategoryHintsAsync(Guid userId, List<TemplateItemDto>? items)
    {
        if (items == null) return;

        var categoryIds = items
            .Where(i => i.CategoryIdHint.HasValue)
            .Select(i => i.CategoryIdHint!.Value)
            .Distinct()
            .ToList();

        if (!categoryIds.Any()) return;

        var existingCount = await _context.Categories
            .CountAsync(c => categoryIds.Contains(c.Id) && c.UserId == userId);

        if (existingCount != categoryIds.Count)
        {
            throw new ArgumentException("Одна или несколько указанных категорий не найдены.");
        }
    }
}