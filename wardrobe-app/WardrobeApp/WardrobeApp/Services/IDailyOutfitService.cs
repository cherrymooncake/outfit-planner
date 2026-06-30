using WardrobeApp.DTOs;

namespace WardrobeApp.Services;

public interface IDailyOutfitService
{
    Task<DailyOutfitDto?> GetByDateAsync(Guid userId, DateOnly date);
    Task<List<DailyOutfitDto>> GetMonthAsync(Guid userId, int year, int month);
    Task<DailyOutfitDto> SetOutfitAsync(Guid userId, SetDailyOutfitDto dto);
    Task<bool> DeleteOutfitAsync(Guid userId, DateOnly date);
    Task<OutfitResponseDto?> GetRandomOutfitAsync(Guid userId);
    Task<AiRecommendationResponseDto> GetAiRecommendationAsync(Guid userId, AiRecommendationRequestDto request);
}