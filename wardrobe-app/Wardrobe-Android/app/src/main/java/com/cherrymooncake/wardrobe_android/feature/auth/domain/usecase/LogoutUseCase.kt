package com.cherrymooncake.wardrobe_android.feature.auth.domain.usecase
import com.cherrymooncake.wardrobe_android.feature.auth.domain.repository.IAuthLocalRepository

class LogoutUseCase(private val localRepository: IAuthLocalRepository) {
    suspend operator fun invoke() {
        localRepository.clearAllUserData()
    }
}