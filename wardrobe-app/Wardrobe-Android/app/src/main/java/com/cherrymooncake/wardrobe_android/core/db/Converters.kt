package com.cherrymooncake.wardrobe_android.core.db

import androidx.room.TypeConverter
import com.cherrymooncake.wardrobe_android.feature.common.data.model.CategoryDbModel
import com.cherrymooncake.wardrobe_android.feature.common.data.model.TagDbModel
import com.cherrymooncake.wardrobe_android.feature.outfits.data.model.OutfitItemLocalModel
import com.cherrymooncake.wardrobe_android.feature.templates.data.model.TemplateItemLocalModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromCategoryList(value: List<CategoryDbModel>?): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toCategoryList(value: String): List<CategoryDbModel> {
        val listType = object : TypeToken<List<CategoryDbModel>>() {}.type
        return gson.fromJson(value, listType) ?: emptyList()
    }

    @TypeConverter
    fun fromTagList(value: List<TagDbModel>?): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toTagList(value: String): List<TagDbModel> {
        val listType = object : TypeToken<List<TagDbModel>>() {}.type
        return gson.fromJson(value, listType) ?: emptyList()
    }

    @TypeConverter
    fun fromOutfitItemList(value: List<OutfitItemLocalModel>?): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toOutfitItemList(value: String): List<OutfitItemLocalModel> {
        val listType = object : TypeToken<List<OutfitItemLocalModel>>() {}.type
        return gson.fromJson(value, listType) ?: emptyList()
    }

    @TypeConverter
    fun fromTemplateItemList(value: List<TemplateItemLocalModel>?): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toTemplateItemList(value: String): List<TemplateItemLocalModel> {
        val listType = object : TypeToken<List<TemplateItemLocalModel>>() {}.type
        return gson.fromJson(value, listType) ?: emptyList()
    }
}