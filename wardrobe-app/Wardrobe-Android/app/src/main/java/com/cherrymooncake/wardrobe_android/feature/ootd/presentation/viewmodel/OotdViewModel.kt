package com.cherrymooncake.wardrobe_android.feature.ootd.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.cherrymooncake.wardrobe_android.core.mvi.BaseViewModel
import com.cherrymooncake.wardrobe_android.core.network.source.ITokenLocalSource
import com.cherrymooncake.wardrobe_android.feature.ootd.domain.usecase.*
import com.cherrymooncake.wardrobe_android.feature.ootd.presentation.mvi.*
import com.cherrymooncake.wardrobe_android.feature.outfits.domain.usecase.SyncOutfitsUseCase
import com.cherrymooncake.wardrobe_android.feature.outfits.presentation.mapper.toUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject
import kotlin.math.roundToInt

@HiltViewModel
class OotdViewModel @Inject constructor(
    private val getDailyOutfitUseCase: GetDailyOutfitUseCase,
    private val setDailyOutfitUseCase: SetDailyOutfitUseCase,
    private val deleteDailyOutfitUseCase: DeleteDailyOutfitUseCase,
    private val getRandomOutfitUseCase: GetRandomOutfitUseCase,
    private val syncOutfitsUseCase: SyncOutfitsUseCase,
    private val getWeatherUseCase: GetWeatherUseCase,
    private val getAiRecommendationUseCase: GetAiRecommendationUseCase,
    private val searchCityUseCase: SearchCityUseCase,
    private val tokenSource: ITokenLocalSource
) : BaseViewModel<OotdState, OotdIntent, OotdEvent>(OotdState()) {

    private var citySearchJob: Job? = null

    override fun processIntent(intent: OotdIntent) {
        when (intent) {
            is OotdIntent.LoadData -> loadData(intent.date)
            is OotdIntent.LoadAllOutfits -> loadAllOutfits()
            is OotdIntent.SetSelectDialogVisible -> updateState { copy(showSelectDialog = intent.isVisible) }
            is OotdIntent.SelectOutfit -> setOutfit(intent.outfitId)
            is OotdIntent.SetRandomOutfit -> setRandomOutfit()
            is OotdIntent.ClearOutfit -> clearOutfit()

            is OotdIntent.LoadSavedCity -> loadSavedCity()
            is OotdIntent.LoadWeather -> loadWeather(intent.city)
            is OotdIntent.SaveCity -> saveCity(intent.city)

            is OotdIntent.SetCityDialogVisible -> {
                updateState {
                    copy(
                        showCityDialog = intent.isVisible,
                        citySearchQuery = if (intent.isVisible) savedCity ?: "" else "",
                        citySearchResults = emptyList()
                    )
                }
            }

            is OotdIntent.UpdateCitySearchQuery -> {
                updateState { copy(citySearchQuery = intent.query) }
                searchCityDebounced(intent.query)
            }

            is OotdIntent.SelectCitySuggestion -> {
                updateState { copy(citySearchQuery = intent.cityName, citySearchResults = emptyList()) }
            }

            is OotdIntent.SetAiDialogVisible -> updateState {
                copy(showAiDialog = intent.isVisible, aiExplanation = null, aiRecommendedOutfit = null)
            }
            is OotdIntent.UpdateAiPrompt -> updateState { copy(aiPrompt = intent.prompt) }
            is OotdIntent.AskAiStylist -> askAiStylist()
            is OotdIntent.ApplyAiRecommendation -> applyAiRecommendation()
        }
    }

    private fun loadData(dateStr: String) {
        val target = if (dateStr.isBlank() || dateStr == "{date}") LocalDate.now().toString() else dateStr

        viewModelScope.launch {
            updateState { copy(isLoading = true, targetDate = target, weather = null, weatherError = null) }
            try {
                val daily = getDailyOutfitUseCase(target)
                updateState { copy(outfit = daily.outfit?.toUi(), isLoading = false) }
            } catch (e: Exception) {
                updateState { copy(outfit = null, isLoading = false) }
            }
        }
    }

    private fun loadAllOutfits() {
        viewModelScope.launch {
            updateState { copy(isDialogLoading = true, showSelectDialog = true) }
            try {
                val outfits = syncOutfitsUseCase().firstOrNull() ?: emptyList()
                updateState { copy(allOutfits = outfits.map { it.toUi() }, isDialogLoading = false) }
            } catch (e: Exception) {
                sendEvent(OotdEvent.ShowSnackbar("Ошибка загрузки образов"))
                updateState { copy(isDialogLoading = false) }
            }
        }
    }

    private fun setOutfit(outfitId: String) {
        viewModelScope.launch {
            updateState { copy(isLoading = true, showSelectDialog = false) }
            try {
                val daily = setDailyOutfitUseCase(state.value.targetDate, outfitId)
                updateState { copy(outfit = daily.outfit?.toUi(), isLoading = false) }
                sendEvent(OotdEvent.ShowSnackbar("Образ установлен!"))
            } catch (e: Exception) {
                sendEvent(OotdEvent.ShowSnackbar("Ошибка установки образа"))
                updateState { copy(isLoading = false) }
            }
        }
    }

    private fun setRandomOutfit() {
        viewModelScope.launch {
            updateState { copy(isLoading = true) }
            try {
                val random = getRandomOutfitUseCase()
                val daily = setDailyOutfitUseCase(state.value.targetDate, random.id)
                updateState { copy(outfit = daily.outfit?.toUi(), isLoading = false) }
                sendEvent(OotdEvent.ShowSnackbar("Случайный образ подобран ✨"))
            } catch (e: Exception) {
                sendEvent(OotdEvent.ShowSnackbar("Нет сохраненных образов для рандома"))
                updateState { copy(isLoading = false) }
            }
        }
    }

    private fun clearOutfit() {
        viewModelScope.launch {
            updateState { copy(isLoading = true) }
            try {
                deleteDailyOutfitUseCase(state.value.targetDate)
                updateState { copy(outfit = null, isLoading = false) }
                sendEvent(OotdEvent.ShowSnackbar("Образ убран с этого дня"))
            } catch (e: Exception) {
                sendEvent(OotdEvent.ShowSnackbar("Ошибка удаления"))
                updateState { copy(isLoading = false) }
            }
        }
    }


    private fun loadSavedCity() {
        val city = tokenSource.getCity()
        updateState { copy(savedCity = city) }

        if (!city.isNullOrBlank()) {
            processIntent(OotdIntent.LoadWeather(city))
        } else {
            updateState { copy(isWeatherLoading = false, weather = null, weatherError = null) }
        }
    }

    private fun saveCity(city: String) {
        tokenSource.saveCity(city)
        updateState { copy(savedCity = city, showCityDialog = false, citySearchResults = emptyList()) }
        processIntent(OotdIntent.LoadWeather(city))
    }

    private fun loadWeather(city: String) {
        viewModelScope.launch {
            updateState { copy(isWeatherLoading = true, weatherError = null) }
            try {
                val weather = getWeatherUseCase(city)
                tokenSource.saveCity(weather.city)
                updateState {
                    copy(
                        weather = weather,
                        savedCity = weather.city,
                        isWeatherLoading = false
                    )
                }
            } catch (e: Exception) {
                updateState {
                    copy(
                        weatherError = "Не удалось найти город или получить прогноз",
                        isWeatherLoading = false,
                        weather = null
                    )
                }
            }
        }
    }


    private fun searchCityDebounced(query: String) {
        citySearchJob?.cancel()
        if (query.trim().length < 2) {
            updateState { copy(citySearchResults = emptyList(), isCitySearching = false) }
            return
        }

        citySearchJob = viewModelScope.launch {
            delay(400)
            updateState { copy(isCitySearching = true) }
            try {
                val results = searchCityUseCase(query.trim())
                val uiResults = results.map { CityUiModel(it.name, it.region, it.country) }
                updateState { copy(citySearchResults = uiResults, isCitySearching = false) }
            } catch (e: Exception) {
                updateState { copy(citySearchResults = emptyList(), isCitySearching = false) }
            }
        }
    }


    private fun askAiStylist() {
        val st = state.value
        if (st.aiPrompt.isBlank()) return

        viewModelScope.launch {
            updateState { copy(isAiLoading = true) }
            try {
                val weatherStr = st.weather?.let { "${it.temperature.roundToInt()}°C, ${it.condition}" }
                val result = getAiRecommendationUseCase(st.aiPrompt, weatherStr)

                updateState {
                    copy(
                        isAiLoading = false,
                        aiExplanation = result.explanation,
                        aiRecommendedOutfit = result.outfit.toUi()
                    )
                }
            } catch (e: Exception) {
                updateState { copy(isAiLoading = false) }
                sendEvent(OotdEvent.ShowSnackbar(e.localizedMessage ?: "Ошибка ИИ-стилиста"))
            }
        }
    }

    private fun applyAiRecommendation() {
        val outfitId = state.value.aiRecommendedOutfit?.id ?: return
        setOutfit(outfitId)
        updateState { copy(showAiDialog = false, aiPrompt = "", aiExplanation = null, aiRecommendedOutfit = null) }
    }
}