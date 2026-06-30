package com.cherrymooncake.wardrobe_android.feature.outfits.domain.usecase

import com.cherrymooncake.wardrobe_android.feature.outfits.domain.model.OutfitDomainModel
import com.cherrymooncake.wardrobe_android.feature.outfits.domain.repository.IOutfitsLocalRepository
import com.cherrymooncake.wardrobe_android.feature.outfits.domain.repository.IOutfitsRemoteRepository

class GetOutfitByIdUseCase(private val remote: IOutfitsRemoteRepository, private val local: IOutfitsLocalRepository) {
    suspend operator fun invoke(id: String): OutfitDomainModel? {
        return try {
            val outfit = remote.getOutfitById(id)
            local.saveOutfit(outfit)
            outfit
        } catch (e: Exception) {
            local.getOutfitById(id)
        }
    }
}