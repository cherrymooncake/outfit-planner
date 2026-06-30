package com.cherrymooncake.wardrobe_android.feature.common.domain.usecase
import com.cherrymooncake.wardrobe_android.feature.common.domain.repository.ICommonRemoteRepository

class CreateCategoryUseCase(private val repository: ICommonRemoteRepository) {
    suspend operator fun invoke(name: String, isOutfit: Boolean, isItem: Boolean) {
        repository.createCategory(name, isOutfit, isItem)
    }
}

class UpdateCategoryUseCase(private val repository: ICommonRemoteRepository) {
    suspend operator fun invoke(id: String, name: String) {
        repository.updateCategory(id, name)
    }
}

class DeleteCategoryUseCase(private val repository: ICommonRemoteRepository) {
    suspend operator fun invoke(id: String) {
        repository.deleteCategory(id)
    }
}