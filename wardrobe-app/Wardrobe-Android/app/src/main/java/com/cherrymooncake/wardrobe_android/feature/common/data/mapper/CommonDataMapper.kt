package com.cherrymooncake.wardrobe_android.feature.common.data.mapper

import com.cherrymooncake.wardrobe_android.feature.common.data.model.CategoryApiModel
import com.cherrymooncake.wardrobe_android.feature.common.data.model.CategoryDbModel
import com.cherrymooncake.wardrobe_android.feature.common.data.model.TagApiModel
import com.cherrymooncake.wardrobe_android.feature.common.data.model.TagDbModel
import com.cherrymooncake.wardrobe_android.feature.common.domain.model.CategoryDomainModel
import com.cherrymooncake.wardrobe_android.feature.common.domain.model.TagDomainModel


fun CategoryApiModel.toDomain(): CategoryDomainModel {
    return CategoryDomainModel(
        id = this.id,
        name = this.name,
        isOutfitCategory = this.isOutfitCategory,
        isItemCategory = this.isItemCategory
    )
}

fun TagApiModel.toDomain(): TagDomainModel {
    return TagDomainModel(
        id = this.id,
        name = this.name,
        isOutfitTag = this.isOutfitTag,
        isItemTag = this.isItemTag
    )
}

fun CategoryDbModel.toDomain(): CategoryDomainModel {
    return CategoryDomainModel(
        id = this.id,
        name = this.name,
        isOutfitCategory = this.isOutfitCategory,
        isItemCategory = this.isItemCategory
    )
}

fun CategoryDomainModel.toDbModel(): CategoryDbModel {
    return CategoryDbModel(
        id = this.id,
        name = this.name,
        isOutfitCategory = this.isOutfitCategory,
        isItemCategory = this.isItemCategory
    )
}

fun TagDbModel.toDomain(): TagDomainModel {
    return TagDomainModel(
        id = this.id,
        name = this.name,
        isOutfitTag = this.isOutfitTag,
        isItemTag = this.isItemTag
    )
}

fun TagDomainModel.toDbModel(): TagDbModel {
    return TagDbModel(
        id = this.id,
        name = this.name,
        isOutfitTag = this.isOutfitTag,
        isItemTag = this.isItemTag
    )
}