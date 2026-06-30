package com.cherrymooncake.wardrobe_android.feature.wardrobe.data.repository

import com.cherrymooncake.wardrobe_android.feature.wardrobe.data.mapper.toDomain
import com.cherrymooncake.wardrobe_android.feature.wardrobe.data.model.UpdateItemApiModel
import com.cherrymooncake.wardrobe_android.feature.wardrobe.data.source.IItemsRemoteSource
import com.cherrymooncake.wardrobe_android.feature.wardrobe.domain.model.ItemDomainModel
import com.cherrymooncake.wardrobe_android.feature.wardrobe.domain.repository.IItemsRemoteRepository
import java.io.File

class ItemsRemoteRepositoryImpl(
    private val remoteSource: IItemsRemoteSource
) : IItemsRemoteRepository {

    override suspend fun getItems(
        searchTerm: String?,
        categoryId: String?,
        tagId: String?
    ): List<ItemDomainModel> {
        return remoteSource.getItems(searchTerm, categoryId, tagId).map { it.toDomain() }
    }

    override suspend fun createItem(
        name: String,
        description: String?,
        imageFile: File,
        categoryIds: List<String>,
        tagIds: List<String>
    ): ItemDomainModel {
        val response = remoteSource.createItem(name, description, imageFile, categoryIds, tagIds)
        return response.toDomain()
    }

    override suspend fun updateItem(
        id: String,
        name: String,
        description: String?,
        categoryIds: List<String>,
        tagIds: List<String>
    ): ItemDomainModel {
        val updateModel = UpdateItemApiModel(
            name = name,
            description = description,
            categoryIds = categoryIds,
            tagIds = tagIds
        )
        val response = remoteSource.updateItem(id, updateModel)
        return response.toDomain()
    }

    override suspend fun deleteItem(id: String) {
        remoteSource.deleteItem(id)
    }

    override suspend fun reprocessMask(id: String, contourJson: String): String {
        return remoteSource.reprocessMask(id, contourJson)
    }

    override suspend fun restoreAutoMask(id: String): String {
        return remoteSource.restoreAutoMask(id)
    }
}