package com.cherrymooncake.wardrobe_android.feature.wardrobe.domain.usecase

import com.cherrymooncake.wardrobe_android.feature.wardrobe.domain.repository.IItemsLocalRepository
import com.cherrymooncake.wardrobe_android.feature.wardrobe.domain.repository.IItemsRemoteRepository

class UpdateItemUseCase(
    private val remoteRepository: IItemsRemoteRepository,
    private val localRepository: IItemsLocalRepository
) {
    suspend operator fun invoke(
        id: String,
        name: String,
        description: String?,
        categoryIds: List<String>,
        tagIds: List<String>
    ) {
        val updatedItem = remoteRepository.updateItem(id, name, description, categoryIds, tagIds)
        localRepository.saveItem(updatedItem)
    }
}