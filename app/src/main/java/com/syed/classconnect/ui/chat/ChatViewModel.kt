package com.syed.classconnect.ui.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.syed.classconnect.data.model.ChatMessage
import com.syed.classconnect.data.model.ClassRoom
import com.syed.classconnect.data.model.User
import com.syed.classconnect.data.repository.AuthRepository
import com.syed.classconnect.data.repository.ChatRepository
import com.syed.classconnect.data.repository.ClassRepository
import com.syed.classconnect.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val authRepository: AuthRepository,
    private val classRepository: ClassRepository
) : ViewModel() {

    private val _messages = MutableLiveData<NetworkResult<List<ChatMessage>>>()
    val messages: LiveData<NetworkResult<List<ChatMessage>>> = _messages

    private val _currentUser = MutableLiveData<User?>()
    val currentUser: LiveData<User?> = _currentUser

    private val _classDetail = MutableLiveData<ClassRoom?>()
    val classDetail: LiveData<ClassRoom?> = _classDetail

    private val _isMuted = MutableLiveData<Boolean>(false)
    val isMuted: LiveData<Boolean> = _isMuted

    private val _clearResult = MutableLiveData<NetworkResult<Unit>>()
    val clearResult: LiveData<NetworkResult<Unit>> = _clearResult

    fun loadCurrentUser(uid: String) {
        viewModelScope.launch { _currentUser.value = authRepository.getUserById(uid) }
    }

    fun loadClassDetail(classId: String) {
        viewModelScope.launch {
            _classDetail.value = classRepository.getClassById(classId)
        }
    }

    fun loadMessages(classId: String) {
        _messages.value = NetworkResult.Loading()
        viewModelScope.launch {
            try {
                chatRepository.getMessages(classId).collect { list ->
                    _messages.value = NetworkResult.Success(list)
                }
            } catch (e: Exception) {
                _messages.value = NetworkResult.Error(e.message ?: "Failed")
            }
        }
    }

    fun sendMessage(classId: String, message: ChatMessage) {
        viewModelScope.launch { chatRepository.sendMessage(classId, message) }
    }

    fun deleteMessage(classId: String, messageId: String) {
        viewModelScope.launch { chatRepository.deleteMessage(classId, messageId) }
    }

    fun addReaction(classId: String, messageId: String, userId: String, emoji: String) {
        viewModelScope.launch { chatRepository.addReaction(classId, messageId, userId, emoji) }
    }

    fun clearChat(classId: String) {
        _clearResult.value = NetworkResult.Loading()
        viewModelScope.launch {
            val result = chatRepository.clearAllMessages(classId)
            _clearResult.value = result.fold(
                onSuccess = { NetworkResult.Success(Unit) },
                onFailure = { NetworkResult.Error(it.message ?: "Failed to clear chat") }
            )
        }
    }

    fun toggleMute() {
        _isMuted.value = !(_isMuted.value ?: false)
    }
}
