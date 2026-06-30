package com.cherrymooncake.wardrobe_android.feature.templates.domain.usecase

import com.cherrymooncake.wardrobe_android.feature.templates.domain.model.TemplateDomainModel
import com.cherrymooncake.wardrobe_android.feature.templates.domain.model.TemplateItemPayloadDomainModel
import com.cherrymooncake.wardrobe_android.feature.templates.domain.repository.ITemplatesLocalRepository
import com.cherrymooncake.wardrobe_android.feature.templates.domain.repository.ITemplatesRemoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SyncTemplatesUseCase(
    private val remote: ITemplatesRemoteRepository,
    private val local: ITemplatesLocalRepository
) {
    operator fun invoke(): Flow<List<TemplateDomainModel>> = flow {
        val localData = local.getTemplates()
        if (localData.isNotEmpty()) emit(localData)
        try {
            val remoteData = remote.getTemplates()
            local.saveTemplates(remoteData)
            emit(remoteData)
        } catch (e: Exception) {
            emit(local.getTemplates())
        }
    }
}

class GetTemplateByIdUseCase(
    private val remote: ITemplatesRemoteRepository,
    private val local: ITemplatesLocalRepository
) {
    suspend operator fun invoke(id: String): TemplateDomainModel? {
        return try {
            val template = remote.getTemplateById(id)
            local.saveTemplate(template)
            template
        } catch (e: Exception) {
            local.getTemplateById(id)
        }
    }
}

class CreateTemplateUseCase(
    private val remote: ITemplatesRemoteRepository,
    private val local: ITemplatesLocalRepository
) {
    suspend operator fun invoke(name: String, desc: String?, items: List<TemplateItemPayloadDomainModel>) {
        val template = remote.createTemplate(name, desc, items)
        local.saveTemplate(template)
    }
}

class UpdateTemplateUseCase(
    private val remote: ITemplatesRemoteRepository,
    private val local: ITemplatesLocalRepository
) {
    suspend operator fun invoke(id: String, name: String, desc: String?, items: List<TemplateItemPayloadDomainModel>) {
        val template = remote.updateTemplate(id, name, desc, items)
        local.saveTemplate(template)
    }
}

class DeleteTemplateUseCase(
    private val remote: ITemplatesRemoteRepository,
    private val local: ITemplatesLocalRepository
) {
    suspend operator fun invoke(id: String) {
        remote.deleteTemplate(id)
        local.deleteTemplate(id)
    }
}