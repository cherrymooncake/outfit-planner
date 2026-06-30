package com.cherrymooncake.wardrobe_android.feature.auth.data.api

import com.cherrymooncake.wardrobe_android.feature.auth.data.model.ChangePasswordApiModel
import com.cherrymooncake.wardrobe_android.feature.auth.data.model.LoginApiModel
import com.cherrymooncake.wardrobe_android.feature.auth.data.model.RegisterApiModel
import com.cherrymooncake.wardrobe_android.feature.auth.data.model.TokenApiModel
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST

interface IAuthApi {
    @POST("auth/login")
    suspend fun login(@Body body: LoginApiModel): TokenApiModel

    @POST("auth/register")
    suspend fun register(@Body body: RegisterApiModel)

    @POST("auth/change-password")
    suspend fun changePassword(@Body body: ChangePasswordApiModel)

    @DELETE("auth/account")
    suspend fun deleteAccount()
}