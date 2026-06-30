using System.ComponentModel.DataAnnotations;

namespace WardrobeApp.DTOs;

public class CreateCategoryDto
{
    [Required]
    public string Name { get; set; } = string.Empty;
    public bool IsOutfitCategory { get; set; }
    public bool IsItemCategory { get; set; }
}