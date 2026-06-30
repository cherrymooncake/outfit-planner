package com.cherrymooncake.wardrobe_android.feature.templates.data.source

import com.cherrymooncake.wardrobe_android.feature.templates.data.model.CreateTemplateApiModel
import com.cherrymooncake.wardrobe_android.feature.templates.data.model.TemplateApiModel
import com.cherrymooncake.wardrobe_android.feature.templates.data.model.UpdateTemplateApiModel

interface ITemplatesRemoteSource {
    suspend fun getTemplates(): List<TemplateApiModel>
    suspend fun getTemplateById(id: String): TemplateApiModel
    suspend fun createTemplate(payload: CreateTemplateApiModel): TemplateApiModel
    suspend fun updateTemplate(id: String, payload: UpdateTemplateApiModel): TemplateApiModel
    suspend fun deleteTemplate(id: String)
}