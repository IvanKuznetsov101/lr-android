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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.vsu.test.data.api.model.dto.EventDTO
import com.vsu.test.presentation.viewmodel.EventViewModel

@Composable
fun CRUDEvent(
    eventDTO: EventDTO?,
    onSave: (EventDTO, CurrentImages) -> Unit,
    onCancel: (EventDTO, CurrentImages) -> Unit,
    eventViewModel: EventViewModel
) {
    var title by remember { mutableStateOf(eventDTO?.title ?: "") }
    var description by remember { mutableStateOf(eventDTO?.description ?: "") }
    var ageLimit by remember { mutableStateOf(eventDTO?.ageLimit ?: 0) }
    val focusManager = LocalFocusManager.current

    val currentImages by eventViewModel.currentImages.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value = title,
            modifier = Modifier.fillMaxWidth(),
            onValueChange = { title = it },
            label = { Text("Название события") },
            shape = RoundedCornerShape(32.dp),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Black,
                unfocusedIndicatorColor = Color.Gray,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                cursorColor = Color.White,
                focusedLabelColor = Color.Black,
            ),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                }
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = description,
            modifier = Modifier.fillMaxWidth(),
            onValueChange = { description = it },
            label = { Text("Название события") },
            shape = RoundedCornerShape(32.dp),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Black,
                unfocusedIndicatorColor = Color.Gray,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                cursorColor = Color.White,
                focusedLabelColor = Color.Black,
            ),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                }
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        AgeRatingDropdown(
            ageLimit = ageLimit,
            onAgeLimitChange = { newAgeLimit ->
                ageLimit = newAgeLimit
            }
        )
        PhotoPicker(
            currentImages = currentImages,
            onAddUris = { uris -> eventViewModel.addSelectedUris(uris) },
            onRemoveUri = { uri -> eventViewModel.removeSelectedUri(uri) },
            onMarkUrlForDeletion = { url -> eventViewModel.markUrlForDeletion(url) },
            eventViewModel = eventViewModel
        )
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
                onSave(updatedEvent, currentImages)
            },
            rightButton = {
                val updatedEvent = EventDTO(
                    id = eventDTO?.id ?: 0L,
                    title = title,
                    description = description,
                    ageLimit = ageLimit
                )
                onCancel(updatedEvent, currentImages)
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
                    shape = RoundedCornerShape(32.dp)
                )
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(12.dp)
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

data class ImageUrl(
    val imageUrl: String,
    val isDelete: Boolean = false
)

sealed class CurrentImages {
    object Loading : CurrentImages()
    data class Images(
        var imagesUris: List<Uri>? = null,
        var imagesUrls: List<ImageUrl>? = null
    ) : CurrentImages()

    object NoImages : CurrentImages()

}