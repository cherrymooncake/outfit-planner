package com.cherrymooncake.wardrobe_android.feature.profile.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.cherrymooncake.wardrobe_android.core.mvi.BaseViewModel
import com.cherrymooncake.wardrobe_android.feature.auth.domain.usecase.ChangePasswordUseCase
import com.cherrymooncake.wardrobe_android.feature.auth.domain.usecase.DeleteAccountUseCase
import com.cherrymooncake.wardrobe_android.feature.auth.domain.usecase.LogoutUseCase
import com.cherrymooncake.wardrobe_android.feature.profile.presentation.mvi.ProfileEvent
import com.cherrymooncake.wardrobe_android.feature.profile.presentation.mvi.ProfileIntent
import com.cherrymooncake.wardrobe_android.feature.profile.presentation.mvi.ProfileState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val changePasswordUseCase: ChangePasswordUseCase,
    private val deleteAccountUseCase: DeleteAccountUseCase,
    private val logoutUseCase: LogoutUseCase
) : BaseViewModel<ProfileState, ProfileIntent, ProfileEvent>(ProfileState()) {

    override fun processIntent(intent: ProfileIntent) {
        when (intent) {
            is ProfileIntent.ChangePassword -> changePassword(intent.oldPass, intent.newPass)
            is ProfileIntent.Logout -> logout()
            is ProfileIntent.SetDeleteDialogVisible -> updateState { copy(showDeleteDialog = intent.isVisible) }
            is ProfileIntent.ConfirmDeleteAccount -> deleteAccount()
        }
    }

    private fun changePassword(oldPass: String, newPass: String) {
        if (oldPass.isBlank() || newPass.length < 6) {
            sendEvent(ProfileEvent.ShowSnackbar("Новый пароль должен быть от 6 символов"))
            return
        }

        viewModelScope.launch {
            updateState { copy(isLoading = true) }
            try {
                changePasswordUseCase(oldPass, newPass)
                sendEvent(ProfileEvent.ShowSnackbar("Пароль изменен! Пожалуйста, войдите снова."))
                logoutUseCase()
                sendEvent(ProfileEvent.NavigateToLogin)
            } catch (e: Exception) {
                sendEvent(ProfileEvent.ShowSnackbar("Ошибка: Неверный старый пароль или сбой сети"))
            } finally {
                updateState { copy(isLoading = false) }
            }
        }
    }

    private fun logout() {
        viewModelScope.launch {
            logoutUseCase()
            sendEvent(ProfileEvent.NavigateToLogin)
        }
    }

    private fun deleteAccount() {
        viewModelScope.launch {
            updateState { copy(isLoading = true, showDeleteDialog = false) }
            try {
                deleteAccountUseCase()
                logoutUseCase()
                sendEvent(ProfileEvent.ShowSnackbar("Аккаунт успешно удален"))
                sendEvent(ProfileEvent.NavigateToLogin)
            } catch (e: Exception) {
                updateState { copy(isLoading = false) }
                sendEvent(ProfileEvent.ShowSnackbar("Ошибка удаления: ${e.localizedMessage}"))
            }
        }
    }
}