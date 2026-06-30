package com.cherrymooncake.wardrobe_android.feature.outfits.data.api

import com.cherrymooncake.wardrobe_android.feature.outfits.data.model.CreateOutfitApiModel
import com.cherrymooncake.wardrobe_android.feature.outfits.data.model.OutfitApiModel
import com.cherrymooncake.wardrobe_android.feature.outfits.data.model.UpdateOutfitApiModel
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface IOutfitsApi {
    @GET("outfits")
    suspend fun getOutfits(
        @Query("SearchTerm") searchTerm: String? = null,
        @Query("CategoryId") categoryId: String? = null
    ): List<OutfitApiModel>

    @GET("outfits/{id}")
    suspend fun getOutfitById(@Path("id") id: String): OutfitApiModel

    @POST("outfits")
    suspend fun createOutfit(@Body body: CreateOutfitApiModel): OutfitApiModel

    @PUT("outfits/{id}")
    suspend fun updateOutfit(
        @Path("id") id: String,
        @Body body: UpdateOutfitApiModel
    ): OutfitApiModel

    @DELETE("outfits/{id}")
    suspend fun deleteOutfit(@Path("id") id: String)
}