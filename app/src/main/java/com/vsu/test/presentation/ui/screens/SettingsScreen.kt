package com.vsu.test.presentation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vsu.test.presentation.ui.components.BackButton
import com.vsu.test.presentation.ui.components.DefaultButton
import com.vsu.test.presentation.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(onNavigateToAbout: () -> Unit,
                   onNavigateToMore: () -> Unit) {
    val viewModel: SettingsViewModel = hiltViewModel()
    Box(modifier = Modifier
        .fillMaxSize()
        .background(color = Color.White)){
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Settings",
                fontSize = 24.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            DefaultButton(onClick = {},text = "Account")
            Spacer(modifier = Modifier.height(16.dp))
            DefaultButton(onClick = {onNavigateToAbout()},text = "About")
            Spacer(modifier = Modifier.height(16.dp))
            DefaultButton(onClick = {
                viewModel.logout()
            },text = "Logout")

        }
        BackButton(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = 32.dp, y = (-40).dp),
            button = {onNavigateToMore()}
        )
    }
}

