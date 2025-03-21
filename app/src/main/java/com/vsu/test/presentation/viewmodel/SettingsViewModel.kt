package com.vsu.test.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vsu.test.domain.usecase.CreateEventUseCase
import com.vsu.test.domain.usecase.LogoutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val logoutUseCase: LogoutUseCase,
):ViewModel() {
    fun logout() {
        viewModelScope.launch {
            logoutUseCase.invoke()
        }
    }
}