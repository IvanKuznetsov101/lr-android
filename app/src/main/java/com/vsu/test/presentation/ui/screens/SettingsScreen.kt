package com.vsu.test.presentation.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.vsu.test.Screen
import com.vsu.test.data.storage.TokenManager
import com.vsu.test.presentation.ui.components.BackButton
import com.vsu.test.presentation.ui.components.DefaultButton
import com.vsu.test.presentation.ui.components.LocationTrackingSwitch
import com.vsu.test.presentation.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(onNavigateToAbout: () -> Unit,
                   onNavigateToMore: () -> Unit,
                   navController: NavController,
                   tokenManager: TokenManager
) {
    val viewModel: SettingsViewModel = hiltViewModel()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)
    ) {
        // Заголовок сверху
        Text(
            text = "Settings",
            fontSize = 24.sp,
            modifier = Modifier
                .align(Alignment.TopCenter) // Размещаем сверху
                .padding(top = 32.dp) // Добавляем отступ сверху
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center, // Центрируем содержимое (без заголовка)
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            DefaultButton(
                onClick = {
                    val currentUserId = tokenManager.getId().toString()
                    navController.navigate(Screen.Profile.route(currentUserId))
                },
                text = "Account",
                icon = Icons.Default.Person
            )
            Spacer(modifier = Modifier.height(16.dp))
            DefaultButton(onClick = { onNavigateToAbout() }, text = "About", icon = Icons.Default.Info)
            Spacer(modifier = Modifier.height(16.dp))
            DefaultButton(onClick = { viewModel.logout() }, text = "Logout", icon = Icons.Default.Logout)
            Spacer(modifier = Modifier.height(16.dp))
            LocationTrackingSwitch(
                onServiceStateChanged = { enabled ->
                    Log.d("SettingsScreen", "Tracking state changed: $enabled")
                }
            )
        }

        // Кнопка "Назад" внизу справа
        BackButton(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = 32.dp, y = (-40).dp),
            button = { onNavigateToMore() }
        )
    }
}

