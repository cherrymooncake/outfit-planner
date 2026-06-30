package com.cherrymooncake.wardrobe_android.feature.wardrobe.domain.usecase

import com.cherrymooncake.wardrobe_android.feature.wardrobe.domain.model.ItemDomainModel
import com.cherrymooncake.wardrobe_android.feature.wardrobe.domain.repository.IItemsLocalRepository

class GetItemByIdUseCase(
    private val localRepository: IItemsLocalRepository
) {
    suspend operator fun invoke(itemId: String): ItemDomainModel? {
        return localRepository.getItemById(itemId)
    }
}