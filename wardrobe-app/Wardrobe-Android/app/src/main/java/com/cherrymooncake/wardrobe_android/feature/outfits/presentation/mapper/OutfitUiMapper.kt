package com.cherrymooncake.wardrobe_android.feature.outfits.presentation.mapper

import com.cherrymooncake.wardrobe_android.feature.outfits.domain.model.OutfitDomainModel
import com.cherrymooncake.wardrobe_android.feature.outfits.domain.model.OutfitItemDomainModel
import com.cherrymooncake.wardrobe_android.feature.outfits.presentation.model.OutfitItemUiModel
import com.cherrymooncake.wardrobe_android.feature.outfits.presentation.model.OutfitUiModel
import com.cherrymooncake.wardrobe_android.feature.wardrobe.presentation.model.CategoryUiModel

private const val IMG_BASE_URL = "http://localhost:5202"

fun OutfitItemDomainModel.toUi() = OutfitItemUiModel(
    id = id,
    itemId = itemId,
    imageUrl = "$IMG_BASE_URL/${itemImageUrl.removePrefix("/")}",
    x = x, y = y, scale = scale, rotation = rotation, zIndex = zIndex,
    isSlot = false,
    categoryIdHint = null,
    categoryName = null
)

fun OutfitDomainModel.toUi() = OutfitUiModel(
    id = id,
    name = name,
    description = description,
    canvasWidth = canvasWidth,
    canvasHeight = canvasHeight,
    items = items.map { it.toUi() },
    categories = categories.map { CategoryUiModel(it.id, it.name) }
)