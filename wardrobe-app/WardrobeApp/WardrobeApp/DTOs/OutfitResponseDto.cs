namespace WardrobeApp.DTOs;

public class OutfitResponseDto
{
    public Guid Id { get; set; }
    public string Name { get; set; } = string.Empty;
    public string? Description { get; set; }
    
    public int CanvasWidth { get; set; }
    public int CanvasHeight { get; set; }
    
    public Guid? TemplateId { get; set; }
    public string? TemplateName { get; set; }
    
    public List<OutfitItemResponseDto> Items { get; set; } = new();
    
    public List<CategoryDto> Categories { get; set; } = new();
}

public class OutfitItemResponseDto
{
    public Guid Id { get; set; } 
    public Guid ItemId { get; set; }
    
    public string ItemImageUrl { get; set; } 
    
    public double X { get; set; }
    public double Y { get; set; }
    public double Scale { get; set; }
    public double Rotation { get; set; }
    public int ZIndex { get; set; }
}