package com.cherrymooncake.wardrobe_android.feature.common.domain.model

data class CategoryDomainModel(
    val id: String,
    val name: String,
    val isOutfitCategory: Boolean,
    val isItemCategory: Boolean
)

data class TagDomainModel(
    val id: String,
    val name: String,
    val isOutfitTag: Boolean,
    val isItemTag: Boolean
)