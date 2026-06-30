package com.cherrymooncake.wardrobe_android.feature.outfits.domain.usecase

import com.cherrymooncake.wardrobe_android.feature.outfits.domain.model.OutfitDomainModel
import com.cherrymooncake.wardrobe_android.feature.outfits.domain.model.OutfitItemPayloadDomainModel
import com.cherrymooncake.wardrobe_android.feature.outfits.domain.repository.IOutfitsLocalRepository
import com.cherrymooncake.wardrobe_android.feature.outfits.domain.repository.IOutfitsRemoteRepository

class CreateOutfitUseCase(private val remote: IOutfitsRemoteRepository, private val local: IOutfitsLocalRepository) {

    suspend operator fun invoke(name: String, desc: String?, w: Int, h: Int, tId: String?, items: List<OutfitItemPayloadDomainModel>, cats: List<String>): OutfitDomainModel {
        val outfit = remote.createOutfit(name, desc, w, h, tId, items, cats)
        local.saveOutfit(outfit)
        return outfit
    }
}