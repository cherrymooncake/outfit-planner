using System.Net;
using System.Text.Json;
using Moq;
using Moq.Protected;

namespace WardrobeApp.Tests.Helpers;

public static class MockHelper
{
    public static Mock<IHttpClientFactory> CreateHttpClientFactoryMock(HttpStatusCode statusCode, object? responseContent = null)
    {
        var mockMessageHandler = new Mock<HttpMessageHandler>();
        mockMessageHandler.Protected()
            .Setup<Task<HttpResponseMessage>>(
                "SendAsync",
                ItExpr.IsAny<HttpRequestMessage>(),
                ItExpr.IsAny<CancellationToken>()
            )
            .ReturnsAsync(new HttpResponseMessage
            {
                StatusCode = statusCode,
                Content = responseContent != null 
                    ? new StringContent(JsonSerializer.Serialize(responseContent)) 
                    : new StringContent("")
            });

        var client = new HttpClient(mockMessageHandler.Object) { BaseAddress = new Uri("http://localhost") };
        var mockFactory = new Mock<IHttpClientFactory>();
        mockFactory.Setup(_ => _.CreateClient(It.IsAny<string>())).Returns(client);

        return mockFactory;
    }
}