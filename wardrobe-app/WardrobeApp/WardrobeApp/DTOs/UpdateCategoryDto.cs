using System.ComponentModel.DataAnnotations;

namespace WardrobeApp.DTOs;

public class UpdateCategoryDto
{
    [Required(ErrorMessage = "Название категории обязательно")]
    public string Name { get; set; } = string.Empty;
}