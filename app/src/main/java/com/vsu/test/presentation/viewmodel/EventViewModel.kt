package com.vsu.test.presentation.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.ImageLoader
import com.vsu.test.data.storage.TokenManager
import com.vsu.test.data.api.model.dto.EventDTO
import com.vsu.test.data.api.model.dto.LightRoomDTO
import com.vsu.test.data.storage.VisitorInfo
import com.vsu.test.data.storage.VisitorStorage
import com.vsu.test.domain.model.EventWithDetails
import com.vsu.test.domain.usecase.CreateEventUseCase
import com.vsu.test.domain.usecase.CreateLightRoomUseCase
import com.vsu.test.domain.usecase.CreateVisitorUseCase
import com.vsu.test.domain.usecase.DeleteEventUseCase
import com.vsu.test.domain.usecase.EventUseCase
import com.vsu.test.domain.usecase.GetEventByLightRoomIdUseCase
import com.vsu.test.domain.usecase.GetEventWithDetailsByLightRoomIdUseCase
import com.vsu.test.domain.usecase.GetVisitorStateUseCase
import com.vsu.test.domain.usecase.UpdateEventUseCase
import com.vsu.test.domain.usecase.UpdateImagesUseCase
import com.vsu.test.domain.usecase.UploadImagesUseCase
import com.vsu.test.utils.NetworkResult
import com.yandex.mapkit.geometry.Point
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EventViewModel @Inject constructor(
    private val eventUseCase: EventUseCase,
    private val createEventUseCase: CreateEventUseCase,
    private val updateEventUseCase: UpdateEventUseCase,
    private val deleteEventUseCase: DeleteEventUseCase,
    private val createVisitorUseCase: CreateVisitorUseCase,
    private val createLightRoomUseCase: CreateLightRoomUseCase,
    private val uploadImagesUseCase: UploadImagesUseCase,
    private val visitorStorage: VisitorStorage,
    private val getVisitorStateUseCase: GetVisitorStateUseCase,
    private val updateImagesUseCase: UpdateImagesUseCase,
    private val getEventByLightRoomIdUseCase: GetEventByLightRoomIdUseCase,
    val imageLoader: ImageLoader,
    private val tokenManager: TokenManager,
    private val getEventWithDetailsByLightRoomIdUseCase: GetEventWithDetailsByLightRoomIdUseCase
) : ViewModel() {
    val events = MutableStateFlow<List<EventDTO>>(emptyList())
    val event = MutableStateFlow<EventDTO>(EventDTO(0, null, null, null))
    val loading = MutableStateFlow(false)
    val error = MutableStateFlow<String?>(null)

    private  val _eventWithDetails = MutableStateFlow<EventWithDetails?>(null)
    val eventWithDetails: StateFlow<EventWithDetails?> = _eventWithDetails

    fun getEventWithDetailsByLightRoom(lightRoomDTO: LightRoomDTO) {
        viewModelScope.launch {
            try {
                loading.value = true
                _eventWithDetails.value = getEventWithDetailsByLightRoomIdUseCase.invoke(lightRoomDTO)
            } catch (e: Exception) {
                error.value = e.message
            } finally {
                loading.value = false
            }
        }

    }

    fun loadEvents(id: Long? = tokenManager.getId()) {
        viewModelScope.launch {
            loading.value = true
            try {
                val response = id?.let { eventUseCase.invoke(it) }
                if (response is NetworkResult.Success) {
                    events.value = (response.data ?: emptyList()).toList()
                    Log.d("EventViewModel", "События загружены: ${events.value.size}")
                } else if (response is NetworkResult.Error) {
                    error.value = response.message
                    Log.e("EventViewModel", "Ошибка загрузки событий: ${response.message}")
                }
            } catch (e: Exception) {
                error.value = e.message
                Log.e("EventViewModel", "Исключение при загрузке: ${e.message}")
            } finally {
                loading.value = false
            }
        }
    }

    fun createEvent(eventDTO: EventDTO, images: List<Uri>) {
        viewModelScope.launch {
            loading.value = true
            try {
                val response = createEventUseCase.invoke(tokenManager.getId(), eventDTO, true)
                loadEvents()

                if (response is NetworkResult.Error) {
                    error.value = response.message
                    Log.e("EventViewModel", "Ошибка создания события: ${response.message}")
                }
            } catch (e: Exception) {
                error.value = e.message
                Log.e("EventViewModel", "Исключение при создании: ${e.message}")
            } finally {
                loading.value = false
            }
        }
    }

    fun updateEvent(eventDTO: EventDTO, images: List<Uri>) {
        viewModelScope.launch {
            loading.value = true
            try {
                val response = updateEventUseCase.invoke(tokenManager.getId(), eventDTO, true)
                loadEvents()
                if (response is NetworkResult.Error) {
                    error.value = response.message
                    Log.e("EventViewModel", "Ошибка создания события: ${response.message}")
                }
            } catch (e: Exception) {
                error.value = e.message
                Log.e("EventViewModel", "Исключение при создании: ${e.message}")
            } finally {
                loading.value = false
            }
        }
    }

    fun createEventAndCreateLightRoom(
        eventDTO: EventDTO,
        userCoordinates: Point?,
        images: List<Uri>
    ) {
        viewModelScope.launch {
            loading.value = true
            try {
                val response = createEventUseCase.invoke(tokenManager.getId(), eventDTO, true)
                val imageResponse = uploadImagesUseCase.invoke(
                    images = images,
                    eventId = response.data?.id,
                    profileId = null
                )
                if (response is NetworkResult.Success) {
                    val createdEvent = response.data ?: eventDTO.copy(id = 0L)
                    uploadImagesUseCase.invoke(
                        images = images,
                        eventId = eventDTO.id,
                        profileId = null
                    )
                    val lightRoomResponse = createLightRoomUseCase.invoke(
                        userCoordinates!!.latitude,
                        userCoordinates.longitude,
                        createdEvent.id.takeIf { it != 0L }
                            ?: throw IllegalStateException("ID события не получено")
                    )
                    loadEvents()
                    if (lightRoomResponse is NetworkResult.Success){
                        val visitorResponse = lightRoomResponse.data?.let {
                            createVisitorUseCase.invoke(tokenManager.getId(),
                                it.id, visitorStorage.getVisitorId())
                        }
                        if (visitorResponse is NetworkResult.Success && visitorResponse.data != null){
                            visitorStorage.saveVisitorInfo(VisitorInfo(visitorResponse.data.idVisitor, lightRoomResponse.data.id, tokenManager.getId()))
                        }
                    } else{
                        error.value = "Ошибка создания LightRoom"
                        Log.e(
                            "EventViewModel",
                            "Ошибка создания LightRoom: ${lightRoomResponse.message}"
                        )
                    }
                } else {
                    error.value = response.message
                    Log.e("EventViewModel", "Ошибка создания события: ${response.message}")
                }
            } catch (e: Exception) {
                error.value = e.message
                Log.e("EventViewModel", "Исключение при создании: ${e.message}")
            } finally {
                loading.value = false
            }
        }
    }

    fun updateEventAndCreateLightRoom(
        eventDTO: EventDTO,
        images: List<Uri>,
        userCoordinates: Point?
    ) {
        viewModelScope.launch {
            loading.value = true
            try {
                val response = updateEventUseCase.invoke(tokenManager.getId(), eventDTO, true)
                val imageResponse = updateImagesUseCase.invoke(
                    images = images,
                    eventId = eventDTO.id,
                    profileId = null
                )
                if (response is NetworkResult.Success) {
                    val lightRoomResponse = createLightRoomUseCase.invoke(
                        userCoordinates!!.latitude,
                        userCoordinates.longitude,
                        eventDTO.id
                    )
                    loadEvents()
                    if (lightRoomResponse is NetworkResult.Success) {
                        val visitorResponse = lightRoomResponse.data?.let {
                            createVisitorUseCase.invoke(
                                tokenManager.getId(),
                                it.id, visitorStorage.getVisitorId()
                            )
                        }
                        if (visitorResponse is NetworkResult.Success && visitorResponse.data != null) {
                            visitorStorage.saveVisitorInfo(
                                VisitorInfo(
                                    visitorResponse.data.idVisitor,
                                    lightRoomResponse.data.id,
                                    tokenManager.getId()
                                )
                            )
                        }
                    } else {
                        error.value = "Ошибка обновления LightRoom"
                        Log.e(
                            "EventViewModel",
                            "Ошибка обновления LightRoom: ${lightRoomResponse.message}"
                        )
                    }
                } else {
                    error.value = response.message
                    Log.e("EventViewModel", "Ошибка обновления события: ${response.message}")
                }
            } catch (e: Exception) {
                error.value = e.message
                Log.e("EventViewModel", "Исключение при обновлении: ${e.message}")
            } finally {
                loading.value = false
            }
        }
    }

    fun deleteEvent(eventId: Long) {
        viewModelScope.launch {
            loading.value = true
            try {
                val response = deleteEventUseCase.invoke(eventId)
                if (response is NetworkResult.Success) {
                    loadEvents() // Обновляем список после успешного удаления
                } else {
                    error.value = response.message
                    Log.e("EventViewModel", "Ошибка удаления события: ${response.message}")
                }
            } catch (e: Exception) {
                error.value = e.message
                Log.e("EventViewModel", "Исключение при удалении: ${e.message}")
            } finally {
                loading.value = false
            }
        }
    }

    fun getEventByLightRoomId(lightRoomId: Long) {
        viewModelScope.launch {
            loading.value = true
            try {
                val response = getEventByLightRoomIdUseCase.invoke(lightRoomId)
                if (response is NetworkResult.Success) {
                    event.value = response.data ?: EventDTO(0, null, null, null)
                    Log.d("EventViewModel", "Ивент загружен: ${event.value.id}")
                } else if (response is NetworkResult.Error) {
                    error.value = response.message
                    Log.e("EventViewModel", "Ошибка загрузки ивента: ${response.message}")
                }
            } catch (e: Exception) {
                error.value = e.message
                Log.e("EventViewModel", "Исключение при загрузке: ${e.message}")
            } finally {
                loading.value = false
            }
        }
    }
    fun checkProfileLightRoom(): Boolean{
        return visitorStorage.getVisitorId()!=null
//        viewModelScope.launch {
//            val visitor = getVisitorStateUseCase.invoke()
//            return@launch
//        }

    }
}