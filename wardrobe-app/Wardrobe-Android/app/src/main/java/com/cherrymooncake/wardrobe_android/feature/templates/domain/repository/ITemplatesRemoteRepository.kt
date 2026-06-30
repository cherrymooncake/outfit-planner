package com.cherrymooncake.wardrobe_android.feature.templates.domain.repository

import com.cherrymooncake.wardrobe_android.feature.templates.domain.model.TemplateDomainModel
import com.cherrymooncake.wardrobe_android.feature.templates.domain.model.TemplateItemPayloadDomainModel

interface ITemplatesRemoteRepository {
    suspend fun getTemplates(): List<TemplateDomainModel>
    suspend fun getTemplateById(id: String): TemplateDomainModel
    suspend fun createTemplate(name: String, desc: String?, items: List<TemplateItemPayloadDomainModel>): TemplateDomainModel
    suspend fun updateTemplate(id: String, name: String, desc: String?, items: List<TemplateItemPayloadDomainModel>): TemplateDomainModel
    suspend fun deleteTemplate(id: String)
}