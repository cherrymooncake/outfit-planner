package com.cherrymooncake.wardrobe_android.feature.wardrobe.data.source

import com.cherrymooncake.wardrobe_android.feature.wardrobe.data.api.IItemsApi
import com.cherrymooncake.wardrobe_android.feature.wardrobe.data.model.ItemApiModel
import com.cherrymooncake.wardrobe_android.feature.wardrobe.data.model.ManualMaskApiModel
import com.cherrymooncake.wardrobe_android.feature.wardrobe.data.model.UpdateItemApiModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class ItemsRemoteSourceImpl(
    private val api: IItemsApi
) : IItemsRemoteSource {

    override suspend fun getItems(
        searchTerm: String?,
        categoryId: String?,
        tagId: String?
    ): List<ItemApiModel> {
        return api.getItems(searchTerm, categoryId, tagId)
    }

    override suspend fun createItem(
        name: String,
        description: String?,
        imageFile: File,
        categoryIds: List<String>,
        tagIds: List<String>
    ): ItemApiModel {

        val builder = MultipartBody.Builder().setType(MultipartBody.FORM)

        builder.addFormDataPart("Name", name)

        if (!description.isNullOrBlank()) {
            builder.addFormDataPart("Description", description)
        }

        categoryIds.forEach { id ->
            builder.addFormDataPart("CategoryIds", id)
        }

        tagIds.forEach { id ->
            builder.addFormDataPart("TagIds", id)
        }

        val requestFile = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
        builder.addFormDataPart("Image", imageFile.name, requestFile)

        return api.createItem(builder.build())
    }

    override suspend fun updateItem(id: String, model: UpdateItemApiModel): ItemApiModel {
        return api.updateItem(id, model)
    }

    override suspend fun deleteItem(id: String) {
        api.deleteItem(id)
    }

    override suspend fun reprocessMask(id: String, contourJson: String): String {
        val request = ManualMaskApiModel(contourJson)
        return api.reprocessMask(id, request).url
    }

    override suspend fun restoreAutoMask(id: String): String {
        return api.restoreAutoMask(id).url
    }
}