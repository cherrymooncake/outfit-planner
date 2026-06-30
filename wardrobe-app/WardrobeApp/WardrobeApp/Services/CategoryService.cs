using Microsoft.EntityFrameworkCore;
using WardrobeApp.Data;
using WardrobeApp.DTOs;
using WardrobeApp.Entities;

namespace WardrobeApp.Services;

public class CategoryService : ICategoryService
{
    private readonly AppDbContext _context;

    public CategoryService(AppDbContext context)
    {
        _context = context;
    }

    public async Task<List<CategoryDto>> GetAllAsync(Guid userId)
    {
        var categories = await _context.Categories
            .Where(c => c.UserId == userId)
            .ToListAsync();

        return categories.Select(c => new CategoryDto
        {
            Id = c.Id,
            Name = c.Name,
            IsItemCategory = c.IsItemCategory,
            IsOutfitCategory = c.IsOutfitCategory
        }).ToList();
    }

    public async Task<CategoryDto> CreateAsync(Guid userId, CreateCategoryDto dto)
    {
        var category = new Category
        {
            Id = Guid.NewGuid(),
            UserId = userId,
            Name = dto.Name,
            IsItemCategory = dto.IsItemCategory,
            IsOutfitCategory = dto.IsOutfitCategory
        };

        _context.Categories.Add(category);
        await _context.SaveChangesAsync();

        return new CategoryDto
        {
            Id = category.Id,
            Name = category.Name,
            IsItemCategory = category.IsItemCategory,
            IsOutfitCategory = category.IsOutfitCategory
        };
    }

    public async Task<bool> DeleteAsync(Guid userId, Guid categoryId)
    {
        var category = await _context.Categories
            .FirstOrDefaultAsync(c => c.Id == categoryId && c.UserId == userId);

        if (category == null) return false;

        _context.Categories.Remove(category);
        await _context.SaveChangesAsync();
        return true;
    }
    
    public async Task<CategoryDto?> UpdateAsync(Guid userId, Guid categoryId, UpdateCategoryDto dto)
    {
        var category = await _context.Categories
            .FirstOrDefaultAsync(c => c.Id == categoryId && c.UserId == userId);

        if (category == null) return null;
        
        category.Name = dto.Name;
        category.UpdatedAt = DateTime.UtcNow;

        await _context.SaveChangesAsync();

        return new CategoryDto
        {
            Id = category.Id,
            Name = category.Name,
            IsItemCategory = category.IsItemCategory,
            IsOutfitCategory = category.IsOutfitCategory
        };
    }
}