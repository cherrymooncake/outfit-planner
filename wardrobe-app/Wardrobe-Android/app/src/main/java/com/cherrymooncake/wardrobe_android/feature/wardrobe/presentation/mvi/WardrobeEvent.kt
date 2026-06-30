package com.cherrymooncake.wardrobe_android.feature.wardrobe.presentation.mvi

import com.cherrymooncake.wardrobe_android.core.mvi.ISingleFlowEvent

sealed class WardrobeEvent : ISingleFlowEvent {
    data class ShowSnackbar(val message: String) : WardrobeEvent()
}