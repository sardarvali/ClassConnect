package com.syed.classconnect.ui.profile

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.syed.classconnect.data.model.User
import com.syed.classconnect.data.repository.AuthRepository
import com.syed.classconnect.data.repository.StorageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val storageRepository: StorageRepository
) : ViewModel() {

    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> = _user

    private val _photoUrl = MutableLiveData<String>()
    val photoUrl: LiveData<String> = _photoUrl

    fun loadProfile(uid: String) {
        viewModelScope.launch {
            _user.value = authRepository.getUserById(uid)
        }
    }

    fun updateProfile(uid: String, name: String, bio: String) {
        viewModelScope.launch {
            authRepository.updateUserProfile(uid, name, bio, _user.value?.photoUrl ?: "")
            _user.value = _user.value?.copy(name = name, bio = bio)
        }
    }

    fun uploadProfilePhoto(uid: String, uri: Uri) {
        viewModelScope.launch {
            val result = storageRepository.uploadProfilePhoto(uid, uri)
            result.getOrNull()?.let { url ->
                _photoUrl.value = url
<<<<<<< HEAD
                authRepository.updateUserProfile(uid, _user.value?.name ?: "", _user.value?.bio ?: "", url)
=======
                authRepository.updateUserProfile(
                    uid,
                    _user.value?.name ?: "",
                    _user.value?.bio ?: "",
                    url
                )
>>>>>>> final
            }
        }
    }
}

