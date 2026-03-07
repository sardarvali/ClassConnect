package com.syed.classconnect.ui.classes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.syed.classconnect.data.model.ClassRoom
import com.syed.classconnect.data.model.User
import com.syed.classconnect.data.repository.AuthRepository
import com.syed.classconnect.data.repository.ClassRepository
import com.syed.classconnect.util.Constants
import com.syed.classconnect.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ClassViewModel @Inject constructor(
    private val classRepository: ClassRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _classes = MutableLiveData<NetworkResult<List<ClassRoom>>>()
    val classes: LiveData<NetworkResult<List<ClassRoom>>> = _classes

    private val _currentUser = MutableLiveData<User?>()
    val currentUser: LiveData<User?> = _currentUser

    private val _createResult = MutableLiveData<NetworkResult<Pair<String, String>>>()
    val createResult: LiveData<NetworkResult<Pair<String, String>>> = _createResult

    private val _joinResult = MutableLiveData<NetworkResult<ClassRoom>>()
    val joinResult: LiveData<NetworkResult<ClassRoom>> = _joinResult

    private val _classDetail = MutableLiveData<ClassRoom?>()
    val classDetail: LiveData<ClassRoom?> = _classDetail

    fun loadUser(uid: String) {
        viewModelScope.launch {
            _currentUser.value = authRepository.getUserById(uid)
        }
    }

    fun loadClasses(userId: String, role: String) {
        _classes.value = NetworkResult.Loading()
        viewModelScope.launch {
            try {
                val flow = when (role) {
                    Constants.ROLE_TEACHER -> classRepository.getClassesForTeacher(userId)
                    Constants.ROLE_ADMIN -> {
                        val user = authRepository.getUserById(userId)
                        classRepository.getAllClasses(user?.institutionId ?: "")
                    }
                    else -> classRepository.getClassesForStudent(userId)
                }
                flow.collect { list -> _classes.value = NetworkResult.Success(list) }
            } catch (e: Exception) {
                _classes.value = NetworkResult.Error(e.message ?: "Failed to load classes")
            }
        }
    }

    fun createClass(classRoom: ClassRoom) {
        _createResult.value = NetworkResult.Loading()
        viewModelScope.launch {
            val result = classRepository.createClass(classRoom)
            _createResult.value = result.fold(
                onSuccess = { NetworkResult.Success(it) },
                onFailure = { NetworkResult.Error(it.message ?: "Failed to create class") }
            )
        }
    }

    fun joinClass(code: String, studentId: String) {
        _joinResult.value = NetworkResult.Loading()
        viewModelScope.launch {
            val result = classRepository.joinClass(code, studentId)
            _joinResult.value = result.fold(
                onSuccess = { NetworkResult.Success(it) },
                onFailure = { NetworkResult.Error(it.message ?: "Failed to join class") }
            )
        }
    }

    fun loadClassDetail(classId: String) {
        viewModelScope.launch {
            _classDetail.value = classRepository.getClassById(classId)
        }
    }

    fun getClassPreview(code: String, onResult: (ClassRoom?) -> Unit) {
        viewModelScope.launch {
            onResult(classRepository.getClassPreviewByCode(code))
        }
    }
}

