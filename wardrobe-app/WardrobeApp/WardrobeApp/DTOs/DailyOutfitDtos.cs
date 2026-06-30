using System.ComponentModel.DataAnnotations;

namespace WardrobeApp.DTOs;

public class DailyOutfitDto
{
    public DateOnly Date { get; set; }
    public OutfitResponseDto? Outfit { get; set; } 
}

public class SetDailyOutfitDto
{
    [Required]
    public DateOnly Date { get; set; }
    
    [Required]
    public Guid OutfitId { get; set; }
}

public class AiRecommendationRequestDto
{
    public string Prompt { get; set; } = string.Empty;
    public string? WeatherContext { get; set; }
}

public class AiRecommendationResponseDto
{
    public Guid RecommendedOutfitId { get; set; }
    public string Explanation { get; set; } = string.Empty;
    public OutfitResponseDto? Outfit { get; set; }
}