package com.cherrymooncake.wardrobe_android.feature.auth.data.model

import com.google.gson.annotations.SerializedName

data class LoginApiModel(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)

data class RegisterApiModel(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)

data class TokenApiModel(
    @SerializedName("accessToken") val accessToken: String,
    @SerializedName("refreshToken") val refreshToken: String
)

data class ChangePasswordApiModel(
    @SerializedName("oldPassword") val oldPassword: String,
    @SerializedName("newPassword") val newPassword: String
)