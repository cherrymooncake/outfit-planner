namespace WardrobeApp.DTOs;

public class OutfitFilterDto
{
    public string? SearchTerm { get; set; }
    
    public Guid? CategoryId { get; set; }
}