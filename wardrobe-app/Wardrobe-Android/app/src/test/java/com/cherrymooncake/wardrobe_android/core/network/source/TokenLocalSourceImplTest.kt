package com.cherrymooncake.wardrobe_android.core.network.source

import android.content.SharedPreferences
import android.util.Base64
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class TokenLocalSourceImplTest {

    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var tokenSource: TokenLocalSourceImpl

    @Before
    fun setup() {
        sharedPrefs = mockk()
        editor = mockk(relaxed = true)
        every { sharedPrefs.edit() } returns editor

        mockkStatic(Base64::class)
        every { Base64.decode(any<String>(), any()) } answers {
            java.util.Base64.getUrlDecoder().decode(firstArg<String>())
        }

        tokenSource = TokenLocalSourceImpl(sharedPrefs)
    }

    @After
    fun teardown() {
        unmockkAll()
    }

    @Test
    fun saveTokens_callsEditorPutString() {
        tokenSource.saveTokens("access123", "refresh123")

        verify { editor.putString("access_token", "access123") }
        verify { editor.putString("refresh_token", "refresh123") }
        verify { editor.apply() }
    }

    @Test
    fun getAccessToken_returnsToken() {
        every { sharedPrefs.getString("access_token", null) } returns "my_token"

        val result = tokenSource.getAccessToken()

        assertEquals("my_token", result)
    }

    @Test
    fun getAccessToken_returnsNullIfEmpty() {
        every { sharedPrefs.getString("access_token", null) } returns null

        val result = tokenSource.getAccessToken()

        assertNull(result)
    }

    @Test
    fun getUserRole_validJwtWithRole_returnsRole() {
        val payload = "{\"role\":\"Admin\"}"
        val encodedPayload = java.util.Base64.getUrlEncoder().encodeToString(payload.toByteArray())
        val jwt = "header.$encodedPayload.signature"
        every { sharedPrefs.getString("access_token", null) } returns jwt

        val result = tokenSource.getUserRole()

        assertEquals("Admin", result)
    }

    @Test
    fun getUserRole_validJwtWithMsRole_returnsRole() {
        val payload = "{\"http://schemas.microsoft.com/ws/2008/06/identity/claims/role\":\"User\"}"
        val encodedPayload = java.util.Base64.getUrlEncoder().encodeToString(payload.toByteArray())
        val jwt = "header.$encodedPayload.signature"
        every { sharedPrefs.getString("access_token", null) } returns jwt

        val result = tokenSource.getUserRole()

        assertEquals("User", result)
    }

    @Test
    fun getUserRole_invalidJwt_returnsDefaultUser() {
        every { sharedPrefs.getString("access_token", null) } returns "invalid_token_format"

        val result = tokenSource.getUserRole()

        assertEquals("User", result)
    }
}