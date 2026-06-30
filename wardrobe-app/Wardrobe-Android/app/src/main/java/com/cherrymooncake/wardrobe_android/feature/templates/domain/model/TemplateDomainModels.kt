package com.cherrymooncake.wardrobe_android.feature.templates.domain.model

data class TemplateItemDomainModel(
    val id: String,
    val categoryIdHint: String?,
    val categoryName: String?,
    val x: Float,
    val y: Float,
    val scale: Float,
    val rotation: Float,
    val zIndex: Int
)

data class TemplateDomainModel(
    val id: String,
    val name: String,
    val description: String?,
    val items: List<TemplateItemDomainModel>
)

data class TemplateItemPayloadDomainModel(
    val categoryIdHint: String?,
    val x: Float,
    val y: Float,
    val scale: Float,
    val rotation: Float,
    val zIndex: Int
)