package com.cherrymooncake.wardrobe_android.feature.outfits.domain.repository
import com.cherrymooncake.wardrobe_android.feature.outfits.domain.model.OutfitDomainModel
import com.cherrymooncake.wardrobe_android.feature.outfits.domain.model.OutfitItemPayloadDomainModel

interface IOutfitsRemoteRepository {
    suspend fun getOutfits(searchTerm: String?, categoryId: String?): List<OutfitDomainModel>
    suspend fun getOutfitById(id: String): OutfitDomainModel
    suspend fun createOutfit(name: String, desc: String?, width: Int, height: Int, templateId: String?, items: List<OutfitItemPayloadDomainModel>, categoryIds: List<String>): OutfitDomainModel
    suspend fun updateOutfit(id: String, name: String, desc: String?, width: Int, height: Int, templateId: String?, items: List<OutfitItemPayloadDomainModel>, categoryIds: List<String>): OutfitDomainModel
    suspend fun deleteOutfit(id: String)
}