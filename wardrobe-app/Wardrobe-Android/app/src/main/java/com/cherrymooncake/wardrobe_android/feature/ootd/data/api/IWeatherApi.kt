package com.cherrymooncake.wardrobe_android.feature.ootd.data.api
import com.cherrymooncake.wardrobe_android.feature.ootd.data.model.WeatherApiModel
import retrofit2.http.GET
import retrofit2.http.Query

interface IWeatherApi {
    @GET("weather")
    suspend fun getWeather(@Query("city") city: String): WeatherApiModel
}