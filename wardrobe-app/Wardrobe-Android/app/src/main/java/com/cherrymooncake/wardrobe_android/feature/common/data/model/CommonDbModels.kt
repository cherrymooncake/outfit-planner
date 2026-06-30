package com.cherrymooncake.wardrobe_android.feature.common.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class CategoryDbModel(
    @PrimaryKey val id: String,
    val name: String,
    val isOutfitCategory: Boolean,
    val isItemCategory: Boolean
)

@Entity(tableName = "tags")
data class TagDbModel(
    @PrimaryKey val id: String,
    val name: String,
    val isOutfitTag: Boolean,
    val isItemTag: Boolean
)