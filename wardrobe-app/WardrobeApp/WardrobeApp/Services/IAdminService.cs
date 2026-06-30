using WardrobeApp.DTOs;

namespace WardrobeApp.Services;

public interface IAdminService
{
    Task<HealthStatusDto> GetHealthStatusAsync();
    Task<GlobalStatsDto> GetGlobalStatsAsync();
    Task<List<UserStatDto>> GetUsersListAsync();
    Task<(bool Success, string Message)> ChangeUserRoleAsync(Guid targetUserId, string newRole, Guid currentAdminId);
    
    Task<byte[]> GetDatabaseBackupAsync();
    Task<byte[]> GetFilesBackupAsync();
    Task<byte[]> GetFullBackupAsync();
}