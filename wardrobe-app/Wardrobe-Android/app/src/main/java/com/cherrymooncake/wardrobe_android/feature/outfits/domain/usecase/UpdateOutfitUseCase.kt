package com.cherrymooncake.wardrobe_android.feature.outfits.domain.usecase

import com.cherrymooncake.wardrobe_android.feature.outfits.domain.model.OutfitItemPayloadDomainModel
import com.cherrymooncake.wardrobe_android.feature.outfits.domain.repository.IOutfitsLocalRepository
import com.cherrymooncake.wardrobe_android.feature.outfits.domain.repository.IOutfitsRemoteRepository

class UpdateOutfitUseCase(private val remote: IOutfitsRemoteRepository, private val local: IOutfitsLocalRepository) {
    suspend operator fun invoke(id: String, name: String, desc: String?, w: Int, h: Int, tId: String?, items: List<OutfitItemPayloadDomainModel>, cats: List<String>) {
        val outfit = remote.updateOutfit(id, name, desc, w, h, tId, items, cats)
        local.saveOutfit(outfit)
    }
}