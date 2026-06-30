package com.cherrymooncake.wardrobe_android.feature.ootd.data.api

import com.cherrymooncake.wardrobe_android.feature.ootd.data.model.GeocodingResponseApiModel
import retrofit2.http.GET
import retrofit2.http.Query

interface IOpenMeteoApi {
    @GET("v1/search")
    suspend fun searchCity(
        @Query("name") name: String,
        @Query("count") count: Int = 5,
        @Query("language") language: String = "ru",
        @Query("format") format: String = "json"
    ): GeocodingResponseApiModel
}