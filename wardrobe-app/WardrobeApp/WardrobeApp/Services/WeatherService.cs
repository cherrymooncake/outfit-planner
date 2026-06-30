using System.Globalization;
using System.Text.Json;
using Microsoft.Extensions.Caching.Memory;
using WardrobeApp.DTOs;

namespace WardrobeApp.Services;

public class WeatherService : IWeatherService
{
    private readonly IHttpClientFactory _httpClientFactory;
    private readonly IMemoryCache _cache;

    public WeatherService(IHttpClientFactory httpClientFactory, IMemoryCache cache)
    {
        _httpClientFactory = httpClientFactory;
        _cache = cache;
    }

    public async Task<WeatherResponseDto> GetWeatherByCityAsync(string city)
    {
        var cacheKey = $"weather_city_{city.ToLowerInvariant()}";

        if (_cache.TryGetValue(cacheKey, out WeatherResponseDto? cachedWeather) && cachedWeather != null)
        {
            return cachedWeather;
        }

        var client = _httpClientFactory.CreateClient();

        var geoUrl = $"https://geocoding-api.open-meteo.com/v1/search?name={Uri.EscapeDataString(city)}&count=1&language=ru&format=json";
        var geoResponse = await client.GetAsync(geoUrl);
        
        if (!geoResponse.IsSuccessStatusCode)
            throw new Exception("Не удалось связаться с сервисом геокодирования.");

        var geoJson = await geoResponse.Content.ReadAsStringAsync();
        using var geoDoc = JsonDocument.Parse(geoJson);

        if (!geoDoc.RootElement.TryGetProperty("results", out var results) || results.GetArrayLength() == 0)
        {
            throw new Exception($"Город '{city}' не найден.");
        }

        var location = results[0];
        var lat = location.GetProperty("latitude").GetDouble();
        var lon = location.GetProperty("longitude").GetDouble();
        var cityName = location.GetProperty("name").GetString() ?? city;

        var latStr = lat.ToString(CultureInfo.InvariantCulture);
        var lonStr = lon.ToString(CultureInfo.InvariantCulture);
        var weatherUrl = $"https://api.open-meteo.com/v1/forecast?latitude={latStr}&longitude={lonStr}&current_weather=true";

        var weatherResponse = await client.GetAsync(weatherUrl);
        if (!weatherResponse.IsSuccessStatusCode)
            throw new Exception("Не удалось получить данные о погоде.");

        var weatherJson = await weatherResponse.Content.ReadAsStringAsync();
        using var weatherDoc = JsonDocument.Parse(weatherJson);

        var currentWeather = weatherDoc.RootElement.GetProperty("current_weather");
        var temp = currentWeather.GetProperty("temperature").GetDouble();
        var weatherCode = currentWeather.GetProperty("weathercode").GetInt32();

        var (condition, recommendation) = AnalyzeWeather(temp, weatherCode);

        var result = new WeatherResponseDto
        {
            City = cityName,
            Temperature = temp,
            Condition = condition,
            WeatherCode = weatherCode,
            Recommendation = recommendation
        };

        _cache.Set(cacheKey, result, TimeSpan.FromMinutes(30));

        return result;
    }

    private (string condition, string recommendation) AnalyzeWeather(double temp, int weatherCode)
    {
        string condition = "Ясно";
        
        if (weatherCode is >= 1 and <= 3) condition = "Облачно";
        else if (weatherCode is >= 45 and <= 48) condition = "Туман";
        else if (weatherCode is >= 51 and <= 67 || weatherCode is >= 80 and <= 82) condition = "Дождь";
        else if (weatherCode is >= 71 and <= 77 || weatherCode == 85 || weatherCode == 86) condition = "Снег";
        else if (weatherCode >= 95) condition = "Гроза";

        string recommendation;
        
        if (condition == "Дождь" || condition == "Гроза")
        {
            recommendation = temp > 15 
                ? "На улице дождь. Захватите зонт, но одевайтесь не слишком тепло." 
                : "Дождливо и прохладно. Идеально подойдет плащ или куртка.";
        }
        else if (condition == "Снег")
        {
            recommendation = temp < -10 
                ? "Мороз и снег! Обязательно теплый пуховик, шапка, шарф и ботинки." 
                : "Идет снег. Наденьте зимнюю куртку и непромокаемую обувь.";
        }
        else
        {
            if (temp < 0) recommendation = "Морозно! Надевайте пуховик, теплый свитер, шапку и перчатки.";
            else if (temp < 10) recommendation = "Холодно. Подойдет демисезонная куртка, пальто или теплый свитер.";
            else if (temp < 18) recommendation = "Прохладно. Захватите ветровку, кардиган или легкую куртку.";
            else if (temp < 25) recommendation = "Отличная погода! Подойдут джинсы, футболка или легкое платье.";
            else recommendation = "Жарко! Выбирайте шорты, легкие майки и не забудьте кепку.";
        }

        return (condition, recommendation);
    }
}