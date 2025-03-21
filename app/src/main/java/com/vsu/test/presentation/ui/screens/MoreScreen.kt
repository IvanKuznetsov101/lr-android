package com.vsu.test.presentation.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.vsu.test.R
import com.vsu.test.domain.usecase.MoreState
import com.vsu.test.presentation.ui.components.CombinedActions
import com.vsu.test.presentation.viewmodel.MoreViewModel

@Composable
fun MoreScreen(viewModel: MoreViewModel = hiltViewModel(),
               onNavigateToSettings: () -> Unit,
               onNavigateToMap:() -> Unit) {
    val context = LocalContext.current
    val state = viewModel.state.collectAsState().value

    // Лаунчер для запроса разрешений
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_BACKGROUND_LOCATION] == true) {
            // Разрешение на фоновое местоположение получено
            // Здесь можно добавить нужные действия
        } else {
            // Разрешение не получено
            // Можно показать сообщение или обработать отказ
        }
    }

    // Состояние для отображения диалога
    var showDialog by remember { mutableStateOf(false) }

    // Проверка разрешений при загрузке экрана
    LaunchedEffect(Unit) {
        val fineLocationGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarseLocationGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val backgroundLocationGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        // Если есть Fine и Coarse, но нет Background, показываем диалог
        if (fineLocationGranted && coarseLocationGranted && !backgroundLocationGranted) {
            showDialog = true
        }
    }
    LaunchedEffect(Unit) {
        viewModel.updateState(context)
    }
    // UI с диалогом
    Column {
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Требуется дополнительное разрешение") },
                text = { Text("Для работы приложения в фоновом режиме нужно разрешение на доступ к местоположению. Хотите предоставить его?") },
                confirmButton = {
                    Button(onClick = {
                        showDialog = false
                        requestPermissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION))
                    }) {
                        Text("Согласен")
                    }
                },
                dismissButton = {
                    Button(onClick = { showDialog = false }) {
                        Text("Отмена")
                    }
                }
            )
        }
    }
   Box(modifier = Modifier.fillMaxSize(). background(color = Color.White)){
       Column(
           modifier = Modifier
               .fillMaxSize()
               .padding(16.dp),
           verticalArrangement = Arrangement.Top,
           horizontalAlignment = Alignment.CenterHorizontally
       ) {
           Spacer(modifier = Modifier.height(16.dp))
           Text(
               text = "More",
               fontSize = 24.sp
           )
           Spacer(modifier = Modifier.height(16.dp))
           Box(
               modifier = Modifier
                   .height(700.dp)
                   .width(320.dp)
                   .background(colorResource(R.color.customGray), shape = RoundedCornerShape(64.dp))

           )
           {
               Column(modifier = Modifier.fillMaxSize()) {
                   when (state) {
                       is MoreState.UserEvent -> {
                           Text(text = "Ваше мероприятие: ${state.event.title}")
                       }
                       is MoreState.EventsInRadius -> {
                           Text(text = "Мероприятия рядом:")
                           state.events.forEach { event ->
                               Text(text = event.toString())
                           }
                       }
                       is MoreState.NoEvents -> {
                           Text(text = "Мероприятий рядом нет")
                       }
                   }
               }
           }
           Spacer(modifier = Modifier.height(16.dp))
       }
       CombinedActions(
           modifier = Modifier
               .align(Alignment.BottomEnd)
               .offset(x = 32.dp, y = (-40).dp),
           leftButton = {
               onNavigateToSettings()
           },
           rightButton = {onNavigateToMap()},
           Icons.Default.Settings,
           Icons.Default.ArrowBackIosNew
       )
   }
}

