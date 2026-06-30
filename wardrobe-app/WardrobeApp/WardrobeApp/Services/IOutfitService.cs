using WardrobeApp.DTOs;

namespace WardrobeApp.Services;

public interface IOutfitService
{
    Task<OutfitResponseDto> CreateAsync(Guid userId, CreateOutfitDto dto);
    Task<List<OutfitResponseDto>> GetAllAsync(Guid userId, OutfitFilterDto filters);
    Task<OutfitResponseDto?> GetByIdAsync(Guid userId, Guid outfitId);
    Task<bool> DeleteAsync(Guid userId, Guid outfitId);
    Task<OutfitResponseDto?> UpdateAsync(Guid userId, Guid outfitId, UpdateOutfitDto dto);
}