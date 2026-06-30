using System.Security.Claims;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using WardrobeApp.DTOs;
using WardrobeApp.Services;

namespace WardrobeApp.Controllers;

[Authorize]
[ApiController]
[Route("api/[controller]")]
public class OutfitsController : ControllerBase
{
    private readonly IOutfitService _service;

    public OutfitsController(IOutfitService service)
    {
        _service = service;
    }

    [HttpPost]
    public async Task<IActionResult> Create([FromBody] CreateOutfitDto dto)
    {
        var userId = User.FindFirst(ClaimTypes.NameIdentifier)?.Value;
        if (userId == null) return Unauthorized();

        try 
        {
            var createdOutfit = await _service.CreateAsync(Guid.Parse(userId), dto);
            return Ok(createdOutfit);
        }
        catch (ArgumentException ex)
        {
            return BadRequest(new { error = ex.Message });
        }
        catch (Exception ex)
        {
            Console.WriteLine(ex); // Логируем
            return StatusCode(500, "Ошибка сервера");
        }
    }
    
    [HttpGet]
    public async Task<IActionResult> GetAll([FromQuery] OutfitFilterDto filters)
    {
        var userId = User.FindFirst(ClaimTypes.NameIdentifier)?.Value;
        if (userId == null) return Unauthorized();

        var outfits = await _service.GetAllAsync(Guid.Parse(userId), filters);
        return Ok(outfits);
    }
    
    [HttpGet("{id}")]
    public async Task<IActionResult> GetById(Guid id)
    {
        var userId = User.FindFirst(ClaimTypes.NameIdentifier)?.Value;
        if (userId == null) return Unauthorized();

        var outfit = await _service.GetByIdAsync(Guid.Parse(userId), id);
        if (outfit == null) return NotFound();
        
        return Ok(outfit);
    }
    
    [HttpPut("{id}")]
    public async Task<IActionResult> Update(Guid id, [FromBody] UpdateOutfitDto dto)
    {
        var userId = User.FindFirst(ClaimTypes.NameIdentifier)?.Value;
        if (userId == null) return Unauthorized();

        try
        {
            var updatedOutfit = await _service.UpdateAsync(Guid.Parse(userId), id, dto);

            if (updatedOutfit == null) return NotFound("Образ не найден");

            return Ok(updatedOutfit);
        }
        catch (ArgumentException ex)
        {
            return BadRequest(new { error = ex.Message });
        }
        catch (Exception ex)
        {
            Console.WriteLine(ex);
            return StatusCode(500, "Ошибка сервера при обновлении образа");
        }
    }

    [HttpDelete("{id}")]
    public async Task<IActionResult> Delete(Guid id)
    {
        var userId = User.FindFirst(ClaimTypes.NameIdentifier)?.Value;
        if (userId == null) return Unauthorized();

        var success = await _service.DeleteAsync(Guid.Parse(userId), id);
        if (!success) return NotFound();

        return NoContent();
    }
}