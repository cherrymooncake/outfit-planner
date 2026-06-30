package com.cherrymooncake.wardrobe_android.feature.templates.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "templates")
data class TemplateDbModel(
    @PrimaryKey val id: String,
    val name: String,
    val description: String?,
    val items: List<TemplateItemLocalModel>
)

data class TemplateItemLocalModel(
    val id: String,
    val categoryIdHint: String?,
    val categoryName: String?,
    val x: Float,
    val y: Float,
    val scale: Float,
    val rotation: Float,
    val zIndex: Int
)