package com.cherrymooncake.wardrobe_android.feature.auth.domain.usecase

import com.cherrymooncake.wardrobe_android.feature.auth.domain.repository.IAuthLocalRepository
import com.cherrymooncake.wardrobe_android.feature.auth.domain.repository.IAuthRemoteRepository

class LoginUseCase(
    private val remoteRepository: IAuthRemoteRepository,
    private val localRepository: IAuthLocalRepository
) {
    suspend operator fun invoke(email: String, password: String) {
        val tokens = remoteRepository.login(email, password)
        localRepository.saveTokens(tokens.accessToken, tokens.refreshToken)
    }
}