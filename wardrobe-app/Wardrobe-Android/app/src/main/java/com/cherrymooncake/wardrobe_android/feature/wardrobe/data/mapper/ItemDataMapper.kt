package com.cherrymooncake.wardrobe_android.feature.wardrobe.data.mapper

import com.cherrymooncake.wardrobe_android.feature.common.data.mapper.toDbModel
import com.cherrymooncake.wardrobe_android.feature.common.data.mapper.toDomain
import com.cherrymooncake.wardrobe_android.feature.wardrobe.data.model.ItemApiModel
import com.cherrymooncake.wardrobe_android.feature.wardrobe.data.model.ItemDbModel
import com.cherrymooncake.wardrobe_android.feature.wardrobe.domain.model.ItemDomainModel

fun ItemApiModel.toDomain(): ItemDomainModel {
    return ItemDomainModel(
        id = this.id,
        name = this.name,
        description = this.description,
        originalImageUrl = this.originalImageUrl,
        processedImageUrl = this.processedImageUrl,
        categories = this.categories.map { it.toDomain() },
        tags = this.tags.map { it.toDomain() }
    )
}

fun ItemDbModel.toDomain(): ItemDomainModel {
    return ItemDomainModel(
        id = this.id,
        name = this.name,
        description = this.description,
        originalImageUrl = this.originalImageUrl,
        processedImageUrl = this.processedImageUrl,
        categories = this.categories.map { it.toDomain() },
        tags = this.tags.map { it.toDomain() }
    )
}

fun ItemDomainModel.toDbModel(): ItemDbModel {
    return ItemDbModel(
        id = this.id,
        name = this.name,
        description = this.description,
        originalImageUrl = this.originalImageUrl,
        processedImageUrl = this.processedImageUrl,
        categories = this.categories.map { it.toDbModel() },
        tags = this.tags.map { it.toDbModel() }
    )
}