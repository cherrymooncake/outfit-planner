package com.cherrymooncake.wardrobe_android.feature.wardrobe.presentation.model

data class CategoryUiModel(
    val id: String,
    val name: String
)

data class TagUiModel(
    val id: String,
    val name: String
)

data class ItemUiModel(
    val id: String,
    val name: String,
    val description: String?,
    val imageUrl: String,
    val categories: List<CategoryUiModel>,
    val tags: List<TagUiModel>
)