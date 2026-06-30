package com.cherrymooncake.wardrobe_android.feature.ootd.domain.usecase

import com.cherrymooncake.wardrobe_android.feature.ootd.domain.model.AiRecommendationDomainModel
import com.cherrymooncake.wardrobe_android.feature.ootd.domain.repository.IDailyOutfitsRemoteRepository

class GetDailyOutfitUseCase(private val repo: IDailyOutfitsRemoteRepository) {
    suspend operator fun invoke(date: String) = repo.getByDate(date)
}

class GetMonthOutfitsUseCase(private val repo: IDailyOutfitsRemoteRepository) {
    suspend operator fun invoke(year: Int, month: Int) = repo.getMonth(year, month)
}

class SetDailyOutfitUseCase(private val repo: IDailyOutfitsRemoteRepository) {
    suspend operator fun invoke(date: String, outfitId: String) = repo.setOutfit(date, outfitId)
}

class DeleteDailyOutfitUseCase(private val repo: IDailyOutfitsRemoteRepository) {
    suspend operator fun invoke(date: String) = repo.deleteOutfit(date)
}

class GetRandomOutfitUseCase(private val repo: IDailyOutfitsRemoteRepository) {
    suspend operator fun invoke() = repo.getRandomOutfit()
}

class GetAiRecommendationUseCase(private val repo: IDailyOutfitsRemoteRepository) {
    suspend operator fun invoke(prompt: String, weatherContext: String?): AiRecommendationDomainModel {
        return repo.getAiRecommendation(prompt, weatherContext)
    }
}