# StudentHomeViewModel / TeacherHomeViewModel / AdminDashboardViewModel — See HomeViewModel

---

## 📁 Location
`app/src/main/java/com/syed/classconnect/ui/home/HomeViewModel.kt`

---

## 🎯 What This File Does
The project uses a single `HomeViewModel` for all home screens. It provides data loading functions for students (`loadStudentHomeData`), teachers (`loadTeacherHomeData`), and shared functionality like `loadCurrentUser()` and `loadNews()`.

---

## 📦 Imports — Every Line Explained

| Import | Library / Package | What It Is | Why This File Needs It |
|--------|------------------|-----------|----------------------|
| `androidx.lifecycle.*` | AndroidX Lifecycle | LiveData, ViewModel | State management |
| `com.google.firebase.Timestamp` | Firebase | Timestamp | Date comparisons for deadlines |
| `com.google.firebase.auth.FirebaseAuth` | Firebase Auth | Auth | Get current user UID |
| `com.syed.classconnect.data.model.*` | App | Data models | User, ClassRoom, Assignment, etc. |
| `com.syed.classconnect.data.remote.NewsApiService` | App | API service | Fetch education news |
| `com.syed.classconnect.data.repository.*` | App | Repositories | All data access |
| `com.syed.classconnect.BuildConfig` | App | Build config | NEWS_API_KEY |
| `dagger.hilt.android.lifecycle.HiltViewModel` | Hilt | DI | ViewModel injection |
| `kotlinx.coroutines.flow.first` | Coroutines | Flow collection | One-shot Flow collection |
| `kotlinx.coroutines.launch` | Coroutines | Coroutines | Launches async work |
| `java.util.Calendar` | Java | Date operations | Get today's day name |
| `javax.inject.Inject` | Hilt | DI | Constructor injection |

---

## 📋 Properties

| Property | Type | What It Stores |
|----------|------|---------------|
| `currentUser` | `LiveData<User?>` | Currently logged-in user |
| `todayClasses` | `LiveData<List<ClassRoom>>` | Classes scheduled for today |
| `upcomingDeadlines` | `LiveData<List<Assignment>>` | Next 5 upcoming assignment deadlines |
| `recentAnnouncements` | `LiveData<List<Announcement>>` | Latest 3 announcements across all classes |
| `unreadCount` | `LiveData<Int>` | Number of unread notifications |
| `news` | `LiveData<List<NewsArticle>>` | Education news articles |

---

## ⚙️ Key Functions

### `loadStudentHomeData(uid: String)`
Loads all student dashboard data in parallel coroutines:
1. Real-time unread notification count
2. Classes enrolled → filters today's classes by schedule
3. Assignments across all classes → filters upcoming (not overdue) → takes top 5
4. Announcements across all classes → sorts by date → takes top 3

### `loadTeacherHomeData(uid: String)`
Loads teacher dashboard data:
1. Real-time unread notification count
2. Teacher's classes → filters today's classes
3. Education news

### `loadNews()`
Calls NewsAPI for education news. Falls back to hardcoded articles if API key is missing or request fails.

---

## ⚠️ Important Notes
- Uses `Flow.first()` for one-shot collection of class/assignment data (not real-time on home screen)
- `getFallbackNews()` provides hardcoded articles when NewsAPI is unavailable
- Today's classes are filtered by matching `Calendar.DAY_OF_WEEK` display name against `schedule` map keys
- Notification count uses real-time collection (keeps counting even as new notifications arrive)

