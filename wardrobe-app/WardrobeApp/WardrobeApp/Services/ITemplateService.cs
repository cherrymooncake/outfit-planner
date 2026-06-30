using WardrobeApp.DTOs;

namespace WardrobeApp.Services;

public interface ITemplateService
{
    Task<TemplateResponseDto> CreateAsync(Guid userId, CreateTemplateDto dto);
    Task<List<TemplateResponseDto>> GetAllAsync(Guid userId);
    Task<TemplateResponseDto?> GetByIdAsync(Guid userId, Guid templateId);
    Task<TemplateResponseDto?> UpdateAsync(Guid userId, Guid templateId, UpdateTemplateDto dto);
    Task<bool> DeleteAsync(Guid userId, Guid templateId);
}