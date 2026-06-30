using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace WardrobeApp.Migrations
{
    /// <inheritdoc />
    public partial class FixTemplateDeleteBehavior : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_Outfits_OutfitTemplates_TemplateId",
                table: "Outfits");

            migrationBuilder.AddForeignKey(
                name: "FK_Outfits_OutfitTemplates_TemplateId",
                table: "Outfits",
                column: "TemplateId",
                principalTable: "OutfitTemplates",
                principalColumn: "Id",
                onDelete: ReferentialAction.SetNull);
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_Outfits_OutfitTemplates_TemplateId",
                table: "Outfits");

            migrationBuilder.AddForeignKey(
                name: "FK_Outfits_OutfitTemplates_TemplateId",
                table: "Outfits",
                column: "TemplateId",
                principalTable: "OutfitTemplates",
                principalColumn: "Id");
        }
    }
}
