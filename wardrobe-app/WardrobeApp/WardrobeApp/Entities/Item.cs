namespace WardrobeApp.Entities;

public class Item
{
    public Guid Id { get; set; }
    
    public Guid UserId { get; set; }
    public User User { get; set; }
    
    public string Name { get; set; } = string.Empty;
    public string? Description { get; set; }
    
    public string OriginalImageUrl { get; set; } = string.Empty;
    public string ProcessedImageUrl { get; set; } = string.Empty;
    
    public bool IsDeleted { get; set; } = false; 
    
    public DateTime CreatedAt { get; set; } = DateTime.UtcNow;
    public DateTime UpdatedAt { get; set; } = DateTime.UtcNow;

    public ICollection<ItemCategory> ItemCategories { get; set; }
    public ICollection<ItemTag> ItemTags { get; set; }
    public ICollection<OutfitItem> OutfitItems { get; set; }
}