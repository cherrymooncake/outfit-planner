namespace WardrobeApp.Entities;

public class OutfitTemplate
{
    public Guid Id { get; set; }
    
    public Guid UserId { get; set; }
    public User User { get; set; }

    public string Name { get; set; } = string.Empty;
    public string? Description { get; set; }

    public DateTime CreatedAt { get; set; } = DateTime.UtcNow;
    public DateTime UpdatedAt { get; set; } = DateTime.UtcNow;

    public ICollection<OutfitTemplateItem> TemplateItems { get; set; }
}