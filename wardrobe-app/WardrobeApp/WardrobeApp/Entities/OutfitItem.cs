namespace WardrobeApp.Entities;

public class OutfitItem
{
    public Guid Id { get; set; }

    public Guid OutfitId { get; set; }
    public Outfit Outfit { get; set; }

    public Guid ItemId { get; set; }
    public Item Item { get; set; }

    public double XPosition { get; set; }
    public double YPosition { get; set; }
    public double Scale { get; set; } = 1.0;
    public double Rotation { get; set; } = 0.0;
    public int ZIndex { get; set; }
}