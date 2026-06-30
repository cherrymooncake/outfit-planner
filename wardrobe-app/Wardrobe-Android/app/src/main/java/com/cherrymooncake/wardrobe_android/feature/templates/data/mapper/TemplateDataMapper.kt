package com.cherrymooncake.wardrobe_android.feature.templates.data.mapper

import com.cherrymooncake.wardrobe_android.feature.templates.data.model.*
import com.cherrymooncake.wardrobe_android.feature.templates.domain.model.*

fun TemplateItemApiModel.toDomain() = TemplateItemDomainModel(
    id = id, categoryIdHint = categoryIdHint, categoryName = categoryName,
    x = x.toFloat(), y = y.toFloat(), scale = scale.toFloat(), rotation = rotation.toFloat(), zIndex = zIndex
)

fun TemplateApiModel.toDomain() = TemplateDomainModel(
    id = id, name = name, description = description,
    items = items.map { it.toDomain() }
)

fun TemplateItemLocalModel.toDomain() = TemplateItemDomainModel(
    id = id, categoryIdHint = categoryIdHint, categoryName = categoryName,
    x = x, y = y, scale = scale, rotation = rotation, zIndex = zIndex
)

fun TemplateDbModel.toDomain() = TemplateDomainModel(
    id = id, name = name, description = description,
    items = items.map { it.toDomain() }
)

fun TemplateItemDomainModel.toDbModel() = TemplateItemLocalModel(
    id = id, categoryIdHint = categoryIdHint, categoryName = categoryName,
    x = x, y = y, scale = scale, rotation = rotation, zIndex = zIndex
)

fun TemplateDomainModel.toDbModel() = TemplateDbModel(
    id = id, name = name, description = description,
    items = items.map { it.toDbModel() }
)

fun TemplateItemPayloadDomainModel.toApiPayload() = TemplateItemPayloadApiModel(
    categoryIdHint = categoryIdHint, x = x.toDouble(), y = y.toDouble(), scale = scale.toDouble(), rotation = rotation.toDouble(), zIndex = zIndex
)