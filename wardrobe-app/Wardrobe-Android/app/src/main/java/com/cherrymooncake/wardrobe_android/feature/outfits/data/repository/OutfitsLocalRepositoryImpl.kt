package com.cherrymooncake.wardrobe_android.feature.outfits.data.repository
import com.cherrymooncake.wardrobe_android.feature.outfits.data.mapper.*
import com.cherrymooncake.wardrobe_android.feature.outfits.data.source.IOutfitsLocalSource
import com.cherrymooncake.wardrobe_android.feature.outfits.domain.model.OutfitDomainModel
import com.cherrymooncake.wardrobe_android.feature.outfits.domain.repository.IOutfitsLocalRepository

class OutfitsLocalRepositoryImpl(private val source: IOutfitsLocalSource) : IOutfitsLocalRepository {
    override suspend fun getOutfits() = source.getOutfits().map { it.toDomain() }
    override suspend fun getOutfitById(id: String) = source.getOutfitById(id)?.toDomain()
    override suspend fun saveOutfits(outfits: List<OutfitDomainModel>) = source.saveOutfits(outfits.map { it.toDbModel() })
    override suspend fun saveOutfit(outfit: OutfitDomainModel) = source.saveOutfit(outfit.toDbModel())
    override suspend fun deleteOutfit(id: String) = source.deleteOutfit(id)
}