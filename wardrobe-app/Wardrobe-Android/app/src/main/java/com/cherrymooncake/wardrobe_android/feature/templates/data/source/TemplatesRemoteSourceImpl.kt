package com.cherrymooncake.wardrobe_android.feature.templates.data.source

import com.cherrymooncake.wardrobe_android.feature.templates.data.api.ITemplatesApi
import com.cherrymooncake.wardrobe_android.feature.templates.data.model.CreateTemplateApiModel
import com.cherrymooncake.wardrobe_android.feature.templates.data.model.UpdateTemplateApiModel

class TemplatesRemoteSourceImpl(private val api: ITemplatesApi) : ITemplatesRemoteSource {
    override suspend fun getTemplates() = api.getTemplates()
    override suspend fun getTemplateById(id: String) = api.getTemplateById(id)
    override suspend fun createTemplate(payload: CreateTemplateApiModel) = api.createTemplate(payload)
    override suspend fun updateTemplate(id: String, payload: UpdateTemplateApiModel) = api.updateTemplate(id, payload)
    override suspend fun deleteTemplate(id: String) = api.deleteTemplate(id)
}