package com.cherrymooncake.wardrobe_android.feature.common.data.repository

import com.cherrymooncake.wardrobe_android.feature.common.data.mapper.toDomain
import com.cherrymooncake.wardrobe_android.feature.common.data.model.*
import com.cherrymooncake.wardrobe_android.feature.common.data.source.ICommonRemoteSource
import com.cherrymooncake.wardrobe_android.feature.common.domain.model.CategoryDomainModel
import com.cherrymooncake.wardrobe_android.feature.common.domain.model.TagDomainModel
import com.cherrymooncake.wardrobe_android.feature.common.domain.repository.ICommonRemoteRepository

class CommonRemoteRepositoryImpl(
    private val remoteSource: ICommonRemoteSource
) : ICommonRemoteRepository {

    override suspend fun getCategories() = remoteSource.getCategories().map { it.toDomain() }
    override suspend fun createCategory(name: String, isOutfit: Boolean, isItem: Boolean): CategoryDomainModel {
        return remoteSource.createCategory(CreateCategoryApiModel(name, isOutfit, isItem)).toDomain()
    }
    override suspend fun updateCategory(id: String, name: String): CategoryDomainModel {
        return remoteSource.updateCategory(id, UpdateCategoryApiModel(name)).toDomain()
    }
    override suspend fun deleteCategory(id: String) = remoteSource.deleteCategory(id)

    override suspend fun getTags() = remoteSource.getTags().map { it.toDomain() }
    override suspend fun createTag(name: String, isOutfit: Boolean, isItem: Boolean): TagDomainModel {
        return remoteSource.createTag(CreateTagApiModel(name, isOutfit, isItem)).toDomain()
    }
    override suspend fun updateTag(id: String, name: String): TagDomainModel {
        return remoteSource.updateTag(id, UpdateTagApiModel(name)).toDomain()
    }
    override suspend fun deleteTag(id: String) = remoteSource.deleteTag(id)
}