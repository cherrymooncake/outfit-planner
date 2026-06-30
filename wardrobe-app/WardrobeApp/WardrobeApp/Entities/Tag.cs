namespace WardrobeApp.Entities;

public class Tag
{
    public Guid Id { get; set; }
    
    public Guid UserId { get; set; }
    public User User { get; set; }

    public string Name { get; set; } = string.Empty;
    
    public bool IsOutfitTag { get; set; }
    public bool IsItemTag { get; set; }

    public DateTime CreatedAt { get; set; } = DateTime.UtcNow;
    public DateTime UpdatedAt { get; set; } = DateTime.UtcNow;

    public ICollection<ItemTag> ItemTags { get; set; }
    public ICollection<OutfitTag> OutfitTags { get; set; }
}