package com.cherrymooncake.wardrobe_android.feature.outfits.domain.repository
import com.cherrymooncake.wardrobe_android.feature.outfits.domain.model.OutfitDomainModel

interface IOutfitsLocalRepository {
    suspend fun getOutfits(): List<OutfitDomainModel>
    suspend fun getOutfitById(id: String): OutfitDomainModel?
    suspend fun saveOutfits(outfits: List<OutfitDomainModel>)
    suspend fun saveOutfit(outfit: OutfitDomainModel)
    suspend fun deleteOutfit(id: String)
}