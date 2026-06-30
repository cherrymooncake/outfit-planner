package com.cherrymooncake.wardrobe_android.feature.auth.presentation.ui

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cherrymooncake.wardrobe_android.feature.auth.presentation.mvi.AuthEvent
import com.cherrymooncake.wardrobe_android.feature.auth.presentation.mvi.AuthIntent
import com.cherrymooncake.wardrobe_android.feature.auth.presentation.viewmodel.AuthViewModel

@Composable
fun RegisterScreen(
    onNavigateBack: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                is AuthEvent.NavigateToLogin -> onNavigateBack()
                is AuthEvent.ShowError -> Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                is AuthEvent.ShowSuccess -> Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                else -> Unit
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Регистрация", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Пароль (мин. 6 символов)") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { viewModel.processIntent(AuthIntent.Register(email, password)) },
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.isLoading
        ) {
            if (state.isLoading) CircularProgressIndicator(modifier = Modifier.size(24.dp))
            else Text("Зарегистрироваться")
        }

        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = onNavigateBack) {
            Text("Уже есть аккаунт? Войти")
        }
    }
}