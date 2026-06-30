package com.cherrymooncake.wardrobe_android.feature.auth.data.source
import com.cherrymooncake.wardrobe_android.feature.auth.data.api.IAuthApi
import com.cherrymooncake.wardrobe_android.feature.auth.data.model.*

class AuthRemoteSourceImpl(private val api: IAuthApi) : IAuthRemoteSource {
    override suspend fun login(model: LoginApiModel) = api.login(model)
    override suspend fun register(model: RegisterApiModel) = api.register(model)
    override suspend fun changePassword(model: ChangePasswordApiModel) = api.changePassword(model)
    override suspend fun deleteAccount() = api.deleteAccount()
}