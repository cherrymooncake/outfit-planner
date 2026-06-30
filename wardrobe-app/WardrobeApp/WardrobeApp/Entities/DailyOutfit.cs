namespace WardrobeApp.Entities;

public class DailyOutfit
{
    public Guid Id { get; set; }
    
    public Guid UserId { get; set; }
    public User User { get; set; }

    public Guid OutfitId { get; set; }
    public Outfit Outfit { get; set; }

    public DateOnly Date { get; set; }

    public DateTime CreatedAt { get; set; } = DateTime.UtcNow;
    public DateTime UpdatedAt { get; set; } = DateTime.UtcNow;
}