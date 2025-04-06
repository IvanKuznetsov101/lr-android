package com.vsu.test.presentation.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vsu.test.presentation.viewmodel.RegistrationViewModel
import java.time.LocalDate
import kotlinx.coroutines.launch
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import com.vsu.test.domain.model.SignUpData
import com.vsu.test.presentation.ui.components.DefaultButton
import com.vsu.test.presentation.ui.components.ErrorMessageBox

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationScreen(viewModel: RegistrationViewModel = hiltViewModel(),
                       onNavigateToLogin: () -> Unit) {
    var fullName by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var dateOfBirth by remember { mutableStateOf(LocalDate.now().minusYears(18)) }
    var showDatePicker by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val errorMessage by viewModel.error.collectAsState()
    val focusManager = LocalFocusManager.current
    // Подписка на состояние из ViewModel
    val registrationState by viewModel.registrationState.observeAsState()

    // Coroutine scope для асинхронных операций
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Поле "Full Name"
        OutlinedTextField(
            value = fullName,
            onValueChange = { fullName = it },
            label = { Text("Full Name") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            shape = RoundedCornerShape(32.dp),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Black,
                unfocusedIndicatorColor = Color.Gray,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                cursorColor = Color.Black,
                focusedLabelColor = Color.Black,
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Поле "Username"
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            shape = RoundedCornerShape(32.dp),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Black,
                unfocusedIndicatorColor = Color.Gray,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                cursorColor = Color.Black,
                focusedLabelColor = Color.Black,

            ),

        )

        Spacer(modifier = Modifier.height(8.dp))

        // Поле "Password"
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            shape = RoundedCornerShape(32.dp),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Black,
                unfocusedIndicatorColor = Color.Gray,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                cursorColor = Color.Black,
                focusedLabelColor = Color.Black,
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Поле "Email"
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            shape = RoundedCornerShape(32.dp),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Black,
                unfocusedIndicatorColor = Color.Gray,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                cursorColor = Color.Black,
                focusedLabelColor = Color.Black,
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Поле "Date of Birth" с DatePicker
        OutlinedTextField(
            value = dateOfBirth.toString(),
            onValueChange = {},
            label = { Text("Date of Birth") },
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            shape = RoundedCornerShape(32.dp),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Black,
                unfocusedIndicatorColor = Color.Gray,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                cursorColor = Color.Black,
                focusedLabelColor = Color.Black,
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

        if (showDatePicker) {
            val datePickerState = rememberDatePickerState()
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            datePickerState.selectedDateMillis?.let { millis ->
                                dateOfBirth = LocalDate.ofEpochDay(millis / 86400000)
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
        Spacer(modifier = Modifier.height(16.dp))
        if(errorMessage.isNotEmpty()){
            Spacer(modifier = Modifier.height(16.dp))
            ErrorMessageBox(errorMessage)
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Кнопка "Register"
        DefaultButton(
            onClick = {
                coroutineScope.launch {
                val signUpData = SignUpData(fullName, username, password, email, dateOfBirth)
                viewModel.createProfile(signUpData)
            } },
            text = "Registration",
            icon = null
        )
        Spacer(modifier = Modifier.height(16.dp))
        DefaultButton(
            onClick = { onNavigateToLogin()},
            text = "Login",
            icon = null
        )

        LaunchedEffect(Unit) {
            viewModel.registrationEvent.collect { event ->
                when (event) {
                    is RegistrationViewModel.RegistrationEvent.Success -> onNavigateToLogin()
                    is RegistrationViewModel.RegistrationEvent.Error ->
                        Toast.makeText(context, event.message, Toast.LENGTH_LONG).show()

                }
            }
        }
    }
}