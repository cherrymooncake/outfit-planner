package com.cherrymooncake.wardrobe_android.feature.outfits.domain.model

import com.cherrymooncake.wardrobe_android.feature.common.domain.model.CategoryDomainModel

data class OutfitItemDomainModel(
    val id: String,
    val itemId: String,
    val itemImageUrl: String,
    val x: Float,
    val y: Float,
    val scale: Float,
    val rotation: Float,
    val zIndex: Int
)

data class OutfitDomainModel(
    val id: String,
    val name: String,
    val description: String?,
    val canvasWidth: Int,
    val canvasHeight: Int,
    val templateId: String?,
    val templateName: String?,
    val items: List<OutfitItemDomainModel>,
    val categories: List<CategoryDomainModel>
)

data class OutfitItemPayloadDomainModel(
    val itemId: String,
    val x: Float,
    val y: Float,
    val scale: Float,
    val rotation: Float,
    val zIndex: Int
)