namespace WardrobeApp.DTOs;

public class ItemFilterDto
{
    public string? SearchTerm { get; set; } 
    public Guid? CategoryId { get; set; } 
    public Guid? TagId { get; set; }   
}