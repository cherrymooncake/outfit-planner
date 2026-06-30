namespace WardrobeApp.DTOs;

public class ItemResponseDto
{
    public Guid Id { get; set; }
    public string Name { get; set; } = string.Empty;
    public string? Description { get; set; }
    
    public string OriginalImageUrl { get; set; } = string.Empty;
    public string ProcessedImageUrl { get; set; } = string.Empty;
    
    public List<CategoryDto> Categories { get; set; } = new();
    public List<TagDto> Tags { get; set; } = new();
}