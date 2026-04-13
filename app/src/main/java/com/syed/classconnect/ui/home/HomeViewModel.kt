package com.syed.classconnect.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.syed.classconnect.BuildConfig
import com.syed.classconnect.data.model.Announcement
import com.syed.classconnect.data.model.Assignment
import com.syed.classconnect.data.model.ClassRoom
import com.syed.classconnect.data.model.NewsArticle
import com.syed.classconnect.data.model.NewsSource
import com.syed.classconnect.data.model.User
import com.syed.classconnect.data.remote.NewsApiService
import com.syed.classconnect.data.repository.AssignmentRepository
import com.syed.classconnect.data.repository.AuthRepository
import com.syed.classconnect.data.repository.ClassRepository
import com.syed.classconnect.data.repository.FeedRepository
import com.syed.classconnect.data.repository.NotificationRepository
import com.syed.classconnect.util.ScheduleUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val auth: FirebaseAuth,
    private val classRepository: ClassRepository,
    private val assignmentRepository: AssignmentRepository,
    private val feedRepository: FeedRepository,
    private val notificationRepository: NotificationRepository,
    private val newsApiService: NewsApiService
) : ViewModel() {

    private val _currentUser = MutableLiveData<User?>()
    val currentUser: LiveData<User?> = _currentUser

    private val _todayClasses = MutableLiveData<List<TodayClassSessionItem>>(emptyList())
    val todayClasses: LiveData<List<TodayClassSessionItem>> = _todayClasses

    private val _upcomingDeadlines = MutableLiveData<List<Assignment>>(emptyList())
    val upcomingDeadlines: LiveData<List<Assignment>> = _upcomingDeadlines

    private val _recentAnnouncements = MutableLiveData<List<Announcement>>(emptyList())
    val recentAnnouncements: LiveData<List<Announcement>> = _recentAnnouncements

    private val _unreadCount = MutableLiveData(0)
    val unreadCount: LiveData<Int> = _unreadCount

    private val _news = MutableLiveData<List<NewsArticle>>(emptyList())
    val news: LiveData<List<NewsArticle>> = _news

    private val _newsStatus = MutableLiveData<String?>(null)
    val newsStatus: LiveData<String?> = _newsStatus

    fun loadCurrentUser() {
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            _currentUser.value = authRepository.getUserById(uid)
        }
    }

    fun loadStudentHomeData(uid: String) {
        viewModelScope.launch {
            // Unread count via realtime listener
            launch {
                try {
                    notificationRepository.getNotifications(uid).collect { list ->
                        _unreadCount.value = list.count { notif -> !notif.isRead }
                    }
                } catch (_: Exception) { /* offline — ignore */
                }
            }

            // Classes + deadlines + announcements
            launch {
                try {
                    val classes: List<ClassRoom> =
                        classRepository.getClassesForStudent(uid).first()

                    _todayClasses.value = classes
                        .flatMap { cls ->
                            ScheduleUtils.slotsForDay(cls.schedule).map { slot ->
                                TodayClassSessionItem(
                                    classRoom = cls,
                                    slot = slot,
                                    startMinutes = ScheduleUtils.startMinutesForSlot(slot)
                                        ?: Int.MAX_VALUE
                                )
                            }
                        }
                        .sortedWith(
                            compareBy<TodayClassSessionItem> { it.startMinutes }
                                .thenBy { it.classRoom.name.lowercase() }
                                .thenBy { it.slot.lowercase() }
                        )

                    val now: Timestamp = Timestamp.now()
                    val upcoming: List<Assignment> = classes
                        .flatMap { cls ->
                            try {
                                assignmentRepository.getAssignments(cls.id).first()
                                    .filter { a -> !a.dueDate.toDate().before(now.toDate()) }
                            } catch (_: Exception) {
                                emptyList()
                            }
                        }
                        .sortedBy { a -> a.dueDate.seconds }
                        .take(5)
                    _upcomingDeadlines.value = upcoming

                    val announcements: List<Announcement> = classes
                        .flatMap { cls ->
                            try {
                                feedRepository.getAnnouncements(cls.id).first()
                            } catch (_: Exception) {
                                emptyList()
                            }
                        }
                        .sortedWith(
                            compareByDescending<Announcement> { it.isPinned }
                                .thenByDescending { it.createdAt.seconds }
                        )
                        .take(3)
                    _recentAnnouncements.value = announcements

                } catch (_: Exception) { /* offline — ignore */
                }
            }
        }
    }

    fun loadTeacherHomeData(uid: String) {
        viewModelScope.launch {
            launch {
                try {
                    notificationRepository.getNotifications(uid).collect { list ->
                        _unreadCount.value = list.count { notif -> !notif.isRead }
                    }
                } catch (_: Exception) { /* offline — ignore */
                }
            }
            launch {
                try {
                    val classes: List<ClassRoom> =
                        classRepository.getClassesForTeacher(uid).first()
                    _todayClasses.value = classes
                        .flatMap { cls ->
                            ScheduleUtils.slotsForDay(cls.schedule).map { slot ->
                                TodayClassSessionItem(
                                    classRoom = cls,
                                    slot = slot,
                                    startMinutes = ScheduleUtils.startMinutesForSlot(slot)
                                        ?: Int.MAX_VALUE
                                )
                            }
                        }
                        .sortedWith(
                            compareBy<TodayClassSessionItem> { it.startMinutes }
                                .thenBy { it.classRoom.name.lowercase() }
                                .thenBy { it.slot.lowercase() }
                        )
                } catch (_: Exception) { /* offline — ignore */
                }
            }
            launch { loadNews() }
        }
    }

    fun loadNews() {
        viewModelScope.launch {
            try {
                val apiKey = BuildConfig.NEWS_API_KEY.trim()
                if (apiKey.isBlank() || apiKey.equals("null", ignoreCase = true)) {
                    _newsStatus.value = "News API key is missing. Showing fallback articles."
                    _news.value = getFallbackNews()
                    return@launch
                }
                val response = newsApiService.getEducationNews(apiKey = apiKey)
                if (response.isSuccessful) {
                    val articles = response.body()?.articles
                        ?.filter {
                            it.title.isNotBlank() &&
                                    it.title != "[Removed]" &&
                                    it.url.isNotBlank() &&
                                    it.source.name.isNotBlank()
                        }
                        ?: emptyList()
                    if (articles.isEmpty()) {
                        _newsStatus.value =
                            "No live education news found right now. Showing fallback articles."
                        _news.value = getFallbackNews()
                    } else {
                        _newsStatus.value = null
                        _news.value = articles
                    }
                } else {
                    val errorPreview = response.errorBody()?.string()?.take(200)
                    Timber.w("News API failed: code=${response.code()}, body=$errorPreview")
                    _newsStatus.value =
                        "Unable to fetch live news right now. Showing fallback articles."
                    _news.value = getFallbackNews()
                }
            } catch (e: Exception) {
                Timber.e(e, "News fetch failed")
                _newsStatus.value =
                    "Unable to fetch live news right now. Showing fallback articles."
                _news.value = getFallbackNews()
            }
        }
    }


    private fun getFallbackNews(): List<NewsArticle> = listOf(
        NewsArticle(
            source = NewsSource(null, "ClassConnect Tips"),
            title = "5 Ways to Make Online Learning More Effective",
            description = "Research-backed strategies for better student engagement",
            url = "https://www.edutopia.org",
            urlToImage = null,
            publishedAt = "",
            author = null,
            content = null
        ),
        NewsArticle(
            source = NewsSource(null, "Teaching Ideas"),
            title = "How AI is Transforming Modern Education",
            description = "Explore how artificial intelligence helps students learn faster",
            url = "https://www.educationweek.org",
            urlToImage = null,
            publishedAt = "",
            author = null,
            content = null
        ),
        NewsArticle(
            source = NewsSource(null, "Education Today"),
            title = "Best Practices for Online Assessments",
            description = "How to design fair and effective quizzes that accurately measure student understanding.",
            url = "https://www.teachthought.com",
            urlToImage = null,
            publishedAt = "",
            author = null,
            content = null
        ),
        NewsArticle(
            source = NewsSource(null, "EdTech Weekly"),
            title = "Building Better Feedback Loops in Digital Learning",
            description = "How timely feedback accelerates student progress and improves outcomes.",
            url = "https://www.edsurge.com",
            urlToImage = null,
            publishedAt = "",
            author = null,
            content = null
        )
    )
}
