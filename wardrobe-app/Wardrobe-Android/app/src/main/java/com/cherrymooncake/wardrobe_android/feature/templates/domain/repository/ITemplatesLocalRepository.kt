package com.cherrymooncake.wardrobe_android.feature.templates.domain.repository

import com.cherrymooncake.wardrobe_android.feature.templates.domain.model.TemplateDomainModel

interface ITemplatesLocalRepository {
    suspend fun getTemplates(): List<TemplateDomainModel>
    suspend fun getTemplateById(id: String): TemplateDomainModel?
    suspend fun saveTemplates(templates: List<TemplateDomainModel>)
    suspend fun saveTemplate(template: TemplateDomainModel)
    suspend fun deleteTemplate(id: String)
}