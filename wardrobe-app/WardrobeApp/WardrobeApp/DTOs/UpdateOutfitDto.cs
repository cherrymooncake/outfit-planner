using System.ComponentModel.DataAnnotations;

namespace WardrobeApp.DTOs;

public class UpdateOutfitDto
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