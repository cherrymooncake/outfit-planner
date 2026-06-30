package com.cherrymooncake.wardrobe_android.feature.auth.data.source
import com.cherrymooncake.wardrobe_android.feature.auth.data.model.*

interface IAuthRemoteSource {
    suspend fun login(model: LoginApiModel): TokenApiModel
    suspend fun register(model: RegisterApiModel)
    suspend fun changePassword(model: ChangePasswordApiModel)
    suspend fun deleteAccount()
}