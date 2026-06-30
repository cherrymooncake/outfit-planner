using Microsoft.EntityFrameworkCore;
using WardrobeApp.Data;
using WardrobeApp.DTOs;
using WardrobeApp.Entities;

namespace WardrobeApp.Services;

public class TagService : ITagService
{
    private readonly AppDbContext _context;

    public TagService(AppDbContext context)
    {
        _context = context;
    }

    public async Task<List<TagDto>> GetAllAsync(Guid userId)
    {
        var tags = await _context.Tags
            .Where(t => t.UserId == userId)
            .ToListAsync();

        return tags.Select(t => new TagDto
        {
            Id = t.Id,
            Name = t.Name,
            IsItemTag = t.IsItemTag,
            IsOutfitTag = t.IsOutfitTag
        }).ToList();
    }

    public async Task<TagDto> CreateAsync(Guid userId, CreateTagDto dto)
    {
        var tag = new Tag
        {
            Id = Guid.NewGuid(),
            UserId = userId,
            Name = dto.Name,
            IsItemTag = dto.IsItemTag,
            IsOutfitTag = dto.IsOutfitTag,
            CreatedAt = DateTime.UtcNow
        };

        _context.Tags.Add(tag);
        await _context.SaveChangesAsync();

        return new TagDto
        {
            Id = tag.Id,
            Name = tag.Name,
            IsItemTag = tag.IsItemTag,
            IsOutfitTag = tag.IsOutfitTag
        };
    }

    public async Task<bool> DeleteAsync(Guid userId, Guid tagId)
    {
        var tag = await _context.Tags
            .FirstOrDefaultAsync(t => t.Id == tagId && t.UserId == userId);

        if (tag == null) return false;

        _context.Tags.Remove(tag);
        await _context.SaveChangesAsync();
        return true;
    }
    
    public async Task<TagDto?> UpdateAsync(Guid userId, Guid tagId, UpdateTagDto dto)
    {
        var tag = await _context.Tags
            .FirstOrDefaultAsync(t => t.Id == tagId && t.UserId == userId);

        if (tag == null) return null;
        
        tag.Name = dto.Name;
        tag.UpdatedAt = DateTime.UtcNow;

        await _context.SaveChangesAsync();

        return new TagDto
        {
            Id = tag.Id,
            Name = tag.Name,
            IsItemTag = tag.IsItemTag,
            IsOutfitTag = tag.IsOutfitTag
        };
    }
}