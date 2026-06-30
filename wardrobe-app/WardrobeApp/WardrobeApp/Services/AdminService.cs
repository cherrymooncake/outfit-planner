using System.Diagnostics;
using System.IO.Compression;
using Microsoft.EntityFrameworkCore;
using Npgsql;
using WardrobeApp.Data;
using WardrobeApp.DTOs;

namespace WardrobeApp.Services;

public class AdminService : IAdminService
{
    private readonly AppDbContext _context;
    private readonly IWebHostEnvironment _env;
    private readonly IHttpClientFactory _httpClientFactory;
    private readonly IConfiguration _configuration;

    public AdminService(AppDbContext context, IWebHostEnvironment env, IHttpClientFactory httpClientFactory, IConfiguration configuration)
    {
        _context = context;
        _env = env;
        _httpClientFactory = httpClientFactory;
        _configuration = configuration;
    }

    public async Task<HealthStatusDto> GetHealthStatusAsync()
    {
        var status = new HealthStatusDto 
        { 
            DbStatus = "Error", 
            BgRemovalStatus = "Error", 
            AiStylistStatus = "Error" 
        };

        if (await _context.Database.CanConnectAsync())
        {
            status.DbStatus = "Ok";
        }

        var client = _httpClientFactory.CreateClient();
        client.Timeout = TimeSpan.FromSeconds(3);

        
        try
        {
            var response = await client.GetAsync("http://localhost:8000/docs"); 
            if (response.IsSuccessStatusCode)
            {
                status.BgRemovalStatus = "Ok";
            }
        }
        catch
        {
            status.BgRemovalStatus = "Unreachable";
        }

        
        try
        {
            var response2 = await client.GetAsync("http://localhost:8001/docs"); 
            if (response2.IsSuccessStatusCode)
            {
                status.AiStylistStatus = "Ok";
            }
        }
        catch
        {
            status.AiStylistStatus = "Unreachable";
        }

        return status;
    }

    public async Task<GlobalStatsDto> GetGlobalStatsAsync()
    {
        var imagesPath = Path.Combine(_env.WebRootPath, "images");
        double sizeMb = 0;

        if (Directory.Exists(imagesPath))
        {
            long sizeBytes = Directory.GetFiles(imagesPath, "*", SearchOption.AllDirectories)
                                      .Sum(t => new FileInfo(t).Length);
            sizeMb = Math.Round(sizeBytes / 1024.0 / 1024.0, 2);
        }

        return new GlobalStatsDto
        {
            TotalUsers = await _context.Users.CountAsync(),
            TotalItems = await _context.Items.CountAsync(i => !i.IsDeleted),
            TotalOutfits = await _context.Outfits.CountAsync(),
            ImagesFolderSizeMb = sizeMb
        };
    }

    public async Task<List<UserStatDto>> GetUsersListAsync()
    {
        return await _context.Users
            .Select(u => new UserStatDto
            {
                Id = u.Id,
                Email = u.Email,
                Role = u.Role,
                RegisteredAt = u.CreatedAt,
                LastActiveAt = u.LastActiveAt,
                ItemsCount = u.Items.Count(i => !i.IsDeleted),
                OutfitsCount = u.Outfits.Count
            })
            .OrderByDescending(u => u.LastActiveAt)
            .ToListAsync();
    }

    public async Task<(bool Success, string Message)> ChangeUserRoleAsync(Guid targetUserId, string newRole, Guid currentAdminId)
    {
        if (targetUserId == currentAdminId)
        {
            return (false, "Вы не можете изменить роль самому себе.");
        }

        if (newRole != "Admin" && newRole != "User")
        {
            return (false, "Недопустимая роль. Доступно: Admin, User.");
        }

        var user = await _context.Users.FindAsync(targetUserId);
        if (user == null) return (false, "Пользователь не найден.");

        user.Role = newRole;
        
        var tokens = await _context.RefreshTokens.Where(t => t.UserId == targetUserId).ToListAsync();
        _context.RefreshTokens.RemoveRange(tokens);

        await _context.SaveChangesAsync();
        return (true, $"Роль пользователя успешно изменена на {newRole}");
    }

    public async Task<byte[]> GetFilesBackupAsync()
    {
        var imagesPath = Path.Combine(_env.WebRootPath, "images");
        if (!Directory.Exists(imagesPath))
        {
            Directory.CreateDirectory(imagesPath);
        }

        var tempZipPath = Path.GetTempFileName();
        if (File.Exists(tempZipPath)) File.Delete(tempZipPath);

        ZipFile.CreateFromDirectory(imagesPath, tempZipPath);
        
        var bytes = await File.ReadAllBytesAsync(tempZipPath);
        File.Delete(tempZipPath);
        
        return bytes;
    }

    public async Task<byte[]> GetDatabaseBackupAsync()
    {
        var connString = _configuration.GetConnectionString("DefaultConnection");
        var builder = new NpgsqlConnectionStringBuilder(connString);

        var tempSqlPath = Path.GetTempFileName() + ".sql";
        
        var processInfo = new ProcessStartInfo
        {
            FileName = "pg_dump",
            Arguments = $"-h {builder.Host} -p {builder.Port} -U {builder.Username} -F p -f \"{tempSqlPath}\" {builder.Database}",
            RedirectStandardOutput = true,
            RedirectStandardError = true,
            UseShellExecute = false,
            CreateNoWindow = true
        };

        processInfo.EnvironmentVariables["PGPASSWORD"] = builder.Password;

        using var process = Process.Start(processInfo);
        if (process != null)
        {
            await process.WaitForExitAsync();
            if (process.ExitCode != 0)
            {
                var error = await process.StandardError.ReadToEndAsync();
                throw new Exception($"pg_dump failed: {error}");
            }
        }

        var bytes = await File.ReadAllBytesAsync(tempSqlPath);
        File.Delete(tempSqlPath);

        return bytes;
    }

    public async Task<byte[]> GetFullBackupAsync()
    {
        var tempDir = Path.Combine(Path.GetTempPath(), Guid.NewGuid().ToString());
        Directory.CreateDirectory(tempDir);

        try
        {
            var dbBytes = await GetDatabaseBackupAsync();
            await File.WriteAllBytesAsync(Path.Combine(tempDir, "database.sql"), dbBytes);

            var imagesSourcePath = Path.Combine(_env.WebRootPath, "images");
            var imagesDestPath = Path.Combine(tempDir, "images");
            
            if (Directory.Exists(imagesSourcePath))
            {
                CopyDirectory(imagesSourcePath, imagesDestPath);
            }

            var tempZipPath = Path.GetTempFileName();
            if (File.Exists(tempZipPath)) File.Delete(tempZipPath);

            ZipFile.CreateFromDirectory(tempDir, tempZipPath);

            var bytes = await File.ReadAllBytesAsync(tempZipPath);
            File.Delete(tempZipPath);

            return bytes;
        }
        finally
        {
            if (Directory.Exists(tempDir))
            {
                Directory.Delete(tempDir, true);
            }
        }
    }

    private static void CopyDirectory(string sourceDir, string destinationDir)
    {
        var dir = new DirectoryInfo(sourceDir);
        Directory.CreateDirectory(destinationDir);

        foreach (var file in dir.GetFiles())
        {
            file.CopyTo(Path.Combine(destinationDir, file.Name), true);
        }

        foreach (var subDir in dir.GetDirectories())
        {
            CopyDirectory(subDir.FullName, Path.Combine(destinationDir, subDir.Name));
        }
    }
}