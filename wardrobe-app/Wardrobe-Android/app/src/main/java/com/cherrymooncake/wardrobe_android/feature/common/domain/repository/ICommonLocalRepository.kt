package com.cherrymooncake.wardrobe_android.feature.common.domain.repository

import com.cherrymooncake.wardrobe_android.feature.common.domain.model.CategoryDomainModel
import com.cherrymooncake.wardrobe_android.feature.common.domain.model.TagDomainModel

interface ICommonLocalRepository {
    suspend fun getCategories(): List<CategoryDomainModel>
    suspend fun saveCategories(categories: List<CategoryDomainModel>)

    suspend fun getTags(): List<TagDomainModel>
    suspend fun saveTags(tags: List<TagDomainModel>)
}