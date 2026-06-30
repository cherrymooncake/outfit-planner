package com.cherrymooncake.wardrobe_android.feature.auth.presentation.mvi

import com.cherrymooncake.wardrobe_android.core.mvi.IUiState

data class AuthState(
    val isLoading: Boolean = false
) : IUiState