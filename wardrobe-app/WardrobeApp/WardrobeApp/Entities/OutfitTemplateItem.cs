namespace WardrobeApp.Entities;

public class OutfitTemplateItem
{
    public Guid Id { get; set; }

    public Guid TemplateId { get; set; }
    public OutfitTemplate Template { get; set; }

    public Guid? CategoryIdHint { get; set; }
    public Category? CategoryHint { get; set; }

    public double XPosition { get; set; }
    public double YPosition { get; set; }
    public double Scale { get; set; }
    public double Rotation { get; set; }
    public int ZIndex { get; set; }
}