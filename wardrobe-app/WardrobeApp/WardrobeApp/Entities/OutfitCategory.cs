namespace WardrobeApp.Entities;

public class OutfitCategory
{
    public Guid Id { get; set; }
    
    public Guid OutfitId { get; set; }
    public Outfit Outfit { get; set; }
    
    public Guid CategoryId { get; set; }
    public Category Category { get; set; }
}