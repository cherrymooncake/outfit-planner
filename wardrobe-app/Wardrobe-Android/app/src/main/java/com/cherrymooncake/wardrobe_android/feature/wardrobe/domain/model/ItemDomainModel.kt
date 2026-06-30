package com.cherrymooncake.wardrobe_android.feature.wardrobe.domain.model

import com.cherrymooncake.wardrobe_android.feature.common.domain.model.CategoryDomainModel
import com.cherrymooncake.wardrobe_android.feature.common.domain.model.TagDomainModel

data class ItemDomainModel(
    val id: String,
    val name: String,
    val description: String?,
    val originalImageUrl: String,
    val processedImageUrl: String,
    val categories: List<CategoryDomainModel>,
    val tags: List<TagDomainModel>
)