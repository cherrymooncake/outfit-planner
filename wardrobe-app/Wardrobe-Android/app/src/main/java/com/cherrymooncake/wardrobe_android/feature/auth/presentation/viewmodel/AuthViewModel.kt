package com.cherrymooncake.wardrobe_android.feature.auth.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.cherrymooncake.wardrobe_android.core.mvi.BaseViewModel
import com.cherrymooncake.wardrobe_android.feature.auth.domain.usecase.LoginUseCase
import com.cherrymooncake.wardrobe_android.feature.auth.domain.usecase.RegisterUseCase
import com.cherrymooncake.wardrobe_android.feature.auth.presentation.mvi.AuthEvent
import com.cherrymooncake.wardrobe_android.feature.auth.presentation.mvi.AuthIntent
import com.cherrymooncake.wardrobe_android.feature.auth.presentation.mvi.AuthState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase
) : BaseViewModel<AuthState, AuthIntent, AuthEvent>(AuthState()) {

    override fun processIntent(intent: AuthIntent) {
        when (intent) {
            is AuthIntent.Login -> login(intent.email, intent.pass)
            is AuthIntent.Register -> register(intent.email, intent.pass)
        }
    }

    private fun login(email: String, pass: String) {
        if (email.isBlank() || pass.isBlank()) {
            sendEvent(AuthEvent.ShowError("Заполните все поля"))
            return
        }

        viewModelScope.launch {
            updateState { copy(isLoading = true) }
            try {
                loginUseCase(email, pass)
                sendEvent(AuthEvent.NavigateToWardrobe)
            } catch (e: HttpException) {
                sendEvent(AuthEvent.ShowError("Неверный email или пароль"))
            } catch (e: Exception) {
                sendEvent(AuthEvent.ShowError("Ошибка соединения: ${e.message}"))
            } finally {
                updateState { copy(isLoading = false) }
            }
        }
    }

    private fun register(email: String, pass: String) {
        if (email.isBlank() || pass.length < 6) {
            sendEvent(AuthEvent.ShowError("Неверный формат или короткий пароль"))
            return
        }

        viewModelScope.launch {
            updateState { copy(isLoading = true) }
            try {
                registerUseCase(email, pass)
                sendEvent(AuthEvent.ShowSuccess("Регистрация успешна! Войдите в систему."))
                sendEvent(AuthEvent.NavigateToLogin)
            } catch (e: HttpException) {
                sendEvent(AuthEvent.ShowError("Ошибка регистрации. Возможно, email уже занят."))
            } catch (e: Exception) {
                sendEvent(AuthEvent.ShowError("Ошибка соединения: ${e.message}"))
            } finally {
                updateState { copy(isLoading = false) }
            }
        }
    }
}