package com.cherrymooncake.wardrobe_android.feature.common.data.source
import com.cherrymooncake.wardrobe_android.feature.common.data.api.ICommonApi
import com.cherrymooncake.wardrobe_android.feature.common.data.model.*

class CommonRemoteSourceImpl(private val api: ICommonApi) : ICommonRemoteSource {
    override suspend fun getCategories() = api.getCategories()
    override suspend fun createCategory(model: CreateCategoryApiModel) = api.createCategory(model)
    override suspend fun updateCategory(id: String, model: UpdateCategoryApiModel) = api.updateCategory(id, model)
    override suspend fun deleteCategory(id: String) = api.deleteCategory(id)

    override suspend fun getTags() = api.getTags()
    override suspend fun createTag(model: CreateTagApiModel) = api.createTag(model)
    override suspend fun updateTag(id: String, model: UpdateTagApiModel) = api.updateTag(id, model)
    override suspend fun deleteTag(id: String) = api.deleteTag(id)
}