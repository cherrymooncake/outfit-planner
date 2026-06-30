using System.ComponentModel.DataAnnotations;

namespace WardrobeApp.DTOs;

public class UpdateTagDto
{
    [Required(ErrorMessage = "Название тега обязательно")]
    public string Name { get; set; } = string.Empty;
}