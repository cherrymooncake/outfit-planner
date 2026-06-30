package com.cherrymooncake.wardrobe_android.feature.templates.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.cherrymooncake.wardrobe_android.feature.templates.data.model.TemplateDbModel

@Dao
interface TemplatesDao {
    @Query("SELECT * FROM templates")
    suspend fun getAllTemplates(): List<TemplateDbModel>

    @Query("SELECT * FROM templates WHERE id = :id LIMIT 1")
    suspend fun getTemplateById(id: String): TemplateDbModel?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTemplates(templates: List<TemplateDbModel>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTemplate(template: TemplateDbModel)

    @Query("DELETE FROM templates WHERE id = :id")
    suspend fun deleteTemplate(id: String)

    @Query("DELETE FROM templates")
    suspend fun clearTemplates()

    @Transaction
    suspend fun replaceAll(templates: List<TemplateDbModel>) {
        clearTemplates()
        insertTemplates(templates)
    }
}