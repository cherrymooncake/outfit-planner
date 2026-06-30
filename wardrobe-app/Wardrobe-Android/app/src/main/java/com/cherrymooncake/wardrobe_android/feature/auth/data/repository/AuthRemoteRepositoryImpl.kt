package com.cherrymooncake.wardrobe_android.feature.auth.data.repository
import com.cherrymooncake.wardrobe_android.feature.auth.data.mapper.toDomain
import com.cherrymooncake.wardrobe_android.feature.auth.data.model.*
import com.cherrymooncake.wardrobe_android.feature.auth.data.source.IAuthRemoteSource
import com.cherrymooncake.wardrobe_android.feature.auth.domain.model.TokenDomainModel
import com.cherrymooncake.wardrobe_android.feature.auth.domain.repository.IAuthRemoteRepository

class AuthRemoteRepositoryImpl(private val remoteSource: IAuthRemoteSource) : IAuthRemoteRepository {
    override suspend fun login(email: String, password: String): TokenDomainModel {
        return remoteSource.login(LoginApiModel(email, password)).toDomain()
    }
    override suspend fun register(email: String, password: String) {
        remoteSource.register(RegisterApiModel(email, password))
    }
    override suspend fun changePassword(oldPass: String, newPass: String) {
        remoteSource.changePassword(ChangePasswordApiModel(oldPass, newPass))
    }
    override suspend fun deleteAccount() {
        remoteSource.deleteAccount()
    }
}