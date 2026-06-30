using System.ComponentModel.DataAnnotations;

namespace WardrobeApp.DTOs;

public class UpdateItemDto
{
    [Required]
    public string Name { get; set; } = string.Empty;
    
    public string? Description { get; set; }

    public List<Guid>? CategoryIds { get; set; }
    public List<Guid>? TagIds { get; set; }
}