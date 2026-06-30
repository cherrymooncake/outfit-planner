package com.cherrymooncake.wardrobe_android.feature.outfits.domain.usecase

import com.cherrymooncake.wardrobe_android.feature.outfits.domain.repository.IOutfitsLocalRepository
import com.cherrymooncake.wardrobe_android.feature.outfits.domain.repository.IOutfitsRemoteRepository

class DeleteOutfitUseCase(private val remote: IOutfitsRemoteRepository, private val local: IOutfitsLocalRepository) {
    suspend operator fun invoke(id: String) {
        remote.deleteOutfit(id)
        local.deleteOutfit(id)
    }
}