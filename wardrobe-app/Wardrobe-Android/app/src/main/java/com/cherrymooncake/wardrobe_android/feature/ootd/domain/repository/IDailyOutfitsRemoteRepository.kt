package com.cherrymooncake.wardrobe_android.feature.ootd.domain.repository

import com.cherrymooncake.wardrobe_android.feature.ootd.domain.model.AiRecommendationDomainModel
import com.cherrymooncake.wardrobe_android.feature.ootd.domain.model.CityDomainModel
import com.cherrymooncake.wardrobe_android.feature.ootd.domain.model.DailyOutfitDomainModel
import com.cherrymooncake.wardrobe_android.feature.outfits.domain.model.OutfitDomainModel
import com.cherrymooncake.wardrobe_android.feature.ootd.domain.model.WeatherDomainModel

interface IDailyOutfitsRemoteRepository {
    suspend fun getByDate(date: String): DailyOutfitDomainModel
    suspend fun getMonth(year: Int, month: Int): List<DailyOutfitDomainModel>
    suspend fun setOutfit(date: String, outfitId: String): DailyOutfitDomainModel
    suspend fun deleteOutfit(date: String)
    suspend fun getRandomOutfit(): OutfitDomainModel
    suspend fun getWeather(city: String): WeatherDomainModel
    suspend fun getAiRecommendation(prompt: String, weatherContext: String?): AiRecommendationDomainModel
    suspend fun searchCity(query: String): List<CityDomainModel>
}