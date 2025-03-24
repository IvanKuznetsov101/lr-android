package com.vsu.test.presentation.viewmodel

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.vsu.test.data.TokenManager
import com.vsu.test.domain.model.LocationData
import com.vsu.test.domain.usecase.DeleteLightRoomByEventIdUseCase
import com.vsu.test.domain.usecase.GetEventStateUseCase
import com.vsu.test.domain.usecase.MoreState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class MoreViewModel @Inject constructor(
    private val getEventStateUseCase: GetEventStateUseCase,
    private val deleteLightRoomByEventIdUseCase: DeleteLightRoomByEventIdUseCase,
    private val fusedLocationClient: FusedLocationProviderClient,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _state = MutableStateFlow<MoreState>(MoreState.NoEvents)
    val state: StateFlow<MoreState> = _state

    fun updateState(context: Context) {
        viewModelScope.launch {
            if (checkSelfPermission(context, ACCESS_FINE_LOCATION) == PERMISSION_GRANTED ||
                checkSelfPermission(context, ACCESS_COARSE_LOCATION) == PERMISSION_GRANTED
            ) {
                val location = fusedLocationClient.lastLocation.await()
                if (location != null) {
                    val profileId = tokenManager.getId()
                    val locationDto = LocationData(profileId, location.latitude, location.longitude)
                    _state.value = getEventStateUseCase(locationDto)
                }
            }
        }
    }
    fun deleteLightRoomByEventId(id: Long, context: Context){
        viewModelScope.launch {
            deleteLightRoomByEventIdUseCase.invoke(id)
            updateState(context)
        }
    }
}