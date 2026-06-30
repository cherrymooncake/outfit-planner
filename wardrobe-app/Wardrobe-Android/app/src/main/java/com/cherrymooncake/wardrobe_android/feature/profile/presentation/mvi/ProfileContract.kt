package com.cherrymooncake.wardrobe_android.feature.profile.presentation.mvi

import com.cherrymooncake.wardrobe_android.core.mvi.ISingleFlowEvent
import com.cherrymooncake.wardrobe_android.core.mvi.IUiIntent
import com.cherrymooncake.wardrobe_android.core.mvi.IUiState

data class ProfileState(
    val isLoading: Boolean = false,
    val showDeleteDialog: Boolean = false
) : IUiState

sealed class ProfileIntent : IUiIntent {
    data class ChangePassword(val oldPass: String, val newPass: String) : ProfileIntent()
    object Logout : ProfileIntent()
    data class SetDeleteDialogVisible(val isVisible: Boolean) : ProfileIntent()
    object ConfirmDeleteAccount : ProfileIntent()
}

sealed class ProfileEvent : ISingleFlowEvent {
    data class ShowSnackbar(val message: String) : ProfileEvent()
    object NavigateToLogin : ProfileEvent()
}