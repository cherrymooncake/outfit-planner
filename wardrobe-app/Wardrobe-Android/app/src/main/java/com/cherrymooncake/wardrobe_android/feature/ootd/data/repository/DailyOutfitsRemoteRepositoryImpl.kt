package com.cherrymooncake.wardrobe_android.feature.ootd.data.repository

import com.cherrymooncake.wardrobe_android.feature.ootd.data.api.IDailyOutfitsApi
import com.cherrymooncake.wardrobe_android.feature.ootd.data.api.IOpenMeteoApi
import com.cherrymooncake.wardrobe_android.feature.ootd.data.mapper.toDomain
import com.cherrymooncake.wardrobe_android.feature.ootd.data.model.SetDailyOutfitApiModel
import com.cherrymooncake.wardrobe_android.feature.ootd.domain.model.DailyOutfitDomainModel
import com.cherrymooncake.wardrobe_android.feature.ootd.domain.repository.IDailyOutfitsRemoteRepository
import com.cherrymooncake.wardrobe_android.feature.outfits.data.mapper.toDomain
import com.cherrymooncake.wardrobe_android.feature.outfits.domain.model.*
import com.cherrymooncake.wardrobe_android.feature.ootd.data.api.IWeatherApi
import com.cherrymooncake.wardrobe_android.feature.ootd.data.model.AiRecommendationRequestApiModel
import com.cherrymooncake.wardrobe_android.feature.ootd.domain.model.AiRecommendationDomainModel
import com.cherrymooncake.wardrobe_android.feature.ootd.domain.model.*

class DailyOutfitsRemoteRepositoryImpl(private val api: IDailyOutfitsApi, private val weatherApi: IWeatherApi, private val openMeteoApi: IOpenMeteoApi) : IDailyOutfitsRemoteRepository {
    override suspend fun getByDate(date: String) = api.getByDate(date).toDomain()
    override suspend fun getMonth(year: Int, month: Int) = api.getMonth(year, month).map { it.toDomain() }
    override suspend fun setOutfit(date: String, outfitId: String) = api.setOutfit(SetDailyOutfitApiModel(date, outfitId)).toDomain()
    override suspend fun deleteOutfit(date: String) = api.deleteOutfit(date)
    override suspend fun getRandomOutfit(): OutfitDomainModel = api.getRandomOutfit().toDomain()
    override suspend fun getWeather(city: String): WeatherDomainModel {
        val dto = weatherApi.getWeather(city)
        return WeatherDomainModel(dto.city, dto.temperature, dto.condition, dto.weatherCode, dto.recommendation)
    }
    override suspend fun getAiRecommendation(prompt: String, weatherContext: String?): AiRecommendationDomainModel {
        return api.getAiRecommendation(AiRecommendationRequestApiModel(prompt, weatherContext)).toDomain()
    }
    override suspend fun searchCity(query: String): List<CityDomainModel> {
        val response = openMeteoApi.searchCity(name = query)
        return response.results?.map { CityDomainModel(it.name, it.admin1, it.country) } ?: emptyList()
    }
}