package com.cherrymooncake.wardrobe_android.feature.wardrobe.data.model

import com.cherrymooncake.wardrobe_android.feature.common.data.model.CategoryApiModel
import com.cherrymooncake.wardrobe_android.feature.common.data.model.TagApiModel
import com.google.gson.annotations.SerializedName

data class ItemApiModel(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String?,
    @SerializedName("originalImageUrl") val originalImageUrl: String,
    @SerializedName("processedImageUrl") val processedImageUrl: String,
    @SerializedName("categories") val categories: List<CategoryApiModel>,
    @SerializedName("tags") val tags: List<TagApiModel>
)