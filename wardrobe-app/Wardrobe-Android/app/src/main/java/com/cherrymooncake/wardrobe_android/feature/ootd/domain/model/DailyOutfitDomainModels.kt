package com.cherrymooncake.wardrobe_android.feature.ootd.domain.model

import com.cherrymooncake.wardrobe_android.feature.outfits.domain.model.OutfitDomainModel

data class DailyOutfitDomainModel(
    val date: String,
    val outfit: OutfitDomainModel?
)

data class AiRecommendationDomainModel(
    val recommendedOutfitId: String,
    val explanation: String,
    val outfit: OutfitDomainModel
)