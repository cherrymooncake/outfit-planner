package com.cherrymooncake.wardrobe_android.feature.wardrobe.presentation.mapper

import com.cherrymooncake.wardrobe_android.feature.common.domain.model.CategoryDomainModel
import com.cherrymooncake.wardrobe_android.feature.common.domain.model.TagDomainModel
import com.cherrymooncake.wardrobe_android.feature.wardrobe.domain.model.ItemDomainModel
import com.cherrymooncake.wardrobe_android.feature.wardrobe.presentation.model.CategoryUiModel
import com.cherrymooncake.wardrobe_android.feature.wardrobe.presentation.model.ItemUiModel
import com.cherrymooncake.wardrobe_android.feature.wardrobe.presentation.model.TagUiModel

private const val IMG_BASE_URL = "http://localhost:5202"

fun CategoryDomainModel.toUi() = CategoryUiModel(id = this.id, name = this.name)

fun TagDomainModel.toUi() = TagUiModel(id = this.id, name = this.name)

fun ItemDomainModel.toUi(): ItemUiModel {
    val cleanPath = this.processedImageUrl.removePrefix("/")
    val fullUrl = "$IMG_BASE_URL/$cleanPath"

    return ItemUiModel(
        id = this.id,
        name = this.name,
        description = this.description,
        imageUrl = fullUrl,
        categories = this.categories.map { it.toUi() },
        tags = this.tags.map { it.toUi() }
    )
}