package com.cherrymooncake.wardrobe_android.feature.auth.presentation.viewmodel

import app.cash.turbine.test
import com.cherrymooncake.wardrobe_android.feature.auth.domain.usecase.LoginUseCase
import com.cherrymooncake.wardrobe_android.feature.auth.domain.usecase.RegisterUseCase
import com.cherrymooncake.wardrobe_android.feature.auth.presentation.mvi.AuthEvent
import com.cherrymooncake.wardrobe_android.feature.auth.presentation.mvi.AuthIntent
import com.cherrymooncake.wardrobe_android.utils.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response

class AuthViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var loginUseCase: LoginUseCase
    private lateinit var registerUseCase: RegisterUseCase
    private lateinit var viewModel: AuthViewModel

    @Before
    fun setup() {
        loginUseCase = mockk()
        registerUseCase = mockk()
        viewModel = AuthViewModel(loginUseCase, registerUseCase)
    }

    @Test
    fun login_emptyFields_emitsError() = runTest {
        viewModel.event.test {
            viewModel.processIntent(AuthIntent.Login("", ""))

            val event = awaitItem()
            assertTrue(event is AuthEvent.ShowError)
            assertEquals("Заполните все поля", (event as AuthEvent.ShowError).message)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun login_success_emitsNavigateAndUpdatesState() = runTest {
        coEvery { loginUseCase("test@mail.com", "password") } returns Unit

        viewModel.event.test {
            viewModel.processIntent(AuthIntent.Login("test@mail.com", "password"))

            val event = awaitItem()
            assertTrue(event is AuthEvent.NavigateToWardrobe)
            assertFalse(viewModel.state.value.isLoading)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun login_httpException_emitsError() = runTest {
        val exception = HttpException(Response.error<Any>(401, okhttp3.ResponseBody.create(null, "")))
        coEvery { loginUseCase("test@mail.com", "wrong") } throws exception

        viewModel.event.test {
            viewModel.processIntent(AuthIntent.Login("test@mail.com", "wrong"))

            val event = awaitItem()
            assertTrue(event is AuthEvent.ShowError)
            assertEquals("Неверный email или пароль", (event as AuthEvent.ShowError).message)
            assertFalse(viewModel.state.value.isLoading)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun register_shortPassword_emitsError() = runTest {
        viewModel.event.test {
            viewModel.processIntent(AuthIntent.Register("test@mail.com", "123"))

            val event = awaitItem()
            assertTrue(event is AuthEvent.ShowError)
            assertEquals("Неверный формат или короткий пароль", (event as AuthEvent.ShowError).message)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun register_success_emitsSuccessAndNavigate() = runTest {
        coEvery { registerUseCase("test@mail.com", "password") } returns Unit

        viewModel.event.test {
            viewModel.processIntent(AuthIntent.Register("test@mail.com", "password"))

            val event1 = awaitItem()
            assertTrue(event1 is AuthEvent.ShowSuccess)

            val event2 = awaitItem()
            assertTrue(event2 is AuthEvent.NavigateToLogin)

            assertFalse(viewModel.state.value.isLoading)
            cancelAndIgnoreRemainingEvents()
        }
    }
}