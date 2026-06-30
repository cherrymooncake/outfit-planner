package com.cherrymooncake.wardrobe_android.core.network.source

interface ITokenLocalSource {
    fun saveTokens(accessToken: String, refreshToken: String)
    fun getAccessToken(): String?
    fun getRefreshToken(): String?
    fun clearTokens()
    fun getUserRole(): String

    fun saveCity(city: String)
    fun getCity(): String?
}