package com.vsu.test.presentation.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.ImageLoader
import com.vsu.test.data.storage.TokenManager
import com.vsu.test.domain.model.ProfileWithImage
import com.vsu.test.domain.usecase.GetProfileByIdUseCase
import com.vsu.test.domain.usecase.UpdateImagesUseCase
import com.vsu.test.domain.usecase.UpdateProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    val imageLoader: ImageLoader,
    private val updateProfileUseCase: UpdateProfileUseCase,
    private val getProfileByIdUseCase: GetProfileByIdUseCase,
    private val updateImagesUseCase: UpdateImagesUseCase,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _editProfileState = MutableStateFlow<EditProfileState>(EditProfileState.Loading)
    val editProfileState: StateFlow<EditProfileState> = _editProfileState

    private val _avatarUri = MutableStateFlow<Uri?>(null)
    val avatarUri: StateFlow<Uri?> = _avatarUri

    init {
        loadProfileForEdit()
    }

    fun loadProfileForEdit() {
        viewModelScope.launch {
            _editProfileState.value = EditProfileState.Loading
            val profileId = tokenManager.getId()
            try {
                val profile = getProfileByIdUseCase(profileId)
                _editProfileState.value = EditProfileState.Success(profile, profile)

            } catch (e: Exception) {
                _editProfileState.value = EditProfileState.Error("Ошибка загрузки: ${e.message}")
            }
        }
    }

    fun updateProfile() {
        viewModelScope.launch {
            val currentState = _editProfileState.value
            if (currentState !is EditProfileState.Success) return@launch
            _editProfileState.value = EditProfileState.Loading

            try {
                updateProfileUseCase.invoke(currentState.editedProfile.profile)
                val uriList: List<Uri> = _avatarUri.value?.let { listOf(it) } ?: emptyList()
                updateImagesUseCase.invoke(
                    images = uriList,
                    profileId = currentState.editedProfile.profile.id,
                    eventId = null
                )
                _editProfileState.value = EditProfileState.Success(
                    originalProfile = currentState.editedProfile,
                    editedProfile = currentState.editedProfile,
                    isSaving = false,
                    isSaved = true
                )
            } catch (e: Exception) {
                _editProfileState.value =
                    currentState.copy(errorMessage = e.message.toString() ?: "Ошибка отправки")
            }
        }
    }

    fun updateFullName(fullName: String) {
        val currentState = _editProfileState.value as? EditProfileState.Success ?: return
        val updatedProfile = currentState.editedProfile.copy(
            profile = currentState.editedProfile.profile.copy(fullName = fullName)
        )
        _editProfileState.value = currentState.copy(editedProfile = updatedProfile)
    }

    fun updateUsername(username: String) {
        val currentState = _editProfileState.value as? EditProfileState.Success ?: return
        val updatedProfile = currentState.editedProfile.copy(
            profile = currentState.editedProfile.profile.copy(username = username)
        )
        _editProfileState.value = currentState.copy(editedProfile = updatedProfile)
    }

    fun updateEmail(email: String) {
        val currentState = _editProfileState.value as? EditProfileState.Success ?: return
        val updatedProfile = currentState.editedProfile.copy(
            profile = currentState.editedProfile.profile.copy(email = email)
        )
        _editProfileState.value = currentState.copy(editedProfile = updatedProfile)
    }

    fun updateDateOfBirth(dateOfBirth: LocalDate) {
        val currentState = _editProfileState.value as? EditProfileState.Success ?: return
        val updatedProfile = currentState.editedProfile.copy(
            profile = currentState.editedProfile.profile.copy(date_of_birth = dateOfBirth)
        )
        _editProfileState.value = currentState.copy(editedProfile = updatedProfile)
    }

    fun updateAvatarUrl(avatarUrl: String) {
        val currentState = _editProfileState.value as? EditProfileState.Success ?: return
        val updatedProfile = currentState.editedProfile.copy(profileImageUrl = avatarUrl)
        _editProfileState.value = currentState.copy(editedProfile = updatedProfile)
    }

    fun updateAvatarUri(uri: Uri) {
        _avatarUri.value = uri
    }

    sealed class EditProfileState {
        object Loading : EditProfileState()
        data class Success(
            val originalProfile: ProfileWithImage,
            val editedProfile: ProfileWithImage,
            val isSaving: Boolean = false,
            val isSaved: Boolean = false,
            val errorMessage: String = ""
        ) : EditProfileState()

        data class Error(val message: String) : EditProfileState()
    }
}