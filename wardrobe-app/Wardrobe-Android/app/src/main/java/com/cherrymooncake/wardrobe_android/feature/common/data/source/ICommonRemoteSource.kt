package com.cherrymooncake.wardrobe_android.feature.common.data.source
import com.cherrymooncake.wardrobe_android.feature.common.data.model.*

interface ICommonRemoteSource {
    suspend fun getCategories(): List<CategoryApiModel>
    suspend fun createCategory(model: CreateCategoryApiModel): CategoryApiModel
    suspend fun updateCategory(id: String, model: UpdateCategoryApiModel): CategoryApiModel
    suspend fun deleteCategory(id: String)

    suspend fun getTags(): List<TagApiModel>
    suspend fun createTag(model: CreateTagApiModel): TagApiModel
    suspend fun updateTag(id: String, model: UpdateTagApiModel): TagApiModel
    suspend fun deleteTag(id: String)
}