package com.cherrymooncake.wardrobe_android.feature.common.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.cherrymooncake.wardrobe_android.feature.common.data.model.TagDbModel

@Dao
interface TagsDao {
    @Query("SELECT * FROM tags")
    suspend fun getAllTags(): List<TagDbModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTags(tags: List<TagDbModel>)

    @Query("DELETE FROM tags")
    suspend fun clearTags()

    @Transaction
    suspend fun replaceAll(tags: List<TagDbModel>) {
        clearTags()
        insertTags(tags)
    }
}