package com.cherrymooncake.wardrobe_android.feature.templates.presentation.model

import com.cherrymooncake.wardrobe_android.feature.templates.domain.model.TemplateDomainModel
import com.cherrymooncake.wardrobe_android.feature.templates.domain.model.TemplateItemDomainModel

data class TemplateItemUiModel(
    val id: String, val categoryName: String?, val x: Float, val y: Float, val scale: Float, val rotation: Float
)
data class TemplateUiModel(
    val id: String, val name: String, val description: String?, val items: List<TemplateItemUiModel>
)

fun TemplateItemDomainModel.toUi() = TemplateItemUiModel(id, categoryName, x, y, scale, rotation)
fun TemplateDomainModel.toUi() = TemplateUiModel(id, name, description, items.map { it.toUi() })