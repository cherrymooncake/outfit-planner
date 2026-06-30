package com.cherrymooncake.wardrobe_android.feature.wardrobe.data.source

import com.cherrymooncake.wardrobe_android.feature.wardrobe.data.model.ItemApiModel
import com.cherrymooncake.wardrobe_android.feature.wardrobe.data.model.UpdateItemApiModel
import java.io.File

interface IItemsRemoteSource {
    suspend fun getItems(
        searchTerm: String?,
        categoryId: String?,
        tagId: String?
    ): List<ItemApiModel>

    suspend fun createItem(
        name: String,
        description: String?,
        imageFile: File,
        categoryIds: List<String>,
        tagIds: List<String>
    ): ItemApiModel

    suspend fun updateItem(id: String, model: UpdateItemApiModel): ItemApiModel

    suspend fun deleteItem(id: String)

    suspend fun reprocessMask(id: String, contourJson: String): String
    suspend fun restoreAutoMask(id: String): String
}