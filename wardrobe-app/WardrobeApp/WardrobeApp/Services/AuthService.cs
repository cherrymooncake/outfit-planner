using System.IdentityModel.Tokens.Jwt;
using System.Security.Claims;
using System.Security.Cryptography;
using System.Text;
using Microsoft.EntityFrameworkCore;
using Microsoft.IdentityModel.Tokens;
using WardrobeApp.Data;
using WardrobeApp.DTOs;
using WardrobeApp.Entities;

namespace WardrobeApp.Services;

public class AuthService : IAuthService
{
    private readonly AppDbContext _context;
    private readonly IConfiguration _configuration;

    public AuthService(AppDbContext context, IConfiguration configuration)
    {
        _context = context;
        _configuration = configuration;
    }
    
    public async Task<(bool Success, string Message)> RegisterAsync(RegisterDto dto)
    {
        if (await _context.Users.AnyAsync(u => u.Email == dto.Email))
        {
            return (false, "Пользователь с таким email уже существует.");
        }

        var passwordHash = BCrypt.Net.BCrypt.HashPassword(dto.Password);

        var user = new User
        {
            Id = Guid.NewGuid(),
            Email = dto.Email,
            PasswordHash = passwordHash,
            Role = "User",
            LastActiveAt = DateTime.UtcNow
        };

        _context.Users.Add(user);
        await _context.SaveChangesAsync();

        return (true, "Регистрация успешна");
    }
    
    public async Task<TokenResponseDto?> LoginAsync(LoginDto dto)
    {
        var user = await _context.Users.FirstOrDefaultAsync(u => u.Email == dto.Email);
        if (user == null) return null;

        if (!BCrypt.Net.BCrypt.Verify(dto.Password, user.PasswordHash)) return null;

        
        user.LastActiveAt = DateTime.UtcNow;

        return await GenerateTokensForUser(user);
    }

    public async Task<TokenResponseDto?> RefreshTokenAsync(string token)
    {
        var storedToken = await _context.RefreshTokens
            .Include(rt => rt.User)
            .FirstOrDefaultAsync(rt => rt.Token == token);
        
        if (storedToken == null || !storedToken.IsActive)
            return null;

        storedToken.RevokedAt = DateTime.UtcNow;
        
        
        storedToken.User.LastActiveAt = DateTime.UtcNow;
        
        var newTokens = await GenerateTokensForUser(storedToken.User);
        
        storedToken.ReplacedByToken = newTokens.RefreshToken;
        
        await _context.SaveChangesAsync(); 
        
        return newTokens;
    }

    private async Task<TokenResponseDto> GenerateTokensForUser(User user)
    {
        var jwtSettings = _configuration.GetSection("JwtSettings");
        var secretKey = Encoding.UTF8.GetBytes(jwtSettings["SecretKey"]!);

        var claims = new List<Claim>
        {
            new Claim(ClaimTypes.NameIdentifier, user.Id.ToString()),
            new Claim(ClaimTypes.Email, user.Email),
            new Claim(ClaimTypes.Role, user.Role) 
        };

        var tokenDescriptor = new SecurityTokenDescriptor
        {
            Subject = new ClaimsIdentity(claims),
            Expires = DateTime.UtcNow.AddMinutes(double.Parse(jwtSettings["AccessTokenExpirationMinutes"]!)),
            SigningCredentials = new SigningCredentials(new SymmetricSecurityKey(secretKey), SecurityAlgorithms.HmacSha256Signature),
            Issuer = jwtSettings["Issuer"],
            Audience = jwtSettings["Audience"]
        };

        var tokenHandler = new JwtSecurityTokenHandler();
        var accessToken = tokenHandler.WriteToken(tokenHandler.CreateToken(tokenDescriptor));

        var refreshToken = GenerateRandomString();
        
        var refreshTokenEntity = new RefreshToken
        {
            Id = Guid.NewGuid(),
            UserId = user.Id,
            Token = refreshToken,
            ExpiresAt = DateTime.UtcNow.AddDays(double.Parse(jwtSettings["RefreshTokenExpirationDays"]!))
        };

        _context.RefreshTokens.Add(refreshTokenEntity);
        await _context.SaveChangesAsync();

        return new TokenResponseDto
        {
            AccessToken = accessToken,
            RefreshToken = refreshToken
        };
    }

    private string GenerateRandomString()
    {
        var randomNumber = new byte[32];
        using var rng = RandomNumberGenerator.Create();
        rng.GetBytes(randomNumber);
        return Convert.ToBase64String(randomNumber);
    }

    public async Task<(bool Success, string Message)> ChangePasswordAsync(Guid userId, ChangePasswordDto dto)
    {
        var user = await _context.Users.FindAsync(userId);
        if (user == null) return (false, "Пользователь не найден");

        if (!BCrypt.Net.BCrypt.Verify(dto.OldPassword, user.PasswordHash))
        {
            return (false, "Старый пароль указан неверно");
        }

        user.PasswordHash = BCrypt.Net.BCrypt.HashPassword(dto.NewPassword);

        var tokens = await _context.RefreshTokens.Where(t => t.UserId == userId).ToListAsync();
        _context.RefreshTokens.RemoveRange(tokens);

        await _context.SaveChangesAsync();
        return (true, "Пароль успешно изменен");
    }

    public async Task<bool> DeleteAccountAsync(Guid userId)
    {
        var user = await _context.Users.FindAsync(userId);
        if (user == null) return false;

        _context.Users.Remove(user);
        
        await _context.SaveChangesAsync();
        return true;
    }
}