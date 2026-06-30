using System.ComponentModel.DataAnnotations;

namespace WardrobeApp.DTOs;

public class CreateOutfitDto
{
    [Required]
    public string Name { get; set; } = string.Empty;
    public string? Description { get; set; }
    
    public int CanvasWidth { get; set; }
    public int CanvasHeight { get; set; }
    
    public Guid? TemplateId { get; set; }
    
    [Required]
    public List<OutfitItemDto> Items { get; set; } = new();
    
    public List<Guid>? CategoryIds { get; set; }
}

public class OutfitItemDto
{
    public Guid ItemId { get; set; }
    
    public double X { get; set; }
    public double Y { get; set; }
    public double Scale { get; set; }
    public double Rotation { get; set; }
    public int ZIndex { get; set; }
}