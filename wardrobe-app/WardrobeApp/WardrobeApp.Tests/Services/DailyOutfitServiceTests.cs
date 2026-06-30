using System.Net;
using Microsoft.EntityFrameworkCore;
using WardrobeApp.Data;
using WardrobeApp.DTOs;
using WardrobeApp.Entities;
using WardrobeApp.Services;
using WardrobeApp.Tests.Helpers;

namespace WardrobeApp.Tests.Services;

public class DailyOutfitServiceTests
{
    private AppDbContext GetMemoryContext()
    {
        var options = new DbContextOptionsBuilder<AppDbContext>()
            .UseInMemoryDatabase(databaseName: Guid.NewGuid().ToString())
            .Options;
        return new AppDbContext(options);
    }

    [Fact]
    public async Task SetOutfitAsync_CreatesNewDailyRecord()
    {
        var userId = Guid.NewGuid();
        var outfitId = Guid.NewGuid();
        var date = new DateOnly(2026, 6, 18);
        var context = GetMemoryContext();
        context.Outfits.Add(new Outfit { Id = outfitId, UserId = userId, Name = "Test" });
        await context.SaveChangesAsync();

        var service = new DailyOutfitService(context, MockHelper.CreateHttpClientFactoryMock(HttpStatusCode.OK).Object);
        var result = await service.SetOutfitAsync(userId, new SetDailyOutfitDto { Date = date, OutfitId = outfitId });

        Assert.Equal(date, result.Date);
        Assert.Single(context.DailyOutfits);
    }

    [Fact]
    public async Task GetAiRecommendationAsync_CallsPythonServiceAndReturnsResult()
    {
        var userId = Guid.NewGuid();
        var outfitId = Guid.NewGuid();
        var context = GetMemoryContext();
        context.Outfits.Add(new Outfit { Id = outfitId, UserId = userId, Name = "Perfect Outfit" });
        await context.SaveChangesAsync();
        
        var pythonResponse = new { recommended_outfit_id = outfitId.ToString(), explanation = "Потому что лето!" };
        var mockHttp = MockHelper.CreateHttpClientFactoryMock(HttpStatusCode.OK, pythonResponse);
        
        var service = new DailyOutfitService(context, mockHttp.Object);
        
        var request = new AiRecommendationRequestDto { Prompt = "Что надеть?", WeatherContext = "Жарко" };
        var result = await service.GetAiRecommendationAsync(userId, request);

        Assert.Equal(outfitId, result.RecommendedOutfitId);
        Assert.Equal("Потому что лето!", result.Explanation);
    }

    [Fact]
    public async Task GetAiRecommendationAsync_PythonServiceFails_ThrowsException()
    {
        var userId = Guid.NewGuid();
        var context = GetMemoryContext();
        context.Outfits.Add(new Outfit { Id = Guid.NewGuid(), UserId = userId, Name = "1" });
        await context.SaveChangesAsync();

        var mockHttp = MockHelper.CreateHttpClientFactoryMock(HttpStatusCode.InternalServerError);
        var service = new DailyOutfitService(context, mockHttp.Object);
        
        await Assert.ThrowsAsync<Exception>(() => 
            service.GetAiRecommendationAsync(userId, new AiRecommendationRequestDto { Prompt = "Test" }));
    }
}