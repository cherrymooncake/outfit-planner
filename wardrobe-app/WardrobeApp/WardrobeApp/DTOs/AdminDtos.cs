using System.ComponentModel.DataAnnotations;

namespace WardrobeApp.DTOs;

public class HealthStatusDto
{
    public string DbStatus { get; set; } = string.Empty;
    public string BgRemovalStatus { get; set; } = string.Empty; 
    public string AiStylistStatus { get; set; } = string.Empty; 
}

public class GlobalStatsDto
{
    public int TotalUsers { get; set; }
    public int TotalItems { get; set; }
    public int TotalOutfits { get; set; }
    public double ImagesFolderSizeMb { get; set; }
}

public class UserStatDto
{
    public Guid Id { get; set; }
    public string Email { get; set; } = string.Empty;
    public string Role { get; set; } = string.Empty;
    public DateTime RegisteredAt { get; set; }
    public DateTime LastActiveAt { get; set; }
    public int ItemsCount { get; set; }
    public int OutfitsCount { get; set; }
}

public class ChangeRoleDto
{
    [Required]
    public string NewRole { get; set; } = string.Empty; 
}