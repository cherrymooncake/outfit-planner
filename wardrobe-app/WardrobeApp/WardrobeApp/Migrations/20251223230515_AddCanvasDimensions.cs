using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace WardrobeApp.Migrations
{
    /// <inheritdoc />
    public partial class AddCanvasDimensions : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.AddColumn<int>(
                name: "CanvasHeight",
                table: "Outfits",
                type: "integer",
                nullable: false,
                defaultValue: 0);

            migrationBuilder.AddColumn<int>(
                name: "CanvasWidth",
                table: "Outfits",
                type: "integer",
                nullable: false,
                defaultValue: 0);
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropColumn(
                name: "CanvasHeight",
                table: "Outfits");

            migrationBuilder.DropColumn(
                name: "CanvasWidth",
                table: "Outfits");
        }
    }
}
