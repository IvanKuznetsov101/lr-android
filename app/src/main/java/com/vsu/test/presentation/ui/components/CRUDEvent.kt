package com.vsu.test.presentation.ui.components

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vsu.test.data.api.model.dto.EventDTO

@Composable
fun CRUDEvent(
    eventDTO: EventDTO?,
    onSave: (EventDTO, List<Uri>) -> Unit,
    onCancel: (EventDTO, List<Uri>) -> Unit
) {
    var title by remember { mutableStateOf(eventDTO?.title ?: "") }
    var description by remember { mutableStateOf(eventDTO?.description ?: "") }
    var ageLimit by remember { mutableStateOf(eventDTO?.ageLimit ?: 0) }
    var selectedImages by remember { mutableStateOf<List<Uri>>(emptyList()) } // Добавляем состояние

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Название события") },
            modifier = Modifier.fillMaxWidth()
        )
        TextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Описание") },
            modifier = Modifier.fillMaxWidth()
        )
        Text("Ограничение по возрасту: $ageLimit")
        Slider(
            value = ageLimit.toFloat(),
            onValueChange = { ageLimit = it.toInt() },
            valueRange = 0f..100f,
            steps = 99
        )

        PhotoPicker { images -> selectedImages = images }
    }
    Box(modifier = Modifier.fillMaxSize()) {
        CombinedActions(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = 32.dp, y = (-40).dp),
            leftButton = {
                val updatedEvent = EventDTO(
                    id = eventDTO?.id ?: 0L,
                    title = title,
                    description = description,
                    ageLimit = ageLimit
                )
                onSave(updatedEvent, selectedImages)
            },
            rightButton = {
                val updatedEvent = EventDTO(
                    id = eventDTO?.id ?: 0L,
                    title = title,
                    description = description,
                    ageLimit = ageLimit
                )
                onCancel(updatedEvent, selectedImages)
            },
            Icons.Default.Done,
            Icons.Default.ArrowBack
        )
    }
}
