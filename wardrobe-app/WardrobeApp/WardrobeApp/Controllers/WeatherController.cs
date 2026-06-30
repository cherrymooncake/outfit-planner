using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using WardrobeApp.Services;

namespace WardrobeApp.Controllers;

[Authorize]
[ApiController]
[Route("api/[controller]")]
public class WeatherController : ControllerBase
{
    private readonly IWeatherService _weatherService;

    public WeatherController(IWeatherService weatherService)
    {
        _weatherService = weatherService;
    }[HttpGet]
    public async Task<IActionResult> Get([FromQuery] string city)
    {
        if (string.IsNullOrWhiteSpace(city))
            return BadRequest(new { error = "Укажите город" });

        try
        {
            var weather = await _weatherService.GetWeatherByCityAsync(city);
            return Ok(weather);
        }
        catch (Exception ex)
        {
            return BadRequest(new { error = ex.Message });
        }
    }
}