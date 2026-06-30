package com.cherrymooncake.wardrobe_android.feature.common.data.model

import com.google.gson.annotations.SerializedName

data class CategoryApiModel(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("isOutfitCategory") val isOutfitCategory: Boolean,
    @SerializedName("isItemCategory") val isItemCategory: Boolean
)

data class TagApiModel(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("isOutfitTag") val isOutfitTag: Boolean,
    @SerializedName("isItemTag") val isItemTag: Boolean
)

data class CreateCategoryApiModel(
    @SerializedName("name") val name: String,
    @SerializedName("isOutfitCategory") val isOutfitCategory: Boolean,
    @SerializedName("isItemCategory") val isItemCategory: Boolean
)

data class UpdateCategoryApiModel(
    @SerializedName("name") val name: String
)

data class CreateTagApiModel(
    @SerializedName("name") val name: String,
    @SerializedName("isOutfitTag") val isOutfitTag: Boolean,
    @SerializedName("isItemTag") val isItemTag: Boolean
)

data class UpdateTagApiModel(
    @SerializedName("name") val name: String
)