package com.cherrymooncake.wardrobe_android.feature.ootd.domain.model

data class WeatherDomainModel(
    val city: String,
    val temperature: Double,
    val condition: String,
    val weatherCode: Int,
    val recommendation: String
)

data class CityDomainModel(
    val name: String,
    val region: String?,
    val country: String?
)