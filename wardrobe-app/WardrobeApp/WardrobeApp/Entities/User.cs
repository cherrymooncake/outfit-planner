namespace WardrobeApp.Entities;

public class User
{
    public Guid Id { get; set; }
    public string Email { get; set; } = string.Empty;
    public string PasswordHash { get; set; } = string.Empty;
    public string Salt { get; set; } = string.Empty; 
    public string Role { get; set; } = "User";
    
    public DateTime CreatedAt { get; set; } = DateTime.UtcNow;
    public DateTime UpdatedAt { get; set; } = DateTime.UtcNow;
    
    public DateTime LastActiveAt { get; set; } = DateTime.UtcNow;

    public ICollection<RefreshToken> RefreshTokens { get; set; }
    public ICollection<Item> Items { get; set; }
    public ICollection<Outfit> Outfits { get; set; }
    public ICollection<Category> Categories { get; set; }
    public ICollection<Tag> Tags { get; set; }
    public ICollection<OutfitTemplate> Templates { get; set; }
    public ICollection<DailyOutfit> DailyOutfits { get; set; }
}