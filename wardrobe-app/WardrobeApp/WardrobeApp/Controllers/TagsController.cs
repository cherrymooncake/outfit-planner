using System.Security.Claims;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using WardrobeApp.DTOs;
using WardrobeApp.Services;

namespace WardrobeApp.Controllers;

[Authorize]
[ApiController]
[Route("api/[controller]")]
public class TagsController : ControllerBase
{
    private readonly ITagService _service;

    public TagsController(ITagService service)
    {
        _service = service;
    }
    
    [HttpGet]
    public async Task<IActionResult> GetAll()
    {
        var userIdString = User.FindFirst(ClaimTypes.NameIdentifier)?.Value;
        if (string.IsNullOrEmpty(userIdString)) return Unauthorized();

        var tags = await _service.GetAllAsync(Guid.Parse(userIdString));
        return Ok(tags);
    }
    
    [HttpPost]
    public async Task<IActionResult> Create([FromBody] CreateTagDto dto)
    {
        var userIdString = User.FindFirst(ClaimTypes.NameIdentifier)?.Value;
        if (string.IsNullOrEmpty(userIdString)) return Unauthorized();

        var createdTag = await _service.CreateAsync(Guid.Parse(userIdString), dto);
        return Ok(createdTag);
    }

    [HttpDelete("{id}")]
    public async Task<IActionResult> Delete(Guid id)
    {
        var userIdString = User.FindFirst(ClaimTypes.NameIdentifier)?.Value;
        if (string.IsNullOrEmpty(userIdString)) return Unauthorized();

        var success = await _service.DeleteAsync(Guid.Parse(userIdString), id);
        
        if (!success) return NotFound("Тег не найден");
        
        return NoContent(); 
    }
    
    [HttpPut("{id}")]
    public async Task<IActionResult> Update(Guid id, [FromBody] UpdateTagDto dto)
    {
        var userIdString = User.FindFirst(ClaimTypes.NameIdentifier)?.Value;
        if (string.IsNullOrEmpty(userIdString)) return Unauthorized();

        var updatedTag = await _service.UpdateAsync(Guid.Parse(userIdString), id, dto);

        if (updatedTag == null) return NotFound("Тег не найден");

        return Ok(updatedTag);
    }
}