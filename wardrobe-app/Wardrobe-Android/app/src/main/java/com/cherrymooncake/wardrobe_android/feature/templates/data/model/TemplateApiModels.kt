package com.cherrymooncake.wardrobe_android.feature.templates.data.model

import com.google.gson.annotations.SerializedName

data class TemplateItemApiModel(
    @SerializedName("id") val id: String,
    @SerializedName("categoryIdHint") val categoryIdHint: String?,
    @SerializedName("categoryName") val categoryName: String?,
    @SerializedName("x") val x: Double,
    @SerializedName("y") val y: Double,
    @SerializedName("scale") val scale: Double,
    @SerializedName("rotation") val rotation: Double,
    @SerializedName("zIndex") val zIndex: Int
)

data class TemplateApiModel(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String?,
    @SerializedName("items") val items: List<TemplateItemApiModel>
)

data class TemplateItemPayloadApiModel(
    @SerializedName("categoryIdHint") val categoryIdHint: String?,
    @SerializedName("x") val x: Double,
    @SerializedName("y") val y: Double,
    @SerializedName("scale") val scale: Double,
    @SerializedName("rotation") val rotation: Double,
    @SerializedName("zIndex") val zIndex: Int
)

data class CreateTemplateApiModel(
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String?,
    @SerializedName("items") val items: List<TemplateItemPayloadApiModel>
)

data class UpdateTemplateApiModel(
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String?,
    @SerializedName("items") val items: List<TemplateItemPayloadApiModel>
)