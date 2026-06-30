using System.Security.Claims;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using WardrobeApp.DTOs;
using WardrobeApp.Services;

namespace WardrobeApp.Controllers;

[Authorize]
[ApiController]
[Route("api/[controller]")]
public class ItemsController : ControllerBase
{
    private readonly IItemService _service;

    public ItemsController(IItemService service)
    {
        _service = service;
    }

    [HttpPost]
    public async Task<IActionResult> Create([FromForm] CreateItemDto dto)
    {
        var userId = User.FindFirst(ClaimTypes.NameIdentifier)?.Value;
        if (userId == null) return Unauthorized();

        try 
        {
            var createdItem = await _service.CreateAsync(Guid.Parse(userId), dto);
            return Ok(createdItem);
        }
        catch (ArgumentException ex)
        {
            return BadRequest(new { error = ex.Message });
        }
        catch (Exception ex)
        {
            Console.WriteLine(ex);
            return StatusCode(500, "Ошибка сервера при создании вещи или загрузки файла");
        }
    }

    [HttpGet]
    public async Task<IActionResult> GetAll([FromQuery] ItemFilterDto filters)
    {
        var userId = User.FindFirst(ClaimTypes.NameIdentifier)?.Value;
        if (userId == null) return Unauthorized();

        var items = await _service.GetAllAsync(Guid.Parse(userId), filters);
        return Ok(items);
    }
    
    [HttpPut("{id}")]
    public async Task<IActionResult> Update(Guid id, [FromBody] UpdateItemDto dto)
    {
        var userId = User.FindFirst(ClaimTypes.NameIdentifier)?.Value;
        if (userId == null) return Unauthorized();
        try
        {
            var updatedItem = await _service.UpdateAsync(Guid.Parse(userId), id, dto);
            
            if (updatedItem == null) return NotFound("Вещь не найдена");
            
            return Ok(updatedItem);
        }
        catch (ArgumentException ex)
        {
            return BadRequest(new { error = ex.Message });
        }
        catch (Exception ex)
        {
            Console.WriteLine(ex);
            return StatusCode(500, "Ошибка сервера при обновлении");
        }
    }

    [HttpDelete("{id}")]
    public async Task<IActionResult> Delete(Guid id)
    {
        var userId = User.FindFirst(ClaimTypes.NameIdentifier)?.Value;
        if (userId == null) return Unauthorized();

        var success = await _service.DeleteAsync(Guid.Parse(userId), id);
        
        if (!success) return NotFound("Вещь не найдена");
        
        return NoContent(); 
    }
    
    [HttpPost("{id}/reprocess-mask")]
    public async Task<IActionResult> ReprocessMask(Guid id, [FromBody] ManualMaskDto dto)
    {
        var userId = User.FindFirst(ClaimTypes.NameIdentifier)?.Value;
        
        try
        {
            var newUrl = await _service.ReprocessMaskAsync(Guid.Parse(userId!), id, dto.ContourJson);
            return Ok(new { url = $"{newUrl}?t={DateTime.UtcNow.Ticks}" });
        }
        catch (Exception ex)
        {
            return BadRequest(new { error = ex.Message });
        }
    }
    
    [HttpPost("{id}/update-image")]
    public async Task<IActionResult> UpdateProcessedImage(Guid id, IFormFile file)
    {
        var userId = User.FindFirst(ClaimTypes.NameIdentifier)?.Value;
        try
        {
            var newUrl = await _service.UpdateProcessedImageAsync(Guid.Parse(userId!), id, file);
            return Ok(new { url = newUrl });
        }
        catch (Exception ex)
        {
            return BadRequest(new { error = ex.Message });
        }
    }
    
    [HttpPost("{id}/restore-auto")]
    public async Task<IActionResult> RestoreAutoMask(Guid id)
    {
        var userId = User.FindFirst(ClaimTypes.NameIdentifier)?.Value;
        try
        {
            var newUrl = await _service.RestoreAutoMaskAsync(Guid.Parse(userId!), id);
            return Ok(new { url = newUrl });
        }
        catch (Exception ex)
        {
            return BadRequest(new { error = ex.Message });
        }
    }
}