package com.cherrymooncake.wardrobe_android.feature.ootd.presentation.mvi

import com.cherrymooncake.wardrobe_android.core.mvi.ISingleFlowEvent
import com.cherrymooncake.wardrobe_android.core.mvi.IUiIntent
import com.cherrymooncake.wardrobe_android.core.mvi.IUiState
import com.cherrymooncake.wardrobe_android.feature.ootd.domain.model.WeatherDomainModel
import com.cherrymooncake.wardrobe_android.feature.outfits.presentation.model.OutfitUiModel

data class CityUiModel(val name: String, val region: String?, val country: String?)

data class OotdState(
    val isLoading: Boolean = true,
    val isDialogLoading: Boolean = false,
    val targetDate: String = "",
    val outfit: OutfitUiModel? = null,
    val showSelectDialog: Boolean = false,
    val allOutfits: List<OutfitUiModel> = emptyList(),

    val weather: WeatherDomainModel? = null,
    val isWeatherLoading: Boolean = false,
    val weatherError: String? = null,
    val savedCity: String? = null,
    val showCityDialog: Boolean = false,

    val showAiDialog: Boolean = false,
    val isAiLoading: Boolean = false,
    val aiPrompt: String = "",
    val aiExplanation: String? = null,
    val aiRecommendedOutfit: OutfitUiModel? = null,

    val citySearchQuery: String = "",
    val citySearchResults: List<CityUiModel> = emptyList(),
    val isCitySearching: Boolean = false,
) : IUiState

sealed class OotdIntent : IUiIntent {
    data class LoadData(val date: String) : OotdIntent()
    object LoadAllOutfits : OotdIntent()
    data class SetSelectDialogVisible(val isVisible: Boolean) : OotdIntent()

    data class SelectOutfit(val outfitId: String) : OotdIntent()
    object SetRandomOutfit : OotdIntent()
    object ClearOutfit : OotdIntent()

    data class LoadWeather(val city: String) : OotdIntent()
    object LoadSavedCity : OotdIntent()
    data class SaveCity(val city: String) : OotdIntent()
    data class SetCityDialogVisible(val isVisible: Boolean) : OotdIntent()
    data class SetAiDialogVisible(val isVisible: Boolean) : OotdIntent()
    data class UpdateAiPrompt(val prompt: String) : OotdIntent()
    object AskAiStylist : OotdIntent()
    object ApplyAiRecommendation : OotdIntent()

    data class UpdateCitySearchQuery(val query: String) : OotdIntent()
    data class SelectCitySuggestion(val cityName: String) : OotdIntent()
}

sealed class OotdEvent : ISingleFlowEvent {
    data class ShowSnackbar(val message: String) : OotdEvent()
}