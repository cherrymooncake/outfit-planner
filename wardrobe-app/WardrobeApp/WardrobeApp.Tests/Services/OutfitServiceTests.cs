using Microsoft.EntityFrameworkCore;
using WardrobeApp.Data;
using WardrobeApp.DTOs;
using WardrobeApp.Entities;
using WardrobeApp.Services;

namespace WardrobeApp.Tests.Services;

public class OutfitServiceTests
{
    private AppDbContext GetMemoryContext()
    {
        var options = new DbContextOptionsBuilder<AppDbContext>()
            .UseInMemoryDatabase(databaseName: Guid.NewGuid().ToString())
            .Options;
        return new AppDbContext(options);
    }

    [Fact]
    public async Task CreateOutfit_MissingItems_ThrowsArgumentException() 
    {
        var userId = Guid.NewGuid();
        var context = GetMemoryContext();
        var service = new OutfitService(context);
        
        var dto = new CreateOutfitDto 
        { 
            Name = "Test Outfit", 
            Items = new List<OutfitItemDto> { new OutfitItemDto { ItemId = Guid.NewGuid() } } 
        };

        await Assert.ThrowsAsync<ArgumentException>(() => service.CreateAsync(userId, dto));
    }

    [Theory]
    [InlineData("Summer", null, 1)]
    [InlineData(null, "C0000000-0000-0000-0000-000000000000", 1)]
    [InlineData("Winter", null, 0)]
    public async Task GetAllAsync_AppliesFiltersCorrectly(string? search, string? catIdStr, int expectedCount)
    {
        var userId = Guid.NewGuid();
        var catId = catIdStr != null ? Guid.Parse(catIdStr) : Guid.NewGuid();
        var context = GetMemoryContext();
        
        var outfit = new Outfit { Id = Guid.NewGuid(), UserId = userId, Name = "Summer Look" };
        if (catIdStr != null) outfit.OutfitCategories = new List<OutfitCategory> { new OutfitCategory { CategoryId = catId } };
        
        context.Outfits.Add(outfit);
        await context.SaveChangesAsync();

        var service = new OutfitService(context);
        var filters = new OutfitFilterDto { SearchTerm = search, CategoryId = catIdStr != null ? catId : null };
        
        var result = await service.GetAllAsync(userId, filters);
        
        Assert.Equal(expectedCount, result.Count);
    }

    [Fact]
    public async Task UpdateAsync_ReplacesOutfitItems()
    {
        var userId = Guid.NewGuid();
        var outfitId = Guid.NewGuid();
        var itemId = Guid.NewGuid();
        var context = GetMemoryContext();
        
        context.Items.Add(new Item { Id = itemId, UserId = userId });
        context.Outfits.Add(new Outfit 
        { 
            Id = outfitId, UserId = userId, Name = "Old Name", 
            OutfitItems = new List<OutfitItem> { new OutfitItem { ItemId = itemId } }
        });
        await context.SaveChangesAsync();

        var service = new OutfitService(context);
        var updateDto = new UpdateOutfitDto 
        { 
            Name = "New Name", 
            Items = new List<OutfitItemDto> { new OutfitItemDto { ItemId = itemId, ZIndex = 99 } } 
        };

        var result = await service.UpdateAsync(userId, outfitId, updateDto);

        Assert.NotNull(result);
        Assert.Equal("New Name", result.Name);
        Assert.Equal(99, result.Items.First().ZIndex);
    }
}