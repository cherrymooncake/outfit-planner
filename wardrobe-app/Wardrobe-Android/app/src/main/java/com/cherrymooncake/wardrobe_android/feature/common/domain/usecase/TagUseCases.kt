package com.cherrymooncake.wardrobe_android.feature.common.domain.usecase
import com.cherrymooncake.wardrobe_android.feature.common.domain.repository.ICommonRemoteRepository

class CreateTagUseCase(private val repository: ICommonRemoteRepository) {
    suspend operator fun invoke(name: String, isOutfit: Boolean, isItem: Boolean) {
        repository.createTag(name, isOutfit, isItem)
    }
}

class UpdateTagUseCase(private val repository: ICommonRemoteRepository) {
    suspend operator fun invoke(id: String, name: String) {
        repository.updateTag(id, name)
    }
}

class DeleteTagUseCase(private val repository: ICommonRemoteRepository) {
    suspend operator fun invoke(id: String) {
        repository.deleteTag(id)
    }
}