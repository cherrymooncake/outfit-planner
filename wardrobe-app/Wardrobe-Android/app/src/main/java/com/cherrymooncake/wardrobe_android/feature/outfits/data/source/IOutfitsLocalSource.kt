package com.cherrymooncake.wardrobe_android.feature.outfits.data.source

import com.cherrymooncake.wardrobe_android.feature.outfits.data.db.OutfitsDao
import com.cherrymooncake.wardrobe_android.feature.outfits.data.model.OutfitDbModel

interface IOutfitsLocalSource {
    suspend fun getOutfits(): List<OutfitDbModel>
    suspend fun getOutfitById(id: String): OutfitDbModel?
    suspend fun saveOutfits(outfits: List<OutfitDbModel>)
    suspend fun saveOutfit(outfit: OutfitDbModel)
    suspend fun deleteOutfit(id: String)
}