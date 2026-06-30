package com.cherrymooncake.wardrobe_android.feature.outfits.presentation.model

import com.cherrymooncake.wardrobe_android.feature.wardrobe.presentation.model.CategoryUiModel

data class OutfitItemUiModel(
    val id: String,
    val itemId: String? = null,
    val imageUrl: String? = null,
    val x: Float,
    val y: Float,
    val scale: Float,
    val rotation: Float,
    val zIndex: Int,

    val isSlot: Boolean = false,
    val categoryIdHint: String? = null,
    val categoryName: String? = null
)

data class OutfitUiModel(
    val id: String,
    val name: String,
    val description: String?,
    val canvasWidth: Int,
    val canvasHeight: Int,
    val items: List<OutfitItemUiModel>,
    val categories: List<CategoryUiModel>
)