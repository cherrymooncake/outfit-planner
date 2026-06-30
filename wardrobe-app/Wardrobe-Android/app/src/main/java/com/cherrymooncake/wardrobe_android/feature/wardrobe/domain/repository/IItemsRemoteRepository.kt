package com.cherrymooncake.wardrobe_android.feature.wardrobe.domain.repository

import com.cherrymooncake.wardrobe_android.feature.wardrobe.domain.model.ItemDomainModel
import java.io.File

interface IItemsRemoteRepository {
    suspend fun getItems(
        searchTerm: String? = null,
        categoryId: String? = null,
        tagId: String? = null
    ): List<ItemDomainModel>

    suspend fun createItem(
        name: String,
        description: String?,
        imageFile: File,
        categoryIds: List<String>,
        tagIds: List<String>
    ): ItemDomainModel

    suspend fun updateItem(
        id: String,
        name: String,
        description: String?,
        categoryIds: List<String>,
        tagIds: List<String>
    ): ItemDomainModel

    suspend fun deleteItem(id: String)

    suspend fun reprocessMask(id: String, contourJson: String): String
    suspend fun restoreAutoMask(id: String): String
}