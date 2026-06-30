using WardrobeApp.DTOs;

namespace WardrobeApp.Services;

public interface IWeatherService
{
    Task<WeatherResponseDto> GetWeatherByCityAsync(string city);
}