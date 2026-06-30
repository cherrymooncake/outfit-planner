namespace WardrobeApp.Entities;

public class OutfitTag
{
    public Guid Id { get; set; }
    
    public Guid OutfitId { get; set; }
    public Outfit Outfit { get; set; }
    
    public Guid TagId { get; set; }
    public Tag Tag { get; set; }
}