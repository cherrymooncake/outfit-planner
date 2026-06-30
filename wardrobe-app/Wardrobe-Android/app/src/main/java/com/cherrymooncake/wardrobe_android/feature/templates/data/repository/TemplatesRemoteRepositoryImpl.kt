package com.cherrymooncake.wardrobe_android.feature.templates.data.repository

import com.cherrymooncake.wardrobe_android.feature.templates.data.mapper.*
import com.cherrymooncake.wardrobe_android.feature.templates.data.model.*
import com.cherrymooncake.wardrobe_android.feature.templates.data.source.ITemplatesRemoteSource
import com.cherrymooncake.wardrobe_android.feature.templates.domain.model.*
import com.cherrymooncake.wardrobe_android.feature.templates.domain.repository.ITemplatesRemoteRepository

class TemplatesRemoteRepositoryImpl(private val source: ITemplatesRemoteSource) : ITemplatesRemoteRepository {
    override suspend fun getTemplates() = source.getTemplates().map { it.toDomain() }
    override suspend fun getTemplateById(id: String) = source.getTemplateById(id).toDomain()
    override suspend fun deleteTemplate(id: String) = source.deleteTemplate(id)

    override suspend fun createTemplate(name: String, desc: String?, items: List<TemplateItemPayloadDomainModel>): TemplateDomainModel {
        val payload = CreateTemplateApiModel(name, desc, items.map { it.toApiPayload() })
        return source.createTemplate(payload).toDomain()
    }

    override suspend fun updateTemplate(id: String, name: String, desc: String?, items: List<TemplateItemPayloadDomainModel>): TemplateDomainModel {
        val payload = UpdateTemplateApiModel(name, desc, items.map { it.toApiPayload() })
        return source.updateTemplate(id, payload).toDomain()
    }
}