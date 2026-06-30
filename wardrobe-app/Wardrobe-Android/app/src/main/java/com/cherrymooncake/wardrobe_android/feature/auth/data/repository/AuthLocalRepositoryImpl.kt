package com.cherrymooncake.wardrobe_android.feature.auth.data.repository

import com.cherrymooncake.wardrobe_android.core.db.WardrobeDB
import com.cherrymooncake.wardrobe_android.core.network.source.ITokenLocalSource
import com.cherrymooncake.wardrobe_android.feature.auth.domain.repository.IAuthLocalRepository

class AuthLocalRepositoryImpl(
    private val tokenLocalSource: ITokenLocalSource,
    private val db: WardrobeDB
) : IAuthLocalRepository {

    override fun saveTokens(accessToken: String, refreshToken: String) {
        tokenLocalSource.saveTokens(accessToken, refreshToken)
    }

    override suspend fun clearAllUserData() {
        tokenLocalSource.clearTokens()
        db.clearAllTables()
    }
}