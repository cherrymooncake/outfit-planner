package com.cherrymooncake.wardrobe_android.feature.templates.data.repository

import com.cherrymooncake.wardrobe_android.feature.templates.data.mapper.*
import com.cherrymooncake.wardrobe_android.feature.templates.data.source.ITemplatesLocalSource
import com.cherrymooncake.wardrobe_android.feature.templates.domain.model.TemplateDomainModel
import com.cherrymooncake.wardrobe_android.feature.templates.domain.repository.ITemplatesLocalRepository

class TemplatesLocalRepositoryImpl(private val source: ITemplatesLocalSource) : ITemplatesLocalRepository {
    override suspend fun getTemplates() = source.getTemplates().map { it.toDomain() }
    override suspend fun getTemplateById(id: String) = source.getTemplateById(id)?.toDomain()
    override suspend fun saveTemplates(templates: List<TemplateDomainModel>) = source.saveTemplates(templates.map { it.toDbModel() })
    override suspend fun saveTemplate(template: TemplateDomainModel) = source.saveTemplate(template.toDbModel())
    override suspend fun deleteTemplate(id: String) = source.deleteTemplate(id)
}