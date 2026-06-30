package com.cherrymooncake.wardrobe_android.feature.auth.presentation.mvi

import com.cherrymooncake.wardrobe_android.core.mvi.ISingleFlowEvent

sealed class AuthEvent : ISingleFlowEvent {
    object NavigateToWardrobe : AuthEvent()
    object NavigateToLogin : AuthEvent()
    data class ShowError(val message: String) : AuthEvent()
    data class ShowSuccess(val message: String) : AuthEvent()
}