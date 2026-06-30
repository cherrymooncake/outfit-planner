package com.cherrymooncake.wardrobe_android.feature.auth.domain.usecase

import com.cherrymooncake.wardrobe_android.feature.auth.domain.repository.IAuthRemoteRepository

class RegisterUseCase(
    private val remoteRepository: IAuthRemoteRepository
) {
    suspend operator fun invoke(email: String, password: String) {
        remoteRepository.register(email, password)
    }
}