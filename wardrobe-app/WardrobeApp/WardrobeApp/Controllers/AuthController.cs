using Microsoft.AspNetCore.Mvc;
using WardrobeApp.DTOs; // Проверь namespace
using WardrobeApp.Services;
using System.Security.Claims;
using Microsoft.AspNetCore.Authorization;

namespace WardrobeApp.Controllers;

[ApiController]
[Route("api/[controller]")]
public class AuthController : ControllerBase
{
    private readonly IAuthService _authService;

    public AuthController(IAuthService authService)
    {
        _authService = authService;
    }

    [HttpPost("register")]
    public async Task<IActionResult> Register([FromBody] RegisterDto dto)
    {
        if (!ModelState.IsValid)
            return BadRequest(ModelState);

        var (success, message) = await _authService.RegisterAsync(dto);

        if (!success)
        {
            return BadRequest(new { error = message });
        }

        return Ok(new { message = "Регистрация успешна! Теперь вы можете войти." });
    }

    [HttpPost("login")]
    public async Task<IActionResult> Login([FromBody] LoginDto dto)
    {
        var tokens = await _authService.LoginAsync(dto);

        if (tokens == null)
        {
            return Unauthorized(new { error = "Неверный email или пароль" });
        }

        return Ok(tokens); 
    }

    [HttpPost("refresh-token")]
    public async Task<IActionResult> RefreshToken([FromBody] RefreshTokenRequestDto dto)
    {
        var tokens = await _authService.RefreshTokenAsync(dto.RefreshToken);

        if (tokens == null)
        {
            return Unauthorized(new { error = "Невалидный или истекший Refresh Token" });
        }

        return Ok(tokens);
    }
    
    [Authorize] 
    [HttpPost("change-password")]
    public async Task<IActionResult> ChangePassword([FromBody] ChangePasswordDto dto)
    {
        var userId = User.FindFirst(ClaimTypes.NameIdentifier)?.Value;
        if (userId == null) return Unauthorized();

        var (success, message) = await _authService.ChangePasswordAsync(Guid.Parse(userId), dto);

        if (!success)
        {
            return BadRequest(new { error = message });
        }

        return Ok(new { message = "Пароль успешно изменен. Пожалуйста, войдите снова." });
    }

    [Authorize] 
    [HttpDelete("account")]
    public async Task<IActionResult> DeleteAccount()
    {
        var userId = User.FindFirst(ClaimTypes.NameIdentifier)?.Value;
        if (userId == null) return Unauthorized();

        var success = await _authService.DeleteAccountAsync(Guid.Parse(userId));

        if (!success)
        {
            return NotFound("Пользователь не найден");
        }

        return Ok(new { message = "Аккаунт и все данные успешно удалены." });
    }
}