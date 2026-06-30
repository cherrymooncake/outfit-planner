package com.cherrymooncake.wardrobe_android.feature.wardrobe.presentation.mvi.mask

import com.cherrymooncake.wardrobe_android.core.mvi.ISingleFlowEvent

sealed class MaskEditorEvent : ISingleFlowEvent {
    data class ShowSnackbar(val message: String) : MaskEditorEvent()
    object NavigateBack : MaskEditorEvent()
}