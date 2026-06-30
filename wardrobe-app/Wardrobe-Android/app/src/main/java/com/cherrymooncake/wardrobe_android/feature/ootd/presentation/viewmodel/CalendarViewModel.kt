package com.cherrymooncake.wardrobe_android.feature.ootd.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.cherrymooncake.wardrobe_android.core.mvi.BaseViewModel
import com.cherrymooncake.wardrobe_android.feature.ootd.domain.usecase.GetMonthOutfitsUseCase
import com.cherrymooncake.wardrobe_android.feature.ootd.presentation.mvi.*
import com.cherrymooncake.wardrobe_android.feature.outfits.presentation.mapper.toUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.YearMonth
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val getMonthOutfitsUseCase: GetMonthOutfitsUseCase
) : BaseViewModel<CalendarState, CalendarIntent, CalendarEvent>(CalendarState()) {

    init { processIntent(CalendarIntent.LoadMonth(YearMonth.now())) }

    override fun processIntent(intent: CalendarIntent) {
        when (intent) {
            is CalendarIntent.LoadMonth -> loadMonth(intent.yearMonth)
            is CalendarIntent.NextMonth -> {
                val next = state.value.currentMonth.plusMonths(1)
                loadMonth(next)
            }
            is CalendarIntent.PrevMonth -> {
                val prev = state.value.currentMonth.minusMonths(1)
                loadMonth(prev)
            }
        }
    }

    private fun loadMonth(yearMonth: YearMonth) {
        viewModelScope.launch {
            updateState { copy(isLoading = true, currentMonth = yearMonth) }
            try {
                val data = getMonthOutfitsUseCase(yearMonth.year, yearMonth.monthValue)
                val map = data.mapNotNull {
                    if (it.outfit != null) it.date to it.outfit.toUi() else null
                }.toMap()
                updateState { copy(monthData = map, isLoading = false) }
            } catch (e: Exception) {
                updateState { copy(isLoading = false, monthData = emptyMap()) }
                sendEvent(CalendarEvent.ShowSnackbar("Ошибка загрузки календаря"))
            }
        }
    }
}