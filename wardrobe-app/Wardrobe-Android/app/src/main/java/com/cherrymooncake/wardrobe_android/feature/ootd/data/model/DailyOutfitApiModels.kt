package com.cherrymooncake.wardrobe_android.feature.ootd.data.model

import com.cherrymooncake.wardrobe_android.feature.outfits.data.model.OutfitApiModel
import com.google.gson.annotations.SerializedName

data class DailyOutfitApiModel(
    @SerializedName("date") val date: String,
    @SerializedName("outfit") val outfit: OutfitApiModel?
)

data class SetDailyOutfitApiModel(
    @SerializedName("date") val date: String,
    @SerializedName("outfitId") val outfitId: String
)

data class AiRecommendationRequestApiModel(
    @SerializedName("prompt") val prompt: String,
    @SerializedName("weatherContext") val weatherContext: String?
)

data class AiRecommendationResponseApiModel(
    @SerializedName("recommendedOutfitId") val recommendedOutfitId: String,
    @SerializedName("explanation") val explanation: String,
    @SerializedName("outfit") val outfit: OutfitApiModel
)