package com.cherrymooncake.wardrobe_android.feature.outfits.data.mapper

import com.cherrymooncake.wardrobe_android.feature.common.data.mapper.toDbModel
import com.cherrymooncake.wardrobe_android.feature.common.data.mapper.toDomain
import com.cherrymooncake.wardrobe_android.feature.outfits.data.model.*
import com.cherrymooncake.wardrobe_android.feature.outfits.domain.model.*

fun OutfitItemApiModel.toDomain() = OutfitItemDomainModel(
    id = id, itemId = itemId, itemImageUrl = itemImageUrl,
    x = x.toFloat(), y = y.toFloat(), scale = scale.toFloat(), rotation = rotation.toFloat(), zIndex = zIndex
)

fun OutfitApiModel.toDomain() = OutfitDomainModel(
    id = id, name = name, description = description,
    canvasWidth = canvasWidth, canvasHeight = canvasHeight,
    templateId = templateId, templateName = templateName,
    items = items.map { it.toDomain() },
    categories = categories.map { it.toDomain() }
)

fun OutfitItemLocalModel.toDomain() = OutfitItemDomainModel(
    id = id, itemId = itemId, itemImageUrl = itemImageUrl,
    x = x, y = y, scale = scale, rotation = rotation, zIndex = zIndex
)

fun OutfitDbModel.toDomain() = OutfitDomainModel(
    id = id, name = name, description = description,
    canvasWidth = canvasWidth, canvasHeight = canvasHeight,
    templateId = templateId, templateName = templateName,
    items = items.map { it.toDomain() },
    categories = categories.map { it.toDomain() }
)

fun OutfitItemDomainModel.toDbModel() = OutfitItemLocalModel(
    id = id, itemId = itemId, itemImageUrl = itemImageUrl,
    x = x, y = y, scale = scale, rotation = rotation, zIndex = zIndex
)

fun OutfitDomainModel.toDbModel() = OutfitDbModel(
    id = id, name = name, description = description,
    canvasWidth = canvasWidth, canvasHeight = canvasHeight,
    templateId = templateId, templateName = templateName,
    items = items.map { it.toDbModel() },
    categories = categories.map { it.toDbModel() }
)

fun OutfitItemPayloadDomainModel.toApiPayload() = OutfitItemPayloadApiModel(
    itemId = itemId, x = x.toDouble(), y = y.toDouble(), scale = scale.toDouble(), rotation = rotation.toDouble(), zIndex = zIndex
)