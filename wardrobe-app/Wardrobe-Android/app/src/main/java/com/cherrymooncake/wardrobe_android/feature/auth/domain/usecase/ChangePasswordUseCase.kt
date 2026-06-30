package com.cherrymooncake.wardrobe_android.feature.auth.domain.usecase
import com.cherrymooncake.wardrobe_android.feature.auth.domain.repository.IAuthRemoteRepository

class ChangePasswordUseCase(private val remoteRepository: IAuthRemoteRepository) {
    suspend operator fun invoke(oldPass: String, newPass: String) {
        remoteRepository.changePassword(oldPass, newPass)
    }
}