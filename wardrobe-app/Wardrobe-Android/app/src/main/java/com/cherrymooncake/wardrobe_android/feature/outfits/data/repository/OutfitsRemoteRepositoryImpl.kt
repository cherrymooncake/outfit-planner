package com.cherrymooncake.wardrobe_android.feature.outfits.data.repository
import com.cherrymooncake.wardrobe_android.feature.outfits.data.mapper.*
import com.cherrymooncake.wardrobe_android.feature.outfits.data.model.*
import com.cherrymooncake.wardrobe_android.feature.outfits.data.source.IOutfitsRemoteSource
import com.cherrymooncake.wardrobe_android.feature.outfits.domain.model.*
import com.cherrymooncake.wardrobe_android.feature.outfits.domain.repository.IOutfitsRemoteRepository

class OutfitsRemoteRepositoryImpl(private val source: IOutfitsRemoteSource) : IOutfitsRemoteRepository {
    override suspend fun getOutfits(searchTerm: String?, categoryId: String?) = source.getOutfits(searchTerm, categoryId).map { it.toDomain() }
    override suspend fun getOutfitById(id: String) = source.getOutfitById(id).toDomain()
    override suspend fun deleteOutfit(id: String) = source.deleteOutfit(id)

    override suspend fun createOutfit(name: String, desc: String?, width: Int, height: Int, templateId: String?, items: List<OutfitItemPayloadDomainModel>, categoryIds: List<String>): OutfitDomainModel {
        val payload = CreateOutfitApiModel(name, desc, width, height, templateId, items.map { it.toApiPayload() }, categoryIds)
        return source.createOutfit(payload).toDomain()
    }

    override suspend fun updateOutfit(id: String, name: String, desc: String?, width: Int, height: Int, templateId: String?, items: List<OutfitItemPayloadDomainModel>, categoryIds: List<String>): OutfitDomainModel {
        val payload = UpdateOutfitApiModel(name, desc, width, height, templateId, items.map { it.toApiPayload() }, categoryIds)
        return source.updateOutfit(id, payload).toDomain()
    }
}