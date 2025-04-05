package com.vsu.test.presentation.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.vsu.test.R
import com.vsu.test.domain.model.ProfileWithImage
import com.vsu.test.presentation.ui.components.CombinedActions
import com.vsu.test.presentation.ui.components.ErrorMessageBox
import com.vsu.test.presentation.viewmodel.EditProfileViewModel
import com.vsu.test.presentation.viewmodel.ProfileViewModel
import java.time.LocalDate

@Composable
fun EditProfileScreen(
    editProfileViewModel: EditProfileViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onProfileUpdated: () -> Unit
) {
    val state by editProfileViewModel.editProfileState.collectAsState()
    val avatarUri by editProfileViewModel.avatarUri.collectAsState()

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let { editProfileViewModel.updateAvatarUri(it) }
    }

    LaunchedEffect(state) {
        if (state is EditProfileViewModel.EditProfileState.Success && (state as EditProfileViewModel.EditProfileState.Success).isSaved) {
            onProfileUpdated()
        }
    }

    LaunchedEffect(Unit) {
        editProfileViewModel.loadProfileForEdit()
    }

    when (state) {
        is EditProfileViewModel.EditProfileState.Loading -> LoadingScreen()
        is EditProfileViewModel.EditProfileState.Success -> {
            val profile = (state as EditProfileViewModel.EditProfileState.Success).editedProfile
            val errorMessage =  (state as EditProfileViewModel.EditProfileState.Success).errorMessage
            EditProfileContent(
                profile = profile,
                editProfileViewModel = editProfileViewModel,
                avatarUri = avatarUri,
                onBackClick = onBackClick,
                onProfileUpdated = onProfileUpdated,
                errorMessage = errorMessage,
                onImagePick = {
                    photoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }
            )
        }
        is EditProfileViewModel.EditProfileState.Error -> {
            val message = (state as EditProfileViewModel.EditProfileState.Error).message
            ErrorScreen(message)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileContent(
    profile: ProfileWithImage,
    editProfileViewModel: EditProfileViewModel,
    avatarUri: android.net.Uri?,
    onBackClick: () -> Unit,
    errorMessage: String,
    onProfileUpdated: () -> Unit,
    onImagePick: () -> Unit
) {
    var showDatePicker by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Edit profile",
            fontSize = 24.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
        AsyncImage(
            model = avatarUri ?: profile.profileImageUrl, // Сначала Uri, затем URL
            contentDescription = "Avatar",
            modifier = Modifier
                .size(380.dp)
                .clip(RoundedCornerShape(40.dp))
                .clickable { onImagePick() },
            contentScale = ContentScale.Crop,
            placeholder = painterResource(R.drawable.placeholder),
            error = painterResource(R.drawable.placeholder),
            imageLoader = editProfileViewModel.imageLoader
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = profile.profile.fullName,
            modifier = Modifier.fillMaxWidth(),
            onValueChange = { editProfileViewModel.updateFullName(it) },
            label = { Text("Full Name") },
            shape = RoundedCornerShape(32.dp),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Black,
                unfocusedIndicatorColor = Color.Gray
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = profile.profile.username,
            modifier = Modifier.fillMaxWidth(),
            onValueChange = { editProfileViewModel.updateUsername(it) },
            label = { Text("User Name") },
            shape = RoundedCornerShape(32.dp),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Black,
                unfocusedIndicatorColor = Color.Gray
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = profile.profile.email,
            modifier = Modifier.fillMaxWidth(),
            onValueChange = { editProfileViewModel.updateEmail(it) },
            label = { Text("Email") },
            shape = RoundedCornerShape(32.dp),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Black,
                unfocusedIndicatorColor = Color.Gray
            )
        )

        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = profile.profile.date_of_birth.toString(),
            onValueChange = {},
            label = { Text("Date of Birth") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(32.dp),
            readOnly = true,
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Black,
                unfocusedIndicatorColor = Color.Gray
            ),
            trailingIcon = {
                IconButton(onClick = { showDatePicker = true }) {
                    Icon(
                        imageVector = Icons.Filled.CalendarToday,
                        contentDescription = "Select Date"
                    )
                }
            }
        )
        if (errorMessage.isNotEmpty()){
            Spacer(modifier = Modifier.height(16.dp))
            ErrorMessageBox(errorMessage)
        }
        if (showDatePicker) {
            val datePickerState = rememberDatePickerState()
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            datePickerState.selectedDateMillis?.let { millis ->
                                val newDate = java.time.LocalDate.ofEpochDay(millis / 86400000)
                                editProfileViewModel.updateDateOfBirth(newDate)
                            }
                            showDatePicker = false
                        }
                    ) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) {
                        Text("Cancel")
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }
    }
    Box(modifier = Modifier.fillMaxSize()) {
        CombinedActions(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = 32.dp, y = (-40).dp),
            leftButton = { editProfileViewModel.updateProfile() },
            rightButton = { onBackClick() },
            Icons.Default.Done,
            Icons.Default.ArrowBack
        )
    }
}