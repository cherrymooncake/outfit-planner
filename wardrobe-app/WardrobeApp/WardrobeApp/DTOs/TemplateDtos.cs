using System.ComponentModel.DataAnnotations;

namespace WardrobeApp.DTOs;


public class CreateTemplateDto
{
    [Required]
    public string Name { get; set; } = string.Empty;
    public string? Description { get; set; }

    [Required]
    public List<TemplateItemDto> Items { get; set; } = new();
}

public class TemplateItemDto
{
    public Guid? CategoryIdHint { get; set; }

    public double X { get; set; }
    public double Y { get; set; }
    public double Scale { get; set; }
    public double Rotation { get; set; }
    public int ZIndex { get; set; }
}

public class UpdateTemplateDto
{
    [Required]
    public string Name { get; set; } = string.Empty;
    public string? Description { get; set; }

    [Required]
    public List<TemplateItemDto> Items { get; set; } = new();
}

public class TemplateResponseDto
{
    public Guid Id { get; set; }
    public string Name { get; set; } = string.Empty;
    public string? Description { get; set; }
    
    public DateTime CreatedAt { get; set; }
    public DateTime UpdatedAt { get; set; }

    public List<TemplateItemResponseDto> Items { get; set; } = new();
}

public class TemplateItemResponseDto
{
    public Guid Id { get; set; }

    public Guid? CategoryIdHint { get; set; }
    public string? CategoryName { get; set; }

    public double X { get; set; }
    public double Y { get; set; }
    public double Scale { get; set; }
    public double Rotation { get; set; }
    public int ZIndex { get; set; }
}