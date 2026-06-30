package com.cherrymooncake.wardrobe_android.feature.auth.domain.usecase
import com.cherrymooncake.wardrobe_android.feature.auth.domain.repository.IAuthRemoteRepository

class DeleteAccountUseCase(private val remoteRepository: IAuthRemoteRepository) {
    suspend operator fun invoke() {
        remoteRepository.deleteAccount()
    }
}