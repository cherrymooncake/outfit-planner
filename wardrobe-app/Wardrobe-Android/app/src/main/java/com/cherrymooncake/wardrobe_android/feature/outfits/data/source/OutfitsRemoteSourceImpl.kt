package com.cherrymooncake.wardrobe_android.feature.outfits.data.source

import com.cherrymooncake.wardrobe_android.feature.outfits.data.api.IOutfitsApi
import com.cherrymooncake.wardrobe_android.feature.outfits.data.model.CreateOutfitApiModel
import com.cherrymooncake.wardrobe_android.feature.outfits.data.model.OutfitApiModel
import com.cherrymooncake.wardrobe_android.feature.outfits.data.model.UpdateOutfitApiModel

class OutfitsRemoteSourceImpl(private val api: IOutfitsApi) : IOutfitsRemoteSource {
    override suspend fun getOutfits(searchTerm: String?, categoryId: String?) = api.getOutfits(searchTerm, categoryId)
    override suspend fun getOutfitById(id: String) = api.getOutfitById(id)
    override suspend fun createOutfit(payload: CreateOutfitApiModel) = api.createOutfit(payload)
    override suspend fun updateOutfit(id: String, payload: UpdateOutfitApiModel) = api.updateOutfit(id, payload)
    override suspend fun deleteOutfit(id: String) = api.deleteOutfit(id)
}