package com.cherrymooncake.wardrobe_android.feature.templates.data.source

import com.cherrymooncake.wardrobe_android.feature.templates.data.db.TemplatesDao
import com.cherrymooncake.wardrobe_android.feature.templates.data.model.TemplateDbModel

class TemplatesLocalSourceImpl(private val dao: TemplatesDao) : ITemplatesLocalSource {
    override suspend fun getTemplates() = dao.getAllTemplates()
    override suspend fun getTemplateById(id: String) = dao.getTemplateById(id)
    override suspend fun saveTemplates(templates: List<TemplateDbModel>) = dao.replaceAll(templates)
    override suspend fun saveTemplate(template: TemplateDbModel) = dao.insertTemplate(template)
    override suspend fun deleteTemplate(id: String) = dao.deleteTemplate(id)
}