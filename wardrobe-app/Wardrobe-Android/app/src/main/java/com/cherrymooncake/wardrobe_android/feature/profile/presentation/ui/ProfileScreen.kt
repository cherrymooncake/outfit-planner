package com.cherrymooncake.wardrobe_android.feature.profile.presentation.ui

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cherrymooncake.wardrobe_android.feature.profile.presentation.mvi.ProfileEvent
import com.cherrymooncake.wardrobe_android.feature.profile.presentation.mvi.ProfileIntent
import com.cherrymooncake.wardrobe_android.feature.profile.presentation.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateToLogin: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                is ProfileEvent.ShowSnackbar -> Toast.makeText(context, event.message, Toast.LENGTH_LONG).show()
                is ProfileEvent.NavigateToLogin -> onNavigateToLogin()
            }
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                title = { Text("Настройки профиля") },
                actions = {
                    IconButton(onClick = { viewModel.processIntent(ProfileIntent.Logout) }) {
                        Icon(Icons.Default.ExitToApp, "Выйти", tint = MaterialTheme.colorScheme.error)
                    }
                },
                windowInsets = WindowInsets(0, 0, 0, 0)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Смена пароля", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = oldPassword,
                        onValueChange = { oldPassword = it },
                        label = { Text("Текущий пароль") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = newPassword,
                        onValueChange = { newPassword = it },
                        label = { Text("Новый пароль") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            viewModel.processIntent(ProfileIntent.ChangePassword(oldPassword, newPassword))
                            oldPassword = ""
                            newPassword = ""
                        },
                        enabled = !state.isLoading,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Обновить пароль")
                    }
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF0F0)),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Удаление аккаунта", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Это действие необратимо. Все ваши вещи, образы и данные будут удалены без возможности восстановления.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { viewModel.processIntent(ProfileIntent.SetDeleteDialogVisible(true)) },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !state.isLoading
                    ) {
                        Text("Удалить аккаунт навсегда")
                    }
                }
            }

            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
        }
    }

    if (state.showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.processIntent(ProfileIntent.SetDeleteDialogVisible(false)) },
            title = { Text("Внимание!") },
            text = { Text("Вы уверены, что хотите удалить аккаунт? Все данные будут стерты навсегда.") },
            confirmButton = {
                TextButton(onClick = { viewModel.processIntent(ProfileIntent.ConfirmDeleteAccount) }) {
                    Text("Да, удалить", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.processIntent(ProfileIntent.SetDeleteDialogVisible(false)) }) {
                    Text("Отмена")
                }
            }
        )
    }
}