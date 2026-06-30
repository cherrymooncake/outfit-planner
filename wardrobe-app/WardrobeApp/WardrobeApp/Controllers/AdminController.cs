using System.Security.Claims;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using WardrobeApp.DTOs;
using WardrobeApp.Services;

namespace WardrobeApp.Controllers;

[Authorize(Roles = "Admin")]
[ApiController]
[Route("api/[controller]")]
public class AdminController : ControllerBase
{
    private readonly IAdminService _adminService;

    public AdminController(IAdminService adminService)
    {
        _adminService = adminService;
    }[HttpGet("health")]
    public async Task<IActionResult> GetHealthStatus()
    {
        var status = await _adminService.GetHealthStatusAsync();
        return Ok(status);
    }

    [HttpGet("stats/global")]
    public async Task<IActionResult> GetGlobalStats()
    {
        var stats = await _adminService.GetGlobalStatsAsync();
        return Ok(stats);
    }

    [HttpGet("users")]
    public async Task<IActionResult> GetUsersList()
    {
        var users = await _adminService.GetUsersListAsync();
        return Ok(users);
    }[HttpPut("users/{id}/role")]
    public async Task<IActionResult> ChangeUserRole(Guid id, [FromBody] ChangeRoleDto dto)
    {
        var currentAdminIdString = User.FindFirst(ClaimTypes.NameIdentifier)?.Value;
        if (string.IsNullOrEmpty(currentAdminIdString)) return Unauthorized();

        var (success, message) = await _adminService.ChangeUserRoleAsync(id, dto.NewRole, Guid.Parse(currentAdminIdString));

        if (!success) return BadRequest(new { error = message });

        return Ok(new { message });
    }

    [HttpGet("backup/files")]
    public async Task<IActionResult> DownloadFilesBackup()
    {
        var bytes = await _adminService.GetFilesBackupAsync();
        var fileName = $"images_backup_{DateTime.UtcNow:yyyyMMdd_HHmm}.zip";
        return File(bytes, "application/zip", fileName);
    }

    [HttpGet("backup/db")]
    public async Task<IActionResult> DownloadDatabaseBackup()
    {
        try
        {
            var bytes = await _adminService.GetDatabaseBackupAsync();
            var fileName = $"db_backup_{DateTime.UtcNow:yyyyMMdd_HHmm}.sql";
            return File(bytes, "application/octet-stream", fileName);
        }
        catch (Exception ex)
        {
            return StatusCode(500, new { error = "Ошибка создания дампа БД. Убедитесь, что pg_dump установлен в контейнере.", details = ex.Message });
        }
    }

    [HttpGet("backup/full")]
    public async Task<IActionResult> DownloadFullBackup()
    {
        try
        {
            var bytes = await _adminService.GetFullBackupAsync();
            var fileName = $"wardrobe_full_backup_{DateTime.UtcNow:yyyyMMdd_HHmm}.zip";
            return File(bytes, "application/zip", fileName);
        }
        catch (Exception ex)
        {
            return StatusCode(500, new { error = "Ошибка создания полного бэкапа.", details = ex.Message });
        }
    }
}