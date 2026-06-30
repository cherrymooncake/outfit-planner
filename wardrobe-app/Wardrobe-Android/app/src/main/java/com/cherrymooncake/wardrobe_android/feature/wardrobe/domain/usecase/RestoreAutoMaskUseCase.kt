package com.cherrymooncake.wardrobe_android.feature.wardrobe.domain.usecase

import com.cherrymooncake.wardrobe_android.feature.wardrobe.domain.repository.IItemsLocalRepository
import com.cherrymooncake.wardrobe_android.feature.wardrobe.domain.repository.IItemsRemoteRepository

class RestoreAutoMaskUseCase(
    private val remoteRepository: IItemsRemoteRepository,
    private val localRepository: IItemsLocalRepository
) {
    suspend operator fun invoke(itemId: String) {
        val newUrl = remoteRepository.restoreAutoMask(itemId)

        val localItem = localRepository.getItemById(itemId)
        if (localItem != null) {
            val updatedItem = localItem.copy(processedImageUrl = newUrl)
            localRepository.saveItem(updatedItem)
        }
    }
}