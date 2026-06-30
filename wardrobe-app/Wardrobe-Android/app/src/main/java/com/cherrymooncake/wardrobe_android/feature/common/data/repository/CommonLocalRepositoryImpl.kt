package com.cherrymooncake.wardrobe_android.feature.common.data.repository

import com.cherrymooncake.wardrobe_android.feature.common.data.mapper.toDbModel
import com.cherrymooncake.wardrobe_android.feature.common.data.mapper.toDomain
import com.cherrymooncake.wardrobe_android.feature.common.data.source.ICommonLocalSource
import com.cherrymooncake.wardrobe_android.feature.common.domain.model.CategoryDomainModel
import com.cherrymooncake.wardrobe_android.feature.common.domain.model.TagDomainModel
import com.cherrymooncake.wardrobe_android.feature.common.domain.repository.ICommonLocalRepository

class CommonLocalRepositoryImpl(
    private val localSource: ICommonLocalSource
) : ICommonLocalRepository {

    override suspend fun getCategories(): List<CategoryDomainModel> {
        return localSource.getCategories().map { it.toDomain() }
    }

    override suspend fun saveCategories(categories: List<CategoryDomainModel>) {
        localSource.saveCategories(categories.map { it.toDbModel() })
    }

    override suspend fun getTags(): List<TagDomainModel> {
        return localSource.getTags().map { it.toDomain() }
    }

    override suspend fun saveTags(tags: List<TagDomainModel>) {
        localSource.saveTags(tags.map { it.toDbModel() })
    }
}