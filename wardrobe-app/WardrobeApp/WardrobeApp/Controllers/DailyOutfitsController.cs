using System.Security.Claims;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using WardrobeApp.DTOs;
using WardrobeApp.Services;

namespace WardrobeApp.Controllers;

[Authorize]
[ApiController][Route("api/daily-outfits")]
public class DailyOutfitsController : ControllerBase
{
    private readonly IDailyOutfitService _service;

    public DailyOutfitsController(IDailyOutfitService service)
    {
        _service = service;
    }

    [HttpGet("{date}")]
    public async Task<IActionResult> GetByDate(DateOnly date)
    {
        var userId = User.FindFirst(ClaimTypes.NameIdentifier)?.Value;
        if (userId == null) return Unauthorized();

        var dailyOutfit = await _service.GetByDateAsync(Guid.Parse(userId), date);
        
        return Ok(dailyOutfit); 
    }

    [HttpGet("month")]
    public async Task<IActionResult> GetMonth([FromQuery] int year,[FromQuery] int month)
    {
        var userId = User.FindFirst(ClaimTypes.NameIdentifier)?.Value;
        if (userId == null) return Unauthorized();

        if (year < 2000 || month < 1 || month > 12) return BadRequest("Неверный формат года или месяца");

        var dailyOutfits = await _service.GetMonthAsync(Guid.Parse(userId), year, month);
        return Ok(dailyOutfits);
    }

    [HttpPost]
    public async Task<IActionResult> SetOutfit([FromBody] SetDailyOutfitDto dto)
    {
        var userId = User.FindFirst(ClaimTypes.NameIdentifier)?.Value;
        if (userId == null) return Unauthorized();

        try
        {
            var result = await _service.SetOutfitAsync(Guid.Parse(userId), dto);
            return Ok(result);
        }
        catch (ArgumentException ex)
        {
            return BadRequest(new { error = ex.Message });
        }
    }

    [HttpDelete("{date}")]
    public async Task<IActionResult> Delete(DateOnly date)
    {
        var userId = User.FindFirst(ClaimTypes.NameIdentifier)?.Value;
        if (userId == null) return Unauthorized();

        await _service.DeleteOutfitAsync(Guid.Parse(userId), date);
        return NoContent(); 
    }

    [HttpGet("random")]
    public async Task<IActionResult> GetRandomOutfit()
    {
        var userId = User.FindFirst(ClaimTypes.NameIdentifier)?.Value;
        if (userId == null) return Unauthorized();

        var randomOutfit = await _service.GetRandomOutfitAsync(Guid.Parse(userId));
        
        if (randomOutfit == null) return NotFound(new { error = "У вас пока нет ни одного образа для выбора." });

        return Ok(randomOutfit);
    }
    
    [HttpPost("recommend")]
    public async Task<IActionResult> GetAiRecommendation([FromBody] AiRecommendationRequestDto dto)
    {
        var userId = User.FindFirst(ClaimTypes.NameIdentifier)?.Value;
        if (userId == null) return Unauthorized();

        try
        {
            var result = await _service.GetAiRecommendationAsync(Guid.Parse(userId), dto);
            return Ok(result);
        }
        catch (ArgumentException ex)
        {
            return BadRequest(new { error = ex.Message }); 
        }
        catch (Exception ex)
        {
            Console.WriteLine(ex);
            return StatusCode(500, new { error = ex.Message });
        }
    }
}