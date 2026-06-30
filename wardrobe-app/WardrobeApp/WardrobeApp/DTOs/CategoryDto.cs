namespace WardrobeApp.DTOs;

public class CategoryDto
{
    public Guid Id { get; set; }
    public string Name { get; set; } = string.Empty;
    public bool IsOutfitCategory { get; set; }
    public bool IsItemCategory { get; set; }
}