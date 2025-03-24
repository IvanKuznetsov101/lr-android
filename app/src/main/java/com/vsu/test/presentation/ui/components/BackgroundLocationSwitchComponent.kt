package com.vsu.test.presentation.ui.components

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.vsu.test.service.LocationService

@Composable
fun LocationTrackingSwitch(
    modifier: Modifier = Modifier,
    onServiceStateChanged: (Boolean) -> Unit = {}
) {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
    var isTrackingEnabled by remember { mutableStateOf(prefs.getBoolean("tracking_enabled", false)) }
    var showPermissionDialog by remember { mutableStateOf(false) }
    var pendingNotificationPermission by remember { mutableStateOf(false) } // Ожидание запроса уведомлений

    // Лаунчер для ACCESS_BACKGROUND_LOCATION
    val backgroundLocationLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Log.d("LocationTrackingSwitch", "ACCESS_BACKGROUND_LOCATION granted")
            pendingNotificationPermission = true // Переходим к запросу POST_NOTIFICATIONS
        } else {
            Log.d("LocationTrackingSwitch", "ACCESS_BACKGROUND_LOCATION denied")
            isTrackingEnabled = false
            prefs.edit().putBoolean("tracking_enabled", false).apply()
            onServiceStateChanged(false)
        }
    }

    // Лаунчер для POST_NOTIFICATIONS
    val notificationLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        isTrackingEnabled = isGranted // Включаем Switch только если оба разрешения есть
        prefs.edit().putBoolean("tracking_enabled", isGranted).apply()
        if (isGranted) {
            try {
                val intent = Intent(context, LocationService::class.java)
                context.startForegroundService(intent)
                Log.d("LocationTrackingSwitch", "Permissions granted, service started")
                onServiceStateChanged(true)
            } catch (e: SecurityException) {
                Log.e("LocationTrackingSwitch", "Failed to start service: ${e.message}")
                isTrackingEnabled = false
                prefs.edit().putBoolean("tracking_enabled", false).apply()
                onServiceStateChanged(false)
            }
        } else {
            Log.d("LocationTrackingSwitch", "POST_NOTIFICATIONS denied")
            onServiceStateChanged(false)
        }
        pendingNotificationPermission = false // Сбрасываем флаг
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Отслеживание местоположения",
            modifier = Modifier.weight(1f)
        )
        Switch(
            checked = isTrackingEnabled,
            onCheckedChange = { enabled ->
                if (enabled) {
                    val fineLocationGranted = ContextCompat.checkSelfPermission(
                        context, Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                    val backgroundLocationGranted = ContextCompat.checkSelfPermission(
                        context, Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                    val postNotificationsGranted = ContextCompat.checkSelfPermission(
                        context, Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED

                    if (fineLocationGranted && backgroundLocationGranted && postNotificationsGranted) {
                        // Все разрешения есть, включаем сервис
                        isTrackingEnabled = true
                        prefs.edit().putBoolean("tracking_enabled", true).apply()
                        try {
                            val intent = Intent(context, LocationService::class.java)
                            context.startForegroundService(intent)
                            Log.d("LocationTrackingSwitch", "Switch enabled, service started")
                            onServiceStateChanged(true)
                        } catch (e: SecurityException) {
                            Log.e("LocationTrackingSwitch", "Failed to start service: ${e.message}")
                            isTrackingEnabled = false
                            prefs.edit().putBoolean("tracking_enabled", false).apply()
                            onServiceStateChanged(false)
                        }
                    } else if (fineLocationGranted) {
                        // ACCESS_FINE_LOCATION есть, но нет остальных
                        showPermissionDialog = true
                    } else {
                        // ACCESS_FINE_LOCATION нет, что-то пошло не так
                        Log.e("LocationTrackingSwitch", "ACCESS_FINE_LOCATION not granted unexpectedly")
                        isTrackingEnabled = false
                        prefs.edit().putBoolean("tracking_enabled", false).apply()
                        onServiceStateChanged(false)
                    }
                } else {
                    // Выключаем сервис
                    isTrackingEnabled = false
                    prefs.edit().putBoolean("tracking_enabled", false).apply()
                    try {
                        val intent = Intent(context, LocationService::class.java)
                        context.stopService(intent)
                        Log.d("LocationTrackingSwitch", "Service stopped")
                        onServiceStateChanged(false)
                    } catch (e: SecurityException) {
                        Log.e("LocationTrackingSwitch", "Failed to stop service: ${e.message}")
                    }
                }
            }
        )
    }

    // Диалог для запроса разрешений
    if (showPermissionDialog) {
        ShowAlertDialog(
            dialogTitle = "Требуется разрешение",
            dialogText = "Для работы отслеживания нужно предоставить разрешения на фоновое местоположение и уведомления. Хотите сделать это сейчас?",
            onConfirmation = {
                showPermissionDialog = false
                backgroundLocationLauncher.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            },
            onDismissRequest = {
                showPermissionDialog = false
                isTrackingEnabled = false
                prefs.edit().putBoolean("tracking_enabled", false).apply()
                onServiceStateChanged(false)
            }
        )
    }

    // Запрос POST_NOTIFICATIONS после ACCESS_BACKGROUND_LOCATION
    if (pendingNotificationPermission) {
        LaunchedEffect(Unit) {
            notificationLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}

@Composable
fun ShowAlertDialog(
    dialogTitle: String,
    dialogText: String,
    onConfirmation: () -> Unit,
    onDismissRequest: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(dialogTitle) },
        text = { Text(dialogText) },
        confirmButton = {
            Button(onClick = onConfirmation) {
                Text("Согласен")
            }
        },
        dismissButton = {
            Button(onClick = onDismissRequest) {
                Text("Отмена")
            }
        }
    )
}