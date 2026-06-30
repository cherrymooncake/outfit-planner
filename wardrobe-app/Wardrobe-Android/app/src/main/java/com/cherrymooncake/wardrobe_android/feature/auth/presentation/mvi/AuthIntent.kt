package com.cherrymooncake.wardrobe_android.feature.auth.presentation.mvi

import com.cherrymooncake.wardrobe_android.core.mvi.IUiIntent

sealed class AuthIntent : IUiIntent {
    data class Login(val email: String, val pass: String) : AuthIntent()
    data class Register(val email: String, val pass: String) : AuthIntent()
}