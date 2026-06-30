package com.cherrymooncake.wardrobe_android.feature.outfits.data.model

import com.cherrymooncake.wardrobe_android.feature.common.data.model.CategoryApiModel
import com.google.gson.annotations.SerializedName

data class OutfitItemApiModel(
    @SerializedName("id") val id: String,
    @SerializedName("itemId") val itemId: String,
    @SerializedName("itemImageUrl") val itemImageUrl: String,
    @SerializedName("x") val x: Double,
    @SerializedName("y") val y: Double,
    @SerializedName("scale") val scale: Double,
    @SerializedName("rotation") val rotation: Double,
    @SerializedName("zIndex") val zIndex: Int
)

data class OutfitApiModel(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String?,
    @SerializedName("canvasWidth") val canvasWidth: Int,
    @SerializedName("canvasHeight") val canvasHeight: Int,
    @SerializedName("templateId") val templateId: String?,
    @SerializedName("templateName") val templateName: String?,
    @SerializedName("items") val items: List<OutfitItemApiModel>,
    @SerializedName("categories") val categories: List<CategoryApiModel>
)

data class OutfitItemPayloadApiModel(
    @SerializedName("itemId") val itemId: String,
    @SerializedName("x") val x: Double,
    @SerializedName("y") val y: Double,
    @SerializedName("scale") val scale: Double,
    @SerializedName("rotation") val rotation: Double,
    @SerializedName("zIndex") val zIndex: Int
)

data class CreateOutfitApiModel(
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String?,
    @SerializedName("canvasWidth") val canvasWidth: Int,
    @SerializedName("canvasHeight") val canvasHeight: Int,
    @SerializedName("templateId") val templateId: String?,
    @SerializedName("items") val items: List<OutfitItemPayloadApiModel>,
    @SerializedName("categoryIds") val categoryIds: List<String>
)

data class UpdateOutfitApiModel(
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String?,
    @SerializedName("canvasWidth") val canvasWidth: Int,
    @SerializedName("canvasHeight") val canvasHeight: Int,
    @SerializedName("templateId") val templateId: String?,
    @SerializedName("items") val items: List<OutfitItemPayloadApiModel>,
    @SerializedName("categoryIds") val categoryIds: List<String>
)