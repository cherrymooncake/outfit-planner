package com.cherrymooncake.wardrobe_android.feature.outfits.data.source

import com.cherrymooncake.wardrobe_android.feature.outfits.data.api.IOutfitsApi
import com.cherrymooncake.wardrobe_android.feature.outfits.data.model.CreateOutfitApiModel
import com.cherrymooncake.wardrobe_android.feature.outfits.data.model.OutfitApiModel
import com.cherrymooncake.wardrobe_android.feature.outfits.data.model.UpdateOutfitApiModel

interface IOutfitsRemoteSource {
    suspend fun getOutfits(searchTerm: String?, categoryId: String?): List<OutfitApiModel>
    suspend fun getOutfitById(id: String): OutfitApiModel
    suspend fun createOutfit(payload: CreateOutfitApiModel): OutfitApiModel
    suspend fun updateOutfit(id: String, payload: UpdateOutfitApiModel): OutfitApiModel
    suspend fun deleteOutfit(id: String)
}
