package com.vsu.test.presentation.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter


@Composable
fun PhotoPicker(onImagesSelected: (List<Uri>) -> Unit) {
    var selectedImages by remember { mutableStateOf<List<Uri>>(emptyList()) }
    val maxSelectable = (5 - selectedImages.size).coerceAtLeast(2) // Минимум 2

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = maxSelectable)
    ) { uris: List<Uri> ->
        val uniqueUris = uris.filter { it !in selectedImages }
        selectedImages = (selectedImages + uniqueUris).take(5) // Не больше 5 изображений
        onImagesSelected(selectedImages) // Передаём в CRUDEvent
    }

    Row(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        selectedImages.forEach { uri ->
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                    .clickable {
                        selectedImages = selectedImages.filter { it != uri }
                        onImagesSelected(selectedImages) // Обновляем CRUDEvent
                    }
            ) {
                Image(
                    painter = rememberAsyncImagePainter(uri),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
        if (selectedImages.size < 5) {
            IconButton(
                onClick = {
                    photoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.LightGray, RoundedCornerShape(8.dp))
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Добавить изображение"
                )
            }
        }
    }
}
