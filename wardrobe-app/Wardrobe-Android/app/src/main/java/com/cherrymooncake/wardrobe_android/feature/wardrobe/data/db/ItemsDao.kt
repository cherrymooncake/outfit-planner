package com.cherrymooncake.wardrobe_android.feature.wardrobe.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.cherrymooncake.wardrobe_android.feature.wardrobe.data.model.ItemDbModel

@Dao
interface ItemsDao {

    @Query("SELECT * FROM items")
    suspend fun getAllItems(): List<ItemDbModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItems(items: List<ItemDbModel>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: ItemDbModel)

    @Query("DELETE FROM items WHERE id = :itemId")
    suspend fun deleteItem(itemId: String)

    @Query("DELETE FROM items")
    suspend fun clearItems()

    @Transaction
    suspend fun replaceAll(items: List<ItemDbModel>) {
        clearItems()
        insertItems(items)
    }

    @Query("SELECT * FROM items WHERE id = :itemId LIMIT 1")
    suspend fun getItemById(itemId: String): ItemDbModel?
}