package com.cherrymooncake.wardrobe_android.feature.auth.domain.repository

interface IAuthLocalRepository {
    fun saveTokens(accessToken: String, refreshToken: String)
    suspend fun clearAllUserData()
}