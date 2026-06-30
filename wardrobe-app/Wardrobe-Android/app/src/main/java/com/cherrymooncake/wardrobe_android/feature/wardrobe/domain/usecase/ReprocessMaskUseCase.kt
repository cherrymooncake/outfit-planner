package com.cherrymooncake.wardrobe_android.feature.wardrobe.domain.usecase

import com.cherrymooncake.wardrobe_android.feature.wardrobe.domain.repository.IItemsLocalRepository
import com.cherrymooncake.wardrobe_android.feature.wardrobe.domain.repository.IItemsRemoteRepository

class ReprocessMaskUseCase(
    private val remoteRepository: IItemsRemoteRepository,
    private val localRepository: IItemsLocalRepository
) {
    suspend operator fun invoke(itemId: String, contourJson: String) {
        val newUrl = remoteRepository.reprocessMask(itemId, contourJson)

        val localItem = localRepository.getItemById(itemId)
        if (localItem != null) {
            val updatedItem = localItem.copy(processedImageUrl = newUrl)
            localRepository.saveItem(updatedItem)
        }
    }
}