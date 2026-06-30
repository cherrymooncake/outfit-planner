package com.cherrymooncake.wardrobe_android.core.network.source

import android.content.SharedPreferences
import android.util.Base64
import org.json.JSONObject

class TokenLocalSourceImpl(
    private val sharedPreferences: SharedPreferences
) : ITokenLocalSource {

    companion object {
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val KEY_CITY = "user_city"
    }

    override fun saveTokens(accessToken: String, refreshToken: String) {
        sharedPreferences.edit()
            .putString(KEY_ACCESS_TOKEN, accessToken)
            .putString(KEY_REFRESH_TOKEN, refreshToken)
            .apply()
    }

    override fun getAccessToken(): String? = sharedPreferences.getString(KEY_ACCESS_TOKEN, null)

    override fun getRefreshToken(): String? = sharedPreferences.getString(KEY_REFRESH_TOKEN, null)

    override fun clearTokens() {
        sharedPreferences.edit()
            .remove(KEY_ACCESS_TOKEN)
            .remove(KEY_REFRESH_TOKEN)
            .apply()
    }

    override fun getUserRole(): String {
        val token = getAccessToken() ?: return "User"
        try {
            val parts = token.split(".")
            if (parts.size == 3) {
                val payload = String(Base64.decode(parts[1], Base64.URL_SAFE))
                val json = JSONObject(payload)


                if (json.has("role")) return json.getString("role")
                val msRole = "http://schemas.microsoft.com/ws/2008/06/identity/claims/role"
                if (json.has(msRole)) return json.getString(msRole)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return "User"
    }


    override fun saveCity(city: String) {
        sharedPreferences.edit()
            .putString(KEY_CITY, city)
            .apply()
    }

    override fun getCity(): String? {
        return sharedPreferences.getString(KEY_CITY, null)
    }
}