package com.cherrymooncake.wardrobe_android.feature.wardrobe.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.cherrymooncake.wardrobe_android.feature.common.data.model.CategoryDbModel
import com.cherrymooncake.wardrobe_android.feature.common.data.model.TagDbModel

@Entity(tableName = "items")
data class ItemDbModel(
    @PrimaryKey val id: String,
    val name: String,
    val description: String?,
    val originalImageUrl: String,
    val processedImageUrl: String,
    val categories: List<CategoryDbModel>,
    val tags: List<TagDbModel>
)