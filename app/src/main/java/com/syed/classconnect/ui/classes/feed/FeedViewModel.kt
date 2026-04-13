package com.syed.classconnect.ui.classes.feed

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.syed.classconnect.data.model.Announcement
import com.syed.classconnect.data.repository.AuthRepository
import com.syed.classconnect.data.repository.FeedRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class FeedItem {
    data class AnnouncementItem(val announcement: Announcement) : FeedItem()
}

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val feedRepository: FeedRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _feedItems = MutableLiveData<List<FeedItem>>()
    val feedItems: LiveData<List<FeedItem>> = _feedItems

    private val _userRole = MutableLiveData<String>()
    val userRole: LiveData<String> = _userRole

    var currentUserName: String = "Teacher"
        private set

    fun loadUserRole(uid: String) {
        viewModelScope.launch {
            val user = authRepository.getUserById(uid)
            _userRole.value = user?.role ?: "student"
            currentUserName = user?.name ?: "Teacher"
        }
    }

    fun loadFeed(classId: String) {
        viewModelScope.launch {
            try {
                feedRepository.getAnnouncements(classId).collect { announcements ->
                    val sorted =
                        announcements.sortedWith(compareByDescending<Announcement> { it.isPinned }
                            .thenByDescending { it.createdAt.seconds })
                    _feedItems.value = sorted.map { FeedItem.AnnouncementItem(it) }
                }
            } catch (e: Exception) {
                _feedItems.value = emptyList()
            }
        }
    }

    fun postAnnouncement(
        classId: String,
        title: String,
        body: String,
        authorId: String,
        authorName: String
    ) {
        viewModelScope.launch {
            feedRepository.postAnnouncement(
                classId, Announcement(
                    title = title, body = body,
                    authorId = authorId, authorName = authorName,
                    createdAt = Timestamp.now()
                )
            )
        }
    }

    fun togglePin(classId: String, announcementId: String, pinned: Boolean) {
        viewModelScope.launch { feedRepository.togglePin(classId, announcementId, pinned) }
    }

    fun deleteAnnouncement(classId: String, announcementId: String) {
        viewModelScope.launch { feedRepository.deleteAnnouncement(classId, announcementId) }
    }
}
