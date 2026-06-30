using System.Net;
using Microsoft.Extensions.Caching.Memory;
using Microsoft.Extensions.DependencyInjection;
using WardrobeApp.Services;
using WardrobeApp.Tests.Helpers;

namespace WardrobeApp.Tests.Services;

public class WeatherServiceTests
{
    private IMemoryCache GetMemoryCache()
    {
        var services = new ServiceCollection();
        services.AddMemoryCache();
        var provider = services.BuildServiceProvider();
        return provider.GetRequiredService<IMemoryCache>();
    }

    [Theory]
    [InlineData(25, 0, "Ясно")]    
    [InlineData(15, 61, "Дождь")]  
    [InlineData(-5, 71, "Снег")]  
    [InlineData(8, 45, "Туман")]    
    [InlineData(20, 95, "Гроза")]   
    public async Task GetWeatherByCityAsync_ParsesCodeCorrectly(double temp, int weatherCode, string expectedCondition)
    {
        var geoResponse = new { results = new[] { new { latitude = 53.9, longitude = 27.5, name = "Минск" } } };
        
        var weatherResponse = new { current_weather = new { temperature = temp, weathercode = weatherCode } };
        
        var handler = new Moq.Mock<HttpMessageHandler>();
        handler.Protected()
            .SetupSequence<Task<HttpResponseMessage>>("SendAsync", Moq.ItExpr.IsAny<HttpRequestMessage>(), Moq.ItExpr.IsAny<CancellationToken>())
            .ReturnsAsync(new HttpResponseMessage { StatusCode = HttpStatusCode.OK, Content = new StringContent(System.Text.Json.JsonSerializer.Serialize(geoResponse)) })
            .ReturnsAsync(new HttpResponseMessage { StatusCode = HttpStatusCode.OK, Content = new StringContent(System.Text.Json.JsonSerializer.Serialize(weatherResponse)) });
            
        var client = new HttpClient(handler.Object) { BaseAddress = new Uri("http://localhost") };
        var mockFactory = new Moq.Mock<IHttpClientFactory>();
        mockFactory.Setup(_ => _.CreateClient(Moq.It.IsAny<string>())).Returns(client);

        var cache = GetMemoryCache();
        var service = new WeatherService(mockFactory.Object, cache);

        var result = await service.GetWeatherByCityAsync("Минск");

        Assert.Equal(expectedCondition, result.Condition);
        Assert.Equal(temp, result.Temperature);
    }
}