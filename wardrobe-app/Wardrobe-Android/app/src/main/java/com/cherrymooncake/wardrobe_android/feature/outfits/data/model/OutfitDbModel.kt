package com.cherrymooncake.wardrobe_android.feature.outfits.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.cherrymooncake.wardrobe_android.feature.common.data.model.CategoryDbModel

@Entity(tableName = "outfits")
data class OutfitDbModel(
    @PrimaryKey val id: String,
    val name: String,
    val description: String?,
    val canvasWidth: Int,
    val canvasHeight: Int,
    val templateId: String?,
    val templateName: String?,
    val items: List<OutfitItemLocalModel>,
    val categories: List<CategoryDbModel>
)

data class OutfitItemLocalModel(
    val id: String,
    val itemId: String,
    val itemImageUrl: String,
    val x: Float,
    val y: Float,
    val scale: Float,
    val rotation: Float,
    val zIndex: Int
)