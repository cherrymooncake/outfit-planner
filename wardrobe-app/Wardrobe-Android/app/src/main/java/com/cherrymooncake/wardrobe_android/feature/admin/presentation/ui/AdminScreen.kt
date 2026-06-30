package com.cherrymooncake.wardrobe_android.feature.admin.presentation.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cherrymooncake.wardrobe_android.feature.admin.presentation.mvi.*
import com.cherrymooncake.wardrobe_android.feature.admin.presentation.viewmodel.AdminViewModel
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    viewModel: AdminViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.event.collect {
            if (it is AdminEvent.ShowSnackbar) Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
        }
    }

    val timeStamp = remember { SimpleDateFormat("yyyyMMdd_HHmm", Locale.getDefault()).format(Date()) }

    var currentBackupType by remember { mutableStateOf("") }

    val createDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("*/*")
    ) { uri ->
        if (uri != null && currentBackupType.isNotEmpty()) {
            viewModel.processIntent(AdminIntent.SaveBackupToFile(uri, currentBackupType, context))
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                title = { Text("Панель администратора") },
                actions = {
                    IconButton(onClick = { viewModel.processIntent(AdminIntent.LoadData) }) {
                        Icon(Icons.Default.Refresh, "Обновить")
                    }
                },
                windowInsets = WindowInsets(0, 0, 0, 0)
            )
        }
    ) { padding ->
        if (state.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text("Статус сервисов", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        StatusCard("БД", state.health?.dbStatus, Modifier.weight(1f))
                        StatusCard("Вырезка", state.health?.bgRemovalStatus, Modifier.weight(1f))
                        StatusCard("Стилист", state.health?.aiStylistStatus, Modifier.weight(1f))
                    }
                }

                item {
                    Text("Статистика", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        StatCard("Юзеров", state.stats?.users.toString(), Modifier.weight(1f))
                        StatCard("Вещей", state.stats?.items.toString(), Modifier.weight(1f))
                        StatCard("Образов", state.stats?.outfits.toString(), Modifier.weight(1f))
                    }
                    Spacer(Modifier.height(8.dp))
                    StatCard("Папка Images", "${state.stats?.sizeMb} MB", Modifier.fillMaxWidth())
                }

                item { Divider(Modifier.padding(vertical = 8.dp)) }

                item { Text("Пользователи", style = MaterialTheme.typography.titleLarge) }

                item { Divider(Modifier.padding(vertical = 8.dp)) }
                item { Text("Резервное копирование", style = MaterialTheme.typography.titleLarge) }

                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(16.dp)) {
                            Text("Дамп базы данных (.sql)", style = MaterialTheme.typography.titleMedium)
                            Spacer(Modifier.height(8.dp))
                            Button(
                                onClick = {
                                    currentBackupType = "db"
                                    createDocumentLauncher.launch("db_backup_$timeStamp.sql")
                                },
                                enabled = !state.isDownloading,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Скачать БД")
                            }

                            Spacer(Modifier.height(16.dp))
                            Text("Файлы (.zip)", style = MaterialTheme.typography.titleMedium)
                            Spacer(Modifier.height(8.dp))
                            Button(
                                onClick = {
                                    currentBackupType = "files"
                                    createDocumentLauncher.launch("images_backup_$timeStamp.zip")
                                },
                                enabled = !state.isDownloading,
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE6A23C)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Скачать файлы")
                            }

                            Spacer(Modifier.height(16.dp))
                            Text("Полный бэкап (.zip)", style = MaterialTheme.typography.titleMedium)
                            Spacer(Modifier.height(8.dp))
                            Button(
                                onClick = {
                                    currentBackupType = "full"
                                    createDocumentLauncher.launch("full_backup_$timeStamp.zip")
                                },
                                enabled = !state.isDownloading,
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                if (state.isDownloading && currentBackupType == "full") {
                                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                                } else {
                                    Text("Полный бэкап системы")
                                }
                            }
                        }
                    }
                }

                items(state.users) { user ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(Modifier.padding(12.dp)) {
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(user.email, style = MaterialTheme.typography.titleMedium)
                                val color = if (user.role == "Admin") Color(0xFFE6A23C) else Color.Gray
                                Text(user.role, color = color, style = MaterialTheme.typography.labelLarge)
                            }
                            Spacer(Modifier.height(4.dp))
                            Text("Регистрация: ${user.registeredAt.take(10)}", style = MaterialTheme.typography.bodySmall)
                            Text("Активность: ${user.lastActiveAt.take(10)}", style = MaterialTheme.typography.bodySmall)
                            Text("Вещей: ${user.itemsCount} | Образов: ${user.outfitsCount}", style = MaterialTheme.typography.bodySmall)

                            Spacer(Modifier.height(8.dp))
                            Button(
                                onClick = { viewModel.processIntent(AdminIntent.ChangeRole(user.id, user.role)) },
                                modifier = Modifier.fillMaxWidth().height(36.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (user.role == "Admin") MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Text(if (user.role == "Admin") "Забрать администраторские права" else "Выдать администраторские права")
                            }
                        }
                    }
                }

                item { Spacer(Modifier.height(80.dp)) }
            }
        }
    }
}

@Composable
fun StatusCard(title: String, status: String?, modifier: Modifier = Modifier) {
    val isOk = status == "Ok"
    Card(modifier = modifier, colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))) {
        Column(Modifier.padding(12.dp)) {
            Text(title, style = MaterialTheme.typography.bodySmall)
            Text(status ?: "Wait", color = if (isOk) Color(0xFF67C23A) else Color.Red)
        }
    }
}

@Composable
fun StatCard(title: String, value: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
        Column(Modifier.padding(12.dp)) {
            Text(title, style = MaterialTheme.typography.bodySmall)
            Text(value, style = MaterialTheme.typography.titleLarge)
        }
    }
}