package com.cherrymooncake.wardrobe_android.feature.templates.data.source

import com.cherrymooncake.wardrobe_android.feature.templates.data.model.TemplateDbModel

interface ITemplatesLocalSource {
    suspend fun getTemplates(): List<TemplateDbModel>
    suspend fun getTemplateById(id: String): TemplateDbModel?
    suspend fun saveTemplates(templates: List<TemplateDbModel>)
    suspend fun saveTemplate(template: TemplateDbModel)
    suspend fun deleteTemplate(id: String)
}