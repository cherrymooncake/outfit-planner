package com.cherrymooncake.wardrobe_android.feature.ootd.data.api

import com.cherrymooncake.wardrobe_android.feature.ootd.data.model.AiRecommendationRequestApiModel
import com.cherrymooncake.wardrobe_android.feature.ootd.data.model.AiRecommendationResponseApiModel
import com.cherrymooncake.wardrobe_android.feature.ootd.data.model.DailyOutfitApiModel
import com.cherrymooncake.wardrobe_android.feature.ootd.data.model.SetDailyOutfitApiModel
import com.cherrymooncake.wardrobe_android.feature.outfits.data.model.OutfitApiModel
import retrofit2.http.*

interface IDailyOutfitsApi {
    @GET("daily-outfits/{date}")
    suspend fun getByDate(@Path("date") date: String): DailyOutfitApiModel

    @GET("daily-outfits/month")
    suspend fun getMonth(@Query("year") year: Int, @Query("month") month: Int): List<DailyOutfitApiModel>

    @POST("daily-outfits")
    suspend fun setOutfit(@Body body: SetDailyOutfitApiModel): DailyOutfitApiModel

    @DELETE("daily-outfits/{date}")
    suspend fun deleteOutfit(@Path("date") date: String)

    @GET("daily-outfits/random")
    suspend fun getRandomOutfit(): OutfitApiModel

    @POST("daily-outfits/recommend")
    suspend fun getAiRecommendation(@Body body: AiRecommendationRequestApiModel): AiRecommendationResponseApiModel
}