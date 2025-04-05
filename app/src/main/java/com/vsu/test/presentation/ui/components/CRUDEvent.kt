package com.vsu.test.presentation.ui.components

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
        OutlinedTextField(
            value = title,
            onValueChange = { title = it},
            placeholder = { Text("Название события") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(32.dp),
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            placeholder = { Text("Описание") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(32.dp),
        )
        Spacer(modifier = Modifier.height(8.dp))
        AgeRatingDropdown(
            ageLimit = ageLimit,
            onAgeLimitChange = { newAgeLimit ->
                ageLimit = newAgeLimit // Обновляем ageLimit
            }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgeRatingDropdown(
    ageLimit: Int, 
    onAgeLimitChange: (Int) -> Unit
) {
    val ageRatings = listOf(0, 6, 12, 16, 18, 21, 25, 30)
    var expanded by remember { mutableStateOf(false) }
    var selectedRating by remember { mutableStateOf(ageLimit) }



    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            colors = OutlinedTextFieldDefaults.colors(),
            value = "$selectedRating+",
            onValueChange = {},
            readOnly = true,
            placeholder = { Text("Описание") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            shape = RoundedCornerShape(32.dp),
            modifier = Modifier
                .width(110.dp)
                .menuAnchor()
                .background(
                    color = Color(0xFFFFFFFF),
                    shape = RoundedCornerShape(32.dp) // Закругление углов для TextField
                )
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(12.dp) // Закругление углов для DropdownMenu
                )
        ) {
            ageRatings.forEach { rating ->
                DropdownMenuItem(
                    text = { Text(text = "$rating+") },
                    onClick = {
                        selectedRating = rating
                        onAgeLimitChange(rating) // Обновляем ageLimit
                        expanded = false // Закрываем меню после выбора
                    }
                )
            }
        }
    }

}