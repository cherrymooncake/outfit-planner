namespace WardrobeApp.Entities;

public class Category
{
    public Guid Id { get; set; }
    
    public Guid UserId { get; set; }
    public User User { get; set; }

    public string Name { get; set; } = string.Empty;
    
    public bool IsOutfitCategory { get; set; }
    public bool IsItemCategory { get; set; }

    public DateTime CreatedAt { get; set; } = DateTime.UtcNow;
    public DateTime UpdatedAt { get; set; } = DateTime.UtcNow;

    public ICollection<ItemCategory> ItemCategories { get; set; }
    public ICollection<OutfitCategory> OutfitCategories { get; set; }
}