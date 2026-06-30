package com.cherrymooncake.wardrobe_android.feature.common.data.source

import com.cherrymooncake.wardrobe_android.feature.common.data.db.CategoriesDao
import com.cherrymooncake.wardrobe_android.feature.common.data.db.TagsDao
import com.cherrymooncake.wardrobe_android.feature.common.data.model.CategoryDbModel
import com.cherrymooncake.wardrobe_android.feature.common.data.model.TagDbModel

class CommonLocalSourceImpl(
    private val categoriesDao: CategoriesDao,
    private val tagsDao: TagsDao
) : ICommonLocalSource {

    override suspend fun getCategories(): List<CategoryDbModel> {
        return categoriesDao.getAllCategories()
    }

    override suspend fun saveCategories(categories: List<CategoryDbModel>) {
        categoriesDao.replaceAll(categories)
    }

    override suspend fun getTags(): List<TagDbModel> {
        return tagsDao.getAllTags()
    }

    override suspend fun saveTags(tags: List<TagDbModel>) {
        tagsDao.replaceAll(tags)
    }
}