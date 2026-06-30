package com.cherrymooncake.wardrobe_android.feature.common.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.cherrymooncake.wardrobe_android.feature.common.data.model.CategoryDbModel

@Dao
interface CategoriesDao {
    @Query("SELECT * FROM categories")
    suspend fun getAllCategories(): List<CategoryDbModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<CategoryDbModel>)

    @Query("DELETE FROM categories")
    suspend fun clearCategories()

    @Transaction
    suspend fun replaceAll(categories: List<CategoryDbModel>) {
        clearCategories()
        insertCategories(categories)
    }
}