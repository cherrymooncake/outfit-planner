using System;
using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace WardrobeApp.Migrations
{
    /// <inheritdoc />
    public partial class AddTemplateLinkToOutfit : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.AddColumn<Guid>(
                name: "TemplateId",
                table: "Outfits",
                type: "uuid",
                nullable: true);

            migrationBuilder.CreateIndex(
                name: "IX_Outfits_TemplateId",
                table: "Outfits",
                column: "TemplateId");

            migrationBuilder.AddForeignKey(
                name: "FK_Outfits_OutfitTemplates_TemplateId",
                table: "Outfits",
                column: "TemplateId",
                principalTable: "OutfitTemplates",
                principalColumn: "Id");
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_Outfits_OutfitTemplates_TemplateId",
                table: "Outfits");

            migrationBuilder.DropIndex(
                name: "IX_Outfits_TemplateId",
                table: "Outfits");

            migrationBuilder.DropColumn(
                name: "TemplateId",
                table: "Outfits");
        }
    }
}
