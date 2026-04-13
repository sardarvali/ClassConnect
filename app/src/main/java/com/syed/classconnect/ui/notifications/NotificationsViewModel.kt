package com.syed.classconnect.ui.notifications

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.syed.classconnect.data.model.AppNotification
import com.syed.classconnect.data.repository.NotificationRepository
import com.syed.classconnect.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository
) : ViewModel() {

    private val _notifications = MutableLiveData<NetworkResult<List<AppNotification>>>()
    val notifications: LiveData<NetworkResult<List<AppNotification>>> = _notifications

    fun loadNotifications(userId: String) {
        _notifications.value = NetworkResult.Loading()
        viewModelScope.launch {
            try {
                notificationRepository.getNotifications(userId).collect { list ->
                    _notifications.value = NetworkResult.Success(list)
                }
            } catch (e: Exception) {
                _notifications.value = NetworkResult.Error(e.message ?: "Failed")
            }
        }
    }

    fun markAsRead(userId: String, notificationId: String) {
        viewModelScope.launch { notificationRepository.markAsRead(userId, notificationId) }
    }

    fun markAllAsRead(userId: String) {
        viewModelScope.launch { notificationRepository.markAllAsRead(userId) }
    }

    fun deleteNotification(userId: String, notificationId: String) {
        viewModelScope.launch { notificationRepository.deleteNotification(userId, notificationId) }
    }
}

