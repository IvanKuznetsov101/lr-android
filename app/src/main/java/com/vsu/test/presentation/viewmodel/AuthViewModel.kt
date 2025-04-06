package com.vsu.test.presentation.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vsu.test.data.api.model.dto.JwtResponse
import com.vsu.test.data.repository.AuthRepository
import com.vsu.test.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _loginEvent = MutableSharedFlow<LoginEvent>()
    val loginEvent: SharedFlow<LoginEvent> = _loginEvent
    val error = MutableStateFlow<String>("")

    sealed class LoginEvent {
        object Success : LoginEvent()
        data class Error(val message: String) : LoginEvent()
    }

    fun login(username: String, password: String) {
        viewModelScope.launch {

            val response = authRepository.login(username, password)
            if (response is NetworkResult.Success){
                _loginEvent.emit(LoginEvent.Success)
            }
            else{
                error.value = "Invalid username or password"
                _loginEvent.emit(LoginEvent.Error(response.message ?: "Error"))
            }
        }
    }
}