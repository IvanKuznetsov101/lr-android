package com.vsu.test.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vsu.test.data.api.model.dto.ProfileDTO
import com.vsu.test.domain.model.SignUpData
import com.vsu.test.domain.usecase.CreateProfileUseCase
import com.vsu.test.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegistrationViewModel @Inject constructor(
    private val createProfileUseCase: CreateProfileUseCase
) : ViewModel() {
    private val _registrationState = MutableLiveData<NetworkResult<ProfileDTO>?>(null)
    val registrationState: LiveData<NetworkResult<ProfileDTO>?> = _registrationState

    private val _registrationEvent = MutableSharedFlow<RegistrationEvent>()
    val registrationEvent: SharedFlow<RegistrationEvent> = _registrationEvent
    val error = MutableStateFlow<String>("")

    sealed class RegistrationEvent {
        object Success : RegistrationEvent()
        data class Error(val message: String) : RegistrationEvent()
    }

    fun createProfile(signUpData: SignUpData) {
        viewModelScope.launch {
            try {
                val response = createProfileUseCase.invoke(signUpData)
                if (response is NetworkResult.Success) {
                    _registrationEvent.emit(RegistrationEvent.Success)
                } else {
                    error.value = response.message ?: "Error"
                    _registrationEvent.emit(RegistrationEvent.Error(response.message ?: "Error"))
                }
            } catch (e: Exception) {
                error.value = e.message.toString()
            }
        }
    }
}
