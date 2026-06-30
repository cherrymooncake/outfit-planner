package com.cherrymooncake.wardrobe_android.feature.ootd.data.mapper

import com.cherrymooncake.wardrobe_android.feature.ootd.data.model.DailyOutfitApiModel
import com.cherrymooncake.wardrobe_android.feature.ootd.domain.model.DailyOutfitDomainModel
import com.cherrymooncake.wardrobe_android.feature.outfits.data.mapper.toDomain
import com.cherrymooncake.wardrobe_android.feature.ootd.data.model.AiRecommendationResponseApiModel
import com.cherrymooncake.wardrobe_android.feature.ootd.domain.model.AiRecommendationDomainModel

fun DailyOutfitApiModel.toDomain() = DailyOutfitDomainModel(
    date = date,
    outfit = outfit?.toDomain()
)

fun AiRecommendationResponseApiModel.toDomain() = AiRecommendationDomainModel(
    recommendedOutfitId = recommendedOutfitId,
    explanation = explanation,
    outfit = outfit.toDomain()
)