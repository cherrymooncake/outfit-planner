namespace WardrobeApp.Entities;

public class Outfit
{
    public Guid Id { get; set; }
    
    public Guid UserId { get; set; }
    public User User { get; set; }

    public string Name { get; set; } = string.Empty;
    public string? Description { get; set; }
    
    public int CanvasWidth { get; set; } = 800; 
    public int CanvasHeight { get; set; } = 600; 

    public DateTime CreatedAt { get; set; } = DateTime.UtcNow;
    public DateTime UpdatedAt { get; set; } = DateTime.UtcNow;

    public ICollection<OutfitItem> OutfitItems { get; set; } 
    public ICollection<OutfitCategory> OutfitCategories { get; set; }
    public ICollection<OutfitTag> OutfitTags { get; set; }
    
    public Guid? TemplateId { get; set; } 
    public OutfitTemplate? Template { get; set; }
}