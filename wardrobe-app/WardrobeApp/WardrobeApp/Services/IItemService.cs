using WardrobeApp.DTOs;

namespace WardrobeApp.Services;

public interface IItemService
{
    Task<ItemResponseDto> CreateAsync(Guid userId, CreateItemDto dto);
    Task<List<ItemResponseDto>> GetAllAsync(Guid userId, ItemFilterDto filters);
    Task<ItemResponseDto?> UpdateAsync(Guid userId, Guid itemId, UpdateItemDto dto);
    Task<bool> DeleteAsync(Guid userId, Guid itemId);
    Task<string> ReprocessMaskAsync(Guid userId, Guid itemId, string contourJson);
    Task<string> UpdateProcessedImageAsync(Guid userId, Guid itemId, IFormFile imageFile);
    Task<string> RestoreAutoMaskAsync(Guid userId, Guid itemId);
}