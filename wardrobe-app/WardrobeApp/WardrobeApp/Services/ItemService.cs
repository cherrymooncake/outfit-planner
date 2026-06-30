using Microsoft.EntityFrameworkCore;
using WardrobeApp.Data;
using WardrobeApp.DTOs;
using WardrobeApp.Entities;

namespace WardrobeApp.Services;

public class ItemService : IItemService
{
    private readonly AppDbContext _context;
    private readonly IWebHostEnvironment _environment;
    
    private readonly IHttpClientFactory _httpClientFactory; 
    
    public ItemService(AppDbContext context, IWebHostEnvironment environment, IHttpClientFactory httpClientFactory)
    {
        _context = context;
        _environment = environment;
        _httpClientFactory = httpClientFactory;
    }

    public async Task<ItemResponseDto> CreateAsync(Guid userId, CreateItemDto dto)
    {
        await ValidateCategoriesAndTagsAsync(userId, dto.CategoryIds, dto.TagIds);
        
        var uniqueFileName = $"{Guid.NewGuid()}{Path.GetExtension(dto.Image.FileName)}";
        
        var processedFileName = $"{Guid.NewGuid()}.png"; 

        var uploadsFolder = Path.Combine(_environment.WebRootPath, "images");
        
        var originalsPath = Path.Combine(uploadsFolder, "originals", uniqueFileName);
        var processedPath = Path.Combine(uploadsFolder, "processed", processedFileName);
        
        Directory.CreateDirectory(Path.GetDirectoryName(originalsPath)!);
        Directory.CreateDirectory(Path.GetDirectoryName(processedPath)!);
        
        using (var stream = new FileStream(originalsPath, FileMode.Create))
        {
            await dto.Image.CopyToAsync(stream);
        }

        try 
        {
            var client = _httpClientFactory.CreateClient();
            
            using var fileStream = File.OpenRead(originalsPath);
            using var content = new MultipartFormDataContent();
            
            var fileContent = new StreamContent(fileStream);
            
            var extension = Path.GetExtension(uniqueFileName).ToLower();
            var contentType = extension == ".png" ? "image/png" : "image/jpeg";
            fileContent.Headers.ContentType = new System.Net.Http.Headers.MediaTypeHeaderValue(contentType);
            
            content.Add(fileContent, "file", uniqueFileName);
            
            var response = await client.PostAsync("http://localhost:8000/remove-background", content);
            
            
            if (response.IsSuccessStatusCode)
            {
                using var responseStream = await response.Content.ReadAsStreamAsync();
                using var fileStreamProcessed = new FileStream(processedPath, FileMode.Create);
                await responseStream.CopyToAsync(fileStreamProcessed);
            }
            else
            {
                var errorBody = await response.Content.ReadAsStringAsync();
                Console.WriteLine($"Python Error: {response.StatusCode} - {errorBody}");
                
                File.Copy(originalsPath, processedPath, true);
            }
        }
        catch (Exception ex)
        {
            Console.WriteLine($"Ошибка соединения с CV сервисом: {ex.Message}");
            File.Copy(originalsPath, processedPath, true);
        }
        
        var originalUrl = $"/images/originals/{uniqueFileName}";
        var processedUrl = $"/images/processed/{processedFileName}";
        
        var item = new Item
        {
            Id = Guid.NewGuid(),
            UserId = userId,
            Name = dto.Name,
            Description = dto.Description,
            OriginalImageUrl = originalUrl,
            ProcessedImageUrl = processedUrl,
            CreatedAt = DateTime.UtcNow,
            UpdatedAt = DateTime.UtcNow
        };
        
        if (dto.CategoryIds != null && dto.CategoryIds.Any())
        {
            item.ItemCategories = dto.CategoryIds.Select(catId => new ItemCategory
            {
                ItemId = item.Id,
                CategoryId = catId
            }).ToList();
        }
        
        if (dto.TagIds != null && dto.TagIds.Any())
        {
            item.ItemTags = dto.TagIds.Select(tagId => new ItemTag
            {
                ItemId = item.Id,
                TagId = tagId
            }).ToList();
        }

        _context.Items.Add(item);
        await _context.SaveChangesAsync();
        
        var itemWithIncludes = await _context.Items
            .Include(i => i.ItemCategories).ThenInclude(ic => ic.Category)
            .Include(i => i.ItemTags).ThenInclude(it => it.Tag)
            .FirstOrDefaultAsync(i => i.Id == item.Id);

        return MapToDto(itemWithIncludes!);
    }
    
    public async Task<List<ItemResponseDto>> GetAllAsync(Guid userId, ItemFilterDto filters)
    {
        var query = _context.Items
            .Include(i => i.ItemCategories).ThenInclude(ic => ic.Category)
            .Include(i => i.ItemTags).ThenInclude(it => it.Tag)
            .Where(i => i.UserId == userId && !i.IsDeleted)
            .AsQueryable();
        if (!string.IsNullOrWhiteSpace(filters.SearchTerm))
        {
            var term = filters.SearchTerm.ToLower();
            query = query.Where(i => i.Name.ToLower().Contains(term));
        }
        if (filters.CategoryId.HasValue)
        {
            query = query.Where(i => i.ItemCategories.Any(ic => ic.CategoryId == filters.CategoryId.Value));
        }
        if (filters.TagId.HasValue)
        {
            query = query.Where(i => i.ItemTags.Any(it => it.TagId == filters.TagId.Value));
        }
        var items = await query
            .OrderByDescending(i => i.CreatedAt)
            .AsSplitQuery()
            .ToListAsync();

        return items.Select(MapToDto).ToList();
    }

    public async Task<ItemResponseDto?> UpdateAsync(Guid userId, Guid itemId, UpdateItemDto dto)
    {
        await ValidateCategoriesAndTagsAsync(userId, dto.CategoryIds, dto.TagIds);

        var item = await _context.Items
            .Include(i => i.ItemCategories)
            .Include(i => i.ItemTags)
            .FirstOrDefaultAsync(i => i.Id == itemId && i.UserId == userId && !i.IsDeleted);

        if (item == null) return null;

        item.Name = dto.Name;
        item.Description = dto.Description;
        item.UpdatedAt = DateTime.UtcNow;
        if (dto.CategoryIds != null)
        {
            _context.ItemCategories.RemoveRange(item.ItemCategories); 
            item.ItemCategories = dto.CategoryIds.Select(catId => new ItemCategory
            {
                ItemId = item.Id,
                CategoryId = catId
            }).ToList();
        }
        if (dto.TagIds != null)
        {
            _context.ItemTags.RemoveRange(item.ItemTags); 
            item.ItemTags = dto.TagIds.Select(tagId => new ItemTag
            {
                ItemId = item.Id,
                TagId = tagId
            }).ToList();
        }
        await _context.SaveChangesAsync();
        var updatedItem = await _context.Items
             .Include(i => i.ItemCategories).ThenInclude(ic => ic.Category)
             .Include(i => i.ItemTags).ThenInclude(it => it.Tag)
             .FirstAsync(i => i.Id == itemId);

        return MapToDto(updatedItem);
    }
    
    public async Task<bool> DeleteAsync(Guid userId, Guid itemId)
    {
        var item = await _context.Items.FirstOrDefaultAsync(i => i.Id == itemId && i.UserId == userId);
        if (item == null || item.IsDeleted) return false;
        DeleteFile(item.OriginalImageUrl);
        DeleteFile(item.ProcessedImageUrl);
        item.OriginalImageUrl = ""; 
        item.ProcessedImageUrl = "";
        item.IsDeleted = true;
        
        await _context.SaveChangesAsync();
        return true;
    }

    private void DeleteFile(string relativeUrl)
    {
        if (string.IsNullOrEmpty(relativeUrl)) return;

        var relativePath = relativeUrl.TrimStart('/', '\\');
        var fullPath = Path.Combine(_environment.WebRootPath, relativePath);

        if (File.Exists(fullPath))
        {
            try
            {
                File.Delete(fullPath);
            }
            catch (Exception ex)
            {
                Console.WriteLine($"Не удалось удалить файл {fullPath}: {ex.Message}");
            }
        }
    }
    
    private ItemResponseDto MapToDto(Item item)
    {
        return new ItemResponseDto
        {
            Id = item.Id,
            Name = item.Name,
            Description = item.Description,
            OriginalImageUrl = item.OriginalImageUrl,
            
            ProcessedImageUrl = $"{item.ProcessedImageUrl}?v={item.UpdatedAt.Ticks}",
        
            Categories = item.ItemCategories?.Select(ic => new CategoryDto
            {
                Id = ic.Category.Id,
                Name = ic.Category.Name,
                IsItemCategory = ic.Category.IsItemCategory,
                IsOutfitCategory = ic.Category.IsOutfitCategory
            }).ToList() ?? new List<CategoryDto>(),
        
            Tags = item.ItemTags?.Select(it => new TagDto
            {
                Id = it.Tag.Id,
                Name = it.Tag.Name
            }).ToList() ?? new List<TagDto>()
        };
    }
    
    private async Task ValidateCategoriesAndTagsAsync(Guid userId, List<Guid>? categoryIds, List<Guid>? tagIds)
    {
        if (categoryIds != null && categoryIds.Any())
        {
            var uniqueCatIds = categoryIds.Distinct().ToList();
            var existingIds = await _context.Categories
                .Where(c => uniqueCatIds.Contains(c.Id) 
                            && c.UserId == userId 
                            && c.IsItemCategory)
                .Select(c => c.Id)
                .ToListAsync();

            if (existingIds.Count != uniqueCatIds.Count)
            {
                throw new ArgumentException("Одна или несколько категорий не найдены, не принадлежат пользователю или НЕ ЯВЛЯЮТСЯ категориями ВЕЩЕЙ.");
            }
        }
        if (tagIds != null && tagIds.Any())
        {
            var uniqueTagIds = tagIds.Distinct().ToList();
            var existingIds = await _context.Tags
                .Where(t => uniqueTagIds.Contains(t.Id) 
                            && t.UserId == userId 
                            && t.IsItemTag)
                .Select(t => t.Id)
                .ToListAsync();

            if (existingIds.Count != uniqueTagIds.Count)
            {
                throw new ArgumentException("Один или несколько тегов не найдены или НЕ ЯВЛЯЮТСЯ тегами ВЕЩЕЙ.");
            }
        }
    }
    
    public async Task<string> ReprocessMaskAsync(Guid userId, Guid itemId, string contourJson)
    {
        var item = await _context.Items.FirstOrDefaultAsync(i => i.Id == itemId && i.UserId == userId);
        if (item == null) throw new Exception("Вещь не найдена");

        
        var relativePath = item.OriginalImageUrl.TrimStart('/', '\\');
        var originalPhysicalPath = Path.Combine(_environment.WebRootPath, relativePath);

        if (!File.Exists(originalPhysicalPath)) throw new FileNotFoundException("Оригинал изображения не найден на сервере");

        var processedRelativePath = item.ProcessedImageUrl.TrimStart('/', '\\');
        var processedPhysicalPath = Path.Combine(_environment.WebRootPath, processedRelativePath);

        try
        {
            var client = _httpClientFactory.CreateClient();
            
            using var multipartContent = new MultipartFormDataContent();

            var fileStream = File.OpenRead(originalPhysicalPath);
            var fileContent = new StreamContent(fileStream);
            fileContent.Headers.ContentType = new System.Net.Http.Headers.MediaTypeHeaderValue("image/jpeg");
            multipartContent.Add(fileContent, "file", Path.GetFileName(originalPhysicalPath));

            multipartContent.Add(new StringContent(contourJson), "contour_json");

            var response = await client.PostAsync("http://localhost:8000/apply-manual-mask", multipartContent);

            if (!response.IsSuccessStatusCode)
            {
                var error = await response.Content.ReadAsStringAsync();
                throw new Exception($"Python Error: {error}");
            }

            using var responseStream = await response.Content.ReadAsStreamAsync();
            using var fs = new FileStream(processedPhysicalPath, FileMode.Create);
            await responseStream.CopyToAsync(fs);
            
            item.UpdatedAt = DateTime.UtcNow;
            await _context.SaveChangesAsync();

            return item.ProcessedImageUrl;
        }
        catch (Exception ex)
        {
            Console.WriteLine(ex.Message);
            throw;
        }
    }
    
    public async Task<string> UpdateProcessedImageAsync(Guid userId, Guid itemId, IFormFile imageFile)
    {
        var item = await _context.Items.FirstOrDefaultAsync(i => i.Id == itemId && i.UserId == userId);
        if (item == null) throw new Exception("Вещь не найдена");

        var relativePath = item.ProcessedImageUrl.TrimStart('/', '\\').Split('?')[0]; 
        var fullPath = Path.Combine(_environment.WebRootPath, relativePath);

        using (var stream = new FileStream(fullPath, FileMode.Create))
        {
            await imageFile.CopyToAsync(stream);
        }

        item.UpdatedAt = DateTime.UtcNow;
        await _context.SaveChangesAsync();

        return $"{item.ProcessedImageUrl}?t={DateTime.UtcNow.Ticks}";
    }
    
    
    private async Task<bool> ProcessImageWithPythonAsync(string inputPath, string outputPath)
    {
        try
        {
            var client = _httpClientFactory.CreateClient();
            using var fileStream = File.OpenRead(inputPath);
            using var content = new MultipartFormDataContent();
            var fileContent = new StreamContent(fileStream);
            
            var extension = Path.GetExtension(inputPath).ToLower();
            var contentType = extension == ".png" ? "image/png" : "image/jpeg";
            fileContent.Headers.ContentType = new System.Net.Http.Headers.MediaTypeHeaderValue(contentType);
            
            content.Add(fileContent, "file", Path.GetFileName(inputPath));
            
            var response = await client.PostAsync("http://localhost:8000/remove-background", content);

            if (response.IsSuccessStatusCode)
            {
                using var responseStream = await response.Content.ReadAsStreamAsync();
                using var fileStreamProcessed = new FileStream(outputPath, FileMode.Create);
                await responseStream.CopyToAsync(fileStreamProcessed);
                return true;
            }
            else
            {
                Console.WriteLine($"Python Error: {response.StatusCode}");
                return false;
            }
        }
        catch (Exception ex)
        {
            Console.WriteLine($"Exception calling Python: {ex.Message}");
            return false;
        }
    }
    
    public async Task<string> RestoreAutoMaskAsync(Guid userId, Guid itemId)
    {
        var item = await _context.Items.FirstOrDefaultAsync(i => i.Id == itemId && i.UserId == userId);
        if (item == null) throw new Exception("Вещь не найдена");

        var originalPath = Path.Combine(_environment.WebRootPath, item.OriginalImageUrl.TrimStart('/', '\\'));
        var processedPath = Path.Combine(_environment.WebRootPath, item.ProcessedImageUrl.TrimStart('/', '\\').Split('?')[0]);

        if (!File.Exists(originalPath)) throw new Exception("Оригинал изображения отсутствует");

        var success = await ProcessImageWithPythonAsync(originalPath, processedPath);

        if (!success)
        {
            File.Copy(originalPath, processedPath, true);
        }

        item.UpdatedAt = DateTime.UtcNow;
        await _context.SaveChangesAsync();

        return $"{item.ProcessedImageUrl}?t={DateTime.UtcNow.Ticks}";
    }
    
}