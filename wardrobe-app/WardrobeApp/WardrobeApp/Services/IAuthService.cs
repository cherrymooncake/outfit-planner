using WardrobeApp.DTOs;
using WardrobeApp.Entities;

namespace WardrobeApp.Services;

public interface IAuthService
{
    Task<(bool Success, string Message)> RegisterAsync(RegisterDto dto);
    Task<TokenResponseDto?> LoginAsync(LoginDto dto);
    Task<TokenResponseDto?> RefreshTokenAsync(string refreshToken);
    Task<(bool Success, string Message)> ChangePasswordAsync(Guid userId, ChangePasswordDto dto);
    Task<bool> DeleteAccountAsync(Guid userId);
}