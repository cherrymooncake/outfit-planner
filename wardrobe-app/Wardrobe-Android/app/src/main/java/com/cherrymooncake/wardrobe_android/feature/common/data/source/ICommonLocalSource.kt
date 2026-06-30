package com.cherrymooncake.wardrobe_android.feature.common.data.source

import com.cherrymooncake.wardrobe_android.feature.common.data.model.CategoryDbModel
import com.cherrymooncake.wardrobe_android.feature.common.data.model.TagDbModel

interface ICommonLocalSource {
    suspend fun getCategories(): List<CategoryDbModel>
    suspend fun saveCategories(categories: List<CategoryDbModel>)

    suspend fun getTags(): List<TagDbModel>
    suspend fun saveTags(tags: List<TagDbModel>)
}