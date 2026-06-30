using System.ComponentModel.DataAnnotations;

namespace WardrobeApp.DTOs;

public class CreateTagDto
{
    [Required]
    public string Name { get; set; } = string.Empty;
    
    public bool IsOutfitTag { get; set; }
    public bool IsItemTag { get; set; }
}