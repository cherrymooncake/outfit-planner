package com.cherrymooncake.wardrobe_android.feature.wardrobe.data.model

import com.google.gson.annotations.SerializedName

data class UpdateItemApiModel(
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String?,
    @SerializedName("categoryIds") val categoryIds: List<String>,
    @SerializedName("tagIds") val tagIds: List<String>
)