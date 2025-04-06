package com.vsu.test.presentation.viewmodel

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.util.Log
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.tasks.CancellationTokenSource
import com.vsu.test.data.api.model.dto.LightRoomDTO
import com.vsu.test.data.storage.TokenManager
import com.vsu.test.domain.model.LocationData
import com.vsu.test.domain.usecase.CreateVisitorUseCase
import com.vsu.test.domain.usecase.DeleteLightRoomByIdUseCase
import com.vsu.test.domain.usecase.GetEventStateUseCase
import com.vsu.test.domain.usecase.MoreState
import com.vsu.test.domain.usecase.UpdateEndTimeVisitorUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class MoreViewModel @Inject constructor(
    private val getEventStateUseCase: GetEventStateUseCase,
    private val deleteLightRoomByIdUseCase: DeleteLightRoomByIdUseCase,
    private var fusedLocationClient: FusedLocationProviderClient,
    private val tokenManager: TokenManager,
    private val createVisitorUseCase: CreateVisitorUseCase,
    private val updateEndTimeVisitorUseCase: UpdateEndTimeVisitorUseCase
) : ViewModel() {


    private val _eventState = MutableStateFlow<MoreState>(MoreState.NoEvents)
    val state: StateFlow<MoreState> = _eventState


    fun updateState(context: Context) {
        viewModelScope.launch {
            _eventState.value = MoreState.Loading
            val cancellationTokenSource = CancellationTokenSource()
            val priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            if (checkSelfPermission(context, ACCESS_FINE_LOCATION) == PERMISSION_GRANTED ||
                checkSelfPermission(context, ACCESS_COARSE_LOCATION) == PERMISSION_GRANTED
            ) {
                try {
                    fusedLocationClient.getCurrentLocation(priority, cancellationTokenSource.token)
                        .addOnSuccessListener { location ->
                            if (location != null) {
                                Log.d("Location", "Широта: ${location.latitude}, Долгота: ${location.longitude}")
                            } else {
                                Log.d("Location", "Местоположение не найдено")
                            }
                        }
                        .addOnFailureListener { e ->
                            Log.e("Location", "Ошибка получения местоположения: ${e.message}")
                        }
                } catch (e: SecurityException) {
                    Log.e("Location", "SecurityException: ${e.message}")
                }
                val location = fusedLocationClient.lastLocation.await()
                if (location != null) {
                    val profileId = tokenManager.getId()
                    val locationDto = LocationData(profileId, location.latitude, location.longitude)
                    _eventState.value = getEventStateUseCase(locationDto)
                }
            }
        }
    }
    fun deleteLightRoomById(id: Long, context: Context){
        viewModelScope.launch {
            deleteLightRoomByIdUseCase.invoke(id)
            updateState(context)
        }
    }
    fun createVisitor(lightRoomDTO: LightRoomDTO, context: Context){
        viewModelScope.launch {
            val profileId = tokenManager.getId()
            createVisitorUseCase.invoke(profileId, lightRoomDTO.id)
            updateState(context)
        }
    }
}