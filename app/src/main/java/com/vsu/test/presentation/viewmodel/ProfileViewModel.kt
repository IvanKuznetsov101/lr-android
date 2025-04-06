package com.vsu.test.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.ImageLoader
import com.vsu.test.data.api.model.dto.LastEvent
import com.vsu.test.domain.model.ProfileWithDetails
import com.vsu.test.domain.usecase.GetLastEventUseCase
import com.vsu.test.domain.usecase.GetProfileWithDetailsByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    val imageLoader: ImageLoader,
    private val getProfileWithDetailsByIdUseCase: GetProfileWithDetailsByIdUseCase,
    private val getLastEventUseCase: GetLastEventUseCase
) : ViewModel() {

    private val _profileState = MutableStateFlow<ProfileState>(ProfileState.Loading)
    val profileState: StateFlow<ProfileState> = _profileState

    fun loadProfile(profileId: Long, isOwnProfile: Boolean) {
        viewModelScope.launch {
            _profileState.value = ProfileState.Loading
            try {
                val profile = getProfileWithDetailsByIdUseCase(profileId)
                if (profile != null) {
                    if (isOwnProfile) {
                        val events = getLastEventUseCase.invoke(profileId)
                        _profileState.value = ProfileState.Success(profile, events)
                    } else {
                        _profileState.value = ProfileState.Success(profile, null)
                    }
                }
            } catch (e: Exception) {
                _profileState.value = ProfileState.Error("Ошибка загрузки: ${e.message}")
            }
        }
    }

    sealed class ProfileState {
        object Loading : ProfileState()
        data class Success(
            val profile: ProfileWithDetails,
            val events: List<LastEvent>?
        ) : ProfileState()

        data class Error(val message: String) : ProfileState()
    }
}