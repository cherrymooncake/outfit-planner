package com.cherrymooncake.wardrobe_android.feature.ootd.data.model

import com.google.gson.annotations.SerializedName

data class WeatherApiModel(
    @SerializedName("city") val city: String,
    @SerializedName("temperature") val temperature: Double,
    @SerializedName("condition") val condition: String,
    @SerializedName("weatherCode") val weatherCode: Int,
    @SerializedName("recommendation") val recommendation: String
)

data class GeocodingResponseApiModel(
    @SerializedName("results") val results: List<GeocodingResultApiModel>?
)

data class GeocodingResultApiModel(
    @SerializedName("name") val name: String,
    @SerializedName("admin1") val admin1: String?,
    @SerializedName("country") val country: String?
)