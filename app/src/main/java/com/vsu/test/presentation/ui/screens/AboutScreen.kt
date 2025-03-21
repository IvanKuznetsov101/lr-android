package com.vsu.test.presentation.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.vsu.test.BuildConfig
import com.vsu.test.R
import com.vsu.test.presentation.ui.components.BackButton
import com.vsu.test.presentation.ui.components.DefaultButton
import org.w3c.dom.Text

@Composable
fun AboutScreen(onNavigateToSettings:() -> Unit) {
    val context = LocalContext.current
    val url = BuildConfig.YANDEX_MAPS_TERMS_OF_USE
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
                text = "About",
                fontSize = 24.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            DefaultButton(
                onClick = { openUrl(context, url) },
                text = "Условия использования Яндекс Карт"
            )
        }
        BackButton(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = 32.dp, y = (-40).dp),
            button = {onNavigateToSettings()}
        )
    }
}
fun openUrl(context: Context, url: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    context.startActivity(intent)
}



