namespace WardrobeApp.Data;

using Microsoft.EntityFrameworkCore;
using WardrobeApp.Entities;

public class AppDbContext : DbContext
{
    public AppDbContext(DbContextOptions<AppDbContext> options) : base(options)
    {
    }

    public DbSet<User> Users { get; set; }
    public DbSet<RefreshToken> RefreshTokens { get; set; }
    
    public DbSet<Category> Categories { get; set; }
    public DbSet<Tag> Tags { get; set; }
    
    public DbSet<Item> Items { get; set; }
    public DbSet<ItemCategory> ItemCategories { get; set; }
    public DbSet<ItemTag> ItemTags { get; set; }
    
    public DbSet<Outfit> Outfits { get; set; }
    public DbSet<OutfitItem> OutfitItems { get; set; }
    public DbSet<OutfitCategory> OutfitCategories { get; set; }
    public DbSet<OutfitTag> OutfitTags { get; set; }
    
    public DbSet<OutfitTemplate> OutfitTemplates { get; set; }
    public DbSet<OutfitTemplateItem> OutfitTemplateItems { get; set; }
    
    public DbSet<DailyOutfit> DailyOutfits { get; set; }

    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        base.OnModelCreating(modelBuilder);

        modelBuilder.Entity<ItemTag>()
            .HasOne(it => it.Item)
            .WithMany(i => i.ItemTags)
            .HasForeignKey(it => it.ItemId)
            .OnDelete(DeleteBehavior.Cascade); 

        modelBuilder.Entity<ItemTag>()
            .HasOne(it => it.Tag)
            .WithMany(t => t.ItemTags)
            .HasForeignKey(it => it.TagId)
            .OnDelete(DeleteBehavior.Cascade); 

        modelBuilder.Entity<ItemCategory>()
            .HasOne(ic => ic.Item)
            .WithMany(i => i.ItemCategories)
            .HasForeignKey(ic => ic.ItemId)
            .OnDelete(DeleteBehavior.Cascade);

        modelBuilder.Entity<ItemCategory>()
            .HasOne(ic => ic.Category)
            .WithMany(c => c.ItemCategories)
            .HasForeignKey(ic => ic.CategoryId)
            .OnDelete(DeleteBehavior.Cascade);


        modelBuilder.Entity<OutfitItem>()
            .HasOne(oi => oi.Outfit)
            .WithMany(o => o.OutfitItems)
            .HasForeignKey(oi => oi.OutfitId)
            .OnDelete(DeleteBehavior.Cascade); 

        modelBuilder.Entity<OutfitItem>()
            .HasOne(oi => oi.Item)
            .WithMany(i => i.OutfitItems)
            .HasForeignKey(oi => oi.ItemId)
            .OnDelete(DeleteBehavior.Restrict); 

        modelBuilder.Entity<OutfitCategory>()
            .HasOne(oc => oc.Outfit)
            .WithMany(o => o.OutfitCategories)
            .HasForeignKey(oc => oc.OutfitId)
            .OnDelete(DeleteBehavior.Cascade);

        modelBuilder.Entity<OutfitCategory>()
            .HasOne(oc => oc.Category)
            .WithMany(c => c.OutfitCategories)
            .HasForeignKey(oc => oc.CategoryId)
            .OnDelete(DeleteBehavior.Cascade);
            
        modelBuilder.Entity<OutfitTag>()
            .HasOne(ot => ot.Outfit)
            .WithMany(o => o.OutfitTags)
            .HasForeignKey(ot => ot.OutfitId)
            .OnDelete(DeleteBehavior.Cascade);

        modelBuilder.Entity<OutfitTag>()
            .HasOne(ot => ot.Tag)
            .WithMany(t => t.OutfitTags)
            .HasForeignKey(ot => ot.TagId)
            .OnDelete(DeleteBehavior.Cascade);


        modelBuilder.Entity<OutfitTemplateItem>()
            .HasOne(ti => ti.Template)
            .WithMany(t => t.TemplateItems)
            .HasForeignKey(ti => ti.TemplateId)
            .OnDelete(DeleteBehavior.Cascade);

        modelBuilder.Entity<OutfitTemplateItem>()
            .HasOne(ti => ti.CategoryHint)
            .WithMany() 
            .HasForeignKey(ti => ti.CategoryIdHint)
            .OnDelete(DeleteBehavior.SetNull); 

        modelBuilder.Entity<Outfit>()
            .HasOne(o => o.Template)
            .WithMany()
            .HasForeignKey(o => o.TemplateId)
            .OnDelete(DeleteBehavior.SetNull);
        
        modelBuilder.Entity<DailyOutfit>()
            .HasIndex(d => new { d.UserId, d.Date })
            .IsUnique();

        modelBuilder.Entity<DailyOutfit>()
            .HasOne(d => d.User)
            .WithMany(u => u.DailyOutfits)
            .HasForeignKey(d => d.UserId)
            .OnDelete(DeleteBehavior.Cascade);

        modelBuilder.Entity<DailyOutfit>()
            .HasOne(d => d.Outfit)
            .WithMany()
            .HasForeignKey(d => d.OutfitId)
            .OnDelete(DeleteBehavior.Cascade);
    }
}