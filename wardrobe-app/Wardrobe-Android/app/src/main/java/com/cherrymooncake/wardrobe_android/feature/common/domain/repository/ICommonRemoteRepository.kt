package com.cherrymooncake.wardrobe_android.feature.common.domain.repository
import com.cherrymooncake.wardrobe_android.feature.common.domain.model.CategoryDomainModel
import com.cherrymooncake.wardrobe_android.feature.common.domain.model.TagDomainModel

interface ICommonRemoteRepository {
    suspend fun getCategories(): List<CategoryDomainModel>
    suspend fun createCategory(name: String, isOutfit: Boolean, isItem: Boolean): CategoryDomainModel
    suspend fun updateCategory(id: String, name: String): CategoryDomainModel
    suspend fun deleteCategory(id: String)

    suspend fun getTags(): List<TagDomainModel>
    suspend fun createTag(name: String, isOutfit: Boolean, isItem: Boolean): TagDomainModel
    suspend fun updateTag(id: String, name: String): TagDomainModel
    suspend fun deleteTag(id: String)
}