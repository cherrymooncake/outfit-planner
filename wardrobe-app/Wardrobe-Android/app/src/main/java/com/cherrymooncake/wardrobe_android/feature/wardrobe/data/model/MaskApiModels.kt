package com.cherrymooncake.wardrobe_android.feature.wardrobe.data.model

import com.google.gson.annotations.SerializedName

data class ManualMaskApiModel(
    @SerializedName("contourJson") val contourJson: String
)

data class ImageUrlResponseApiModel(
    @SerializedName("url") val url: String
)