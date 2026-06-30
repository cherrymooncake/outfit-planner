using WardrobeApp.DTOs;

namespace WardrobeApp.Services;

public interface ITagService
{
    Task<List<TagDto>> GetAllAsync(Guid userId);
    Task<TagDto> CreateAsync(Guid userId, CreateTagDto dto);
    Task<bool> DeleteAsync(Guid userId, Guid tagId);
    Task<TagDto?> UpdateAsync(Guid userId, Guid tagId, UpdateTagDto dto);
}