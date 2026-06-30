namespace WardrobeApp.DTOs;

public class WeatherResponseDto
{
    public string City { get; set; } = string.Empty;
    public double Temperature { get; set; }
    public string Condition { get; set; } = string.Empty;
    public int WeatherCode { get; set; }
    public string Recommendation { get; set; } = string.Empty;
}