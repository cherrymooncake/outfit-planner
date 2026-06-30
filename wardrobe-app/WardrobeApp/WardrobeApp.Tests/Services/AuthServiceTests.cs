using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Configuration;
using Moq;
using WardrobeApp.Data;
using WardrobeApp.DTOs;
using WardrobeApp.Entities;
using WardrobeApp.Services;

namespace WardrobeApp.Tests.Services;

public class AuthServiceTests
{
    private AppDbContext GetMemoryContext()
    {
        var options = new DbContextOptionsBuilder<AppDbContext>()
            .UseInMemoryDatabase(databaseName: Guid.NewGuid().ToString())
            .Options;
        return new AppDbContext(options);
    }

    private IConfiguration GetMockConfiguration()
    {
        var mockConfig = new Mock<IConfiguration>();
        var mockConfSection = new Mock<IConfigurationSection>();
        mockConfSection.SetupGet(m => m[It.Is<string>(s => s == "SecretKey")]).Returns("SuperSecretKeyThatIsAtLeast32BytesLong!");
        mockConfSection.SetupGet(m => m[It.Is<string>(s => s == "AccessTokenExpirationMinutes")]).Returns("60");
        mockConfSection.SetupGet(m => m[It.Is<string>(s => s == "RefreshTokenExpirationDays")]).Returns("7");
        mockConfSection.SetupGet(m => m[It.Is<string>(s => s == "Issuer")]).Returns("TestIssuer");
        mockConfSection.SetupGet(m => m[It.Is<string>(s => s == "Audience")]).Returns("TestAudience");
        mockConfig.Setup(a => a.GetSection("JwtSettings")).Returns(mockConfSection.Object);
        return mockConfig.Object;
    }

    [Fact]
    public async Task RegisterAsync_ValidData_ReturnsSuccess() // 1 тест
    {
        var context = GetMemoryContext();
        var service = new AuthService(context, GetMockConfiguration());

        var result = await service.RegisterAsync(new RegisterDto { Email = "test@mail.com", Password = "password123" });

        Assert.True(result.Success);
        Assert.Single(context.Users);
        Assert.NotEqual("password123", context.Users.First().PasswordHash); // Пароль должен быть захеширован
    }

    [Fact]
    public async Task RegisterAsync_DuplicateEmail_ReturnsError() // 2 тест
    {
        var context = GetMemoryContext();
        context.Users.Add(new User { Id = Guid.NewGuid(), Email = "test@mail.com", PasswordHash = "hash" });
        await context.SaveChangesAsync();
        var service = new AuthService(context, GetMockConfiguration());

        var result = await service.RegisterAsync(new RegisterDto { Email = "test@mail.com", Password = "newpassword" });

        Assert.False(result.Success);
        Assert.Equal("Пользователь с таким email уже существует.", result.Message);
    }

    [Theory]
    [InlineData("test@mail.com", "wrongpass", false)]
    [InlineData("wrong@mail.com", "password123", false)]
    [InlineData("test@mail.com", "password123", true)]
    [InlineData("TEST@MAIL.COM", "password123", false)] // Case sensitive проверка, если реализована, иначе упадет (настраивается в БД)
    public async Task LoginAsync_VariousCredentials_ReturnsExpectedResult(string email, string password, bool expectedSuccess)
    {
        var context = GetMemoryContext();
        context.Users.Add(new User 
        { 
            Id = Guid.NewGuid(), 
            Email = "test@mail.com", 
            PasswordHash = BCrypt.Net.BCrypt.HashPassword("password123") 
        });
        await context.SaveChangesAsync();
        var service = new AuthService(context, GetMockConfiguration());

        var result = await service.LoginAsync(new LoginDto { Email = email, Password = password });

        if (expectedSuccess) Assert.NotNull(result);
        else Assert.Null(result);
    }

    [Fact]
    public async Task ChangePasswordAsync_ValidOldPassword_ChangesPassword() // 1 тест
    {
        var userId = Guid.NewGuid();
        var context = GetMemoryContext();
        context.Users.Add(new User { Id = userId, Email = "u@m.com", PasswordHash = BCrypt.Net.BCrypt.HashPassword("oldPass") });
        await context.SaveChangesAsync();
        var service = new AuthService(context, GetMockConfiguration());

        var result = await service.ChangePasswordAsync(userId, new ChangePasswordDto { OldPassword = "oldPass", NewPassword = "newPass" });

        Assert.True(result.Success);
        Assert.True(BCrypt.Net.BCrypt.Verify("newPass", context.Users.First().PasswordHash));
    }
}