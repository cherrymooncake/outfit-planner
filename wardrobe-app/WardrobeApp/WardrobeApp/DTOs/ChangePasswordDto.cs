using System.ComponentModel.DataAnnotations;

namespace WardrobeApp.DTOs;

public class ChangePasswordDto
{
    [Required]
    public string OldPassword { get; set; } = string.Empty;

    [Required]
    [MinLength(6, ErrorMessage = "Пароль должен быть длиннее 6 символов")]
    public string NewPassword { get; set; } = string.Empty;
}