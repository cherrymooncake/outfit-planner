namespace WardrobeApp.DTOs;

public class TagDto
{
    public Guid Id { get; set; }
    public string Name { get; set; } = string.Empty;
    public bool IsOutfitTag { get; set; }
    public bool IsItemTag { get; set; }
}