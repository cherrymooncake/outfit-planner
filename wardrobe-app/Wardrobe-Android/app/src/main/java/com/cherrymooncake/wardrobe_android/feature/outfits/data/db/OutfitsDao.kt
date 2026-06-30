package com.cherrymooncake.wardrobe_android.feature.outfits.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.cherrymooncake.wardrobe_android.feature.outfits.data.model.OutfitDbModel

@Dao
interface OutfitsDao {
    @Query("SELECT * FROM outfits")
    suspend fun getAllOutfits(): List<OutfitDbModel>

    @Query("SELECT * FROM outfits WHERE id = :id LIMIT 1")
    suspend fun getOutfitById(id: String): OutfitDbModel?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOutfits(outfits: List<OutfitDbModel>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOutfit(outfit: OutfitDbModel)

    @Query("DELETE FROM outfits WHERE id = :id")
    suspend fun deleteOutfit(id: String)

    @Query("DELETE FROM outfits")
    suspend fun clearOutfits()

    @Transaction
    suspend fun replaceAll(outfits: List<OutfitDbModel>) {
        clearOutfits()
        insertOutfits(outfits)
    }
}