package com.cherrymooncake.wardrobe_android.feature.auth.domain.model

data class TokenDomainModel(
    val accessToken: String,
    val refreshToken: String
)