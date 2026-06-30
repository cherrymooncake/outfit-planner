using WardrobeApp.DTOs;

namespace WardrobeApp.Services;

public interface ICategoryService
{
    Task<List<CategoryDto>> GetAllAsync(Guid userId);
    Task<CategoryDto> CreateAsync(Guid userId, CreateCategoryDto dto);
    Task<bool> DeleteAsync(Guid userId, Guid categoryId);
    Task<CategoryDto?> UpdateAsync(Guid userId, Guid categoryId, UpdateCategoryDto dto);
}