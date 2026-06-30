package com.cherrymooncake.wardrobe_android.feature.ootd.presentation.mvi

import com.cherrymooncake.wardrobe_android.core.mvi.ISingleFlowEvent
import com.cherrymooncake.wardrobe_android.core.mvi.IUiIntent
import com.cherrymooncake.wardrobe_android.core.mvi.IUiState
import com.cherrymooncake.wardrobe_android.feature.outfits.presentation.model.OutfitUiModel
import java.time.YearMonth

data class CalendarState(
    val isLoading: Boolean = true,
    val currentMonth: YearMonth = YearMonth.now(),
    val monthData: Map<String, OutfitUiModel> = emptyMap()
) : IUiState

sealed class CalendarIntent : IUiIntent {
    data class LoadMonth(val yearMonth: YearMonth) : CalendarIntent()
    object NextMonth : CalendarIntent()
    object PrevMonth : CalendarIntent()
}

sealed class CalendarEvent : ISingleFlowEvent {
    data class ShowSnackbar(val message: String) : CalendarEvent()
}