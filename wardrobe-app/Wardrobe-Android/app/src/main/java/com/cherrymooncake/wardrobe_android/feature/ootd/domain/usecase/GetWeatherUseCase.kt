package com.cherrymooncake.wardrobe_android.feature.ootd.domain.usecase

import com.cherrymooncake.wardrobe_android.feature.ootd.domain.repository.IDailyOutfitsRemoteRepository

class GetWeatherUseCase(private val repo: IDailyOutfitsRemoteRepository) {
    suspend operator fun invoke(city: String) = repo.getWeather(city)
}