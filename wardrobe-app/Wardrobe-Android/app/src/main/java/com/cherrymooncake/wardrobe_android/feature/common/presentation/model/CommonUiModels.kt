package com.cherrymooncake.wardrobe_android.feature.common.presentation.model

import com.cherrymooncake.wardrobe_android.feature.common.domain.model.CategoryDomainModel
import com.cherrymooncake.wardrobe_android.feature.common.domain.model.TagDomainModel

data class DictionaryCategoryUiModel(
    val id: String,
    val name: String,
    val isItemCategory: Boolean,
    val isOutfitCategory: Boolean
)

data class DictionaryTagUiModel(
    val id: String,
    val name: String,
    val isItemTag: Boolean,
    val isOutfitTag: Boolean
)

fun CategoryDomainModel.toDictionaryUi() = DictionaryCategoryUiModel(id, name, isItemCategory, isOutfitCategory)
fun TagDomainModel.toDictionaryUi() = DictionaryTagUiModel(id, name, isItemTag, isOutfitTag)