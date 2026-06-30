package com.cherrymooncake.wardrobe_android.feature.outfits.data.source

import com.cherrymooncake.wardrobe_android.feature.outfits.data.db.OutfitsDao
import com.cherrymooncake.wardrobe_android.feature.outfits.data.model.OutfitDbModel

class OutfitsLocalSourceImpl(private val dao: OutfitsDao) : IOutfitsLocalSource {
    override suspend fun getOutfits() = dao.getAllOutfits()
    override suspend fun getOutfitById(id: String) = dao.getOutfitById(id)
    override suspend fun saveOutfits(outfits: List<OutfitDbModel>) = dao.replaceAll(outfits)
    override suspend fun saveOutfit(outfit: OutfitDbModel) = dao.insertOutfit(outfit)
    override suspend fun deleteOutfit(id: String) = dao.deleteOutfit(id)
}