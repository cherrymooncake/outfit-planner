package com.cherrymooncake.wardrobe_android.feature.ootd.domain.usecase

import com.cherrymooncake.wardrobe_android.feature.ootd.domain.repository.IDailyOutfitsRemoteRepository

class SearchCityUseCase(private val repo: IDailyOutfitsRemoteRepository) {
    suspend operator fun invoke(query: String) = repo.searchCity(query)
}