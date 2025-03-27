package com.vsu.test.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.ImageLoader
import com.vsu.test.data.api.model.dto.ExtendedProfileDTO
import com.vsu.test.data.storage.TokenManager
import com.vsu.test.domain.model.ProfileWithDetails
import com.vsu.test.domain.model.ProfileWithImage
import com.vsu.test.domain.usecase.GetProfileByIdUseCase
import com.vsu.test.domain.usecase.GetProfileWithDetailsByIdUseCase
import com.vsu.test.domain.usecase.MoreState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    val imageLoader: ImageLoader,
    private val getProfileWithDetailsByIdUseCase: GetProfileWithDetailsByIdUseCase,
) : ViewModel() {

    private val _profileState = MutableStateFlow<ProfileState>(ProfileState.Loading)
    val profileState: StateFlow<ProfileState> = _profileState

    fun loadProfile(profileId: Long) {
        viewModelScope.launch {
            _profileState.value = ProfileState.Loading
            try {
                val profile = getProfileWithDetailsByIdUseCase(profileId)
                if(profile != null){
                    _profileState.value = ProfileState.Success(profile)
                }
            } catch (e: Exception) {
                _profileState.value = ProfileState.Error("Ошибка загрузки: ${e.message}")
            }
        }
    }

    sealed class ProfileState {
        object Loading : ProfileState()
        data class Success(val profile: ProfileWithDetails) : ProfileState() // Просмотр
        data class Error(val message: String) : ProfileState()
    }
}