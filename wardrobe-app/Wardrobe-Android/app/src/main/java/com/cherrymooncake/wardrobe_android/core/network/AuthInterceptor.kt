package com.cherrymooncake.wardrobe_android.core.network

import com.cherrymooncake.wardrobe_android.core.network.source.ITokenLocalSource
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val tokenSource: ITokenLocalSource
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = tokenSource.getAccessToken()
        val requestBuilder = chain.request().newBuilder()

        if (!token.isNullOrEmpty()) {
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }

        return chain.proceed(requestBuilder.build())
    }
}