package com.cherrymooncake.wardrobe_android.core.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.cherrymooncake.wardrobe_android.feature.common.data.db.CategoriesDao
import com.cherrymooncake.wardrobe_android.feature.common.data.db.TagsDao
import com.cherrymooncake.wardrobe_android.feature.common.data.model.CategoryDbModel
import com.cherrymooncake.wardrobe_android.feature.common.data.model.TagDbModel
import com.cherrymooncake.wardrobe_android.feature.outfits.data.db.OutfitsDao
import com.cherrymooncake.wardrobe_android.feature.outfits.data.model.OutfitDbModel
import com.cherrymooncake.wardrobe_android.feature.templates.data.db.TemplatesDao
import com.cherrymooncake.wardrobe_android.feature.templates.data.model.TemplateDbModel
import com.cherrymooncake.wardrobe_android.feature.wardrobe.data.db.ItemsDao
import com.cherrymooncake.wardrobe_android.feature.wardrobe.data.model.ItemDbModel

@Database(
    entities =[
        CategoryDbModel::class,
        TagDbModel::class,
        ItemDbModel::class,
        OutfitDbModel::class,
        TemplateDbModel::class
    ],
    version = 5,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class WardrobeDB : RoomDatabase() {
    abstract fun categoriesDao(): CategoriesDao
    abstract fun tagsDao(): TagsDao
    abstract fun itemsDao(): ItemsDao
    abstract fun outfitsDao(): OutfitsDao
    abstract fun templatesDao(): TemplatesDao
}