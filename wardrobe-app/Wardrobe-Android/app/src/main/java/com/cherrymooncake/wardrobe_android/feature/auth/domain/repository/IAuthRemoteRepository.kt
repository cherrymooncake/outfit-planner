package com.cherrymooncake.wardrobe_android.feature.auth.domain.repository
import com.cherrymooncake.wardrobe_android.feature.auth.domain.model.TokenDomainModel

interface IAuthRemoteRepository {
    suspend fun login(email: String, password: String): TokenDomainModel
    suspend fun register(email: String, password: String)
    suspend fun changePassword(oldPass: String, newPass: String)
    suspend fun deleteAccount()
}