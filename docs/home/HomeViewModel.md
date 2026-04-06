# HomeViewModel.kt — Shared ViewModel that loads student/teacher home data including classes, deadlines, announcements, unread count, and news

---

## 📁 File Location
`app/src/main/java/com/syed/classconnect/ui/home/HomeViewModel.kt`

---

## 🎯 What This File Does
`HomeViewModel` is a single ViewModel shared by both `StudentHomeFragment` and `TeacherHomeFragment`. It aggregates data from five different repositories (Auth, Class, Assignment, Feed, Notification) plus a Retrofit API (NewsApi) to populate the home screen. For students it loads: today's classes (filtered from all enrolled classes by today's day name), upcoming deadlines (sorted, due in the future, max 5), recent announcements (from all classes, max 3), and unread notification count. For teachers it loads: today's classes they teach and education news articles. Without this ViewModel, both home screens would be blank — they have no data source of their own.

---

## 📦 Every Import — Explained

| Import | From Library | What It Is | Why Used Here |
|--------|-------------|-----------|--------------|
| `androidx.lifecycle.LiveData` | AndroidX Lifecycle | Read-only observable | Public properties |
| `androidx.lifecycle.MutableLiveData` | AndroidX Lifecycle | Writable observable | Private backing fields |
| `androidx.lifecycle.ViewModel` | AndroidX Lifecycle | Lifecycle-aware state container | HomeViewModel extends it |
| `androidx.lifecycle.viewModelScope` | AndroidX Lifecycle | Coroutine scope tied to ViewModel | Launching coroutines |
| `com.google.firebase.Timestamp` | Firebase Firestore | Firestore timestamp type | Comparing assignment due dates |
| `com.google.firebase.auth.FirebaseAuth` | Firebase Auth | Current user access | Getting UID |
| `com.syed.classconnect.data.model.Announcement` | Project | Announcement data class | Type for `_recentAnnouncements` |
| `com.syed.classconnect.data.model.Assignment` | Project | Assignment data class | Type for `_upcomingDeadlines` |
| `com.syed.classconnect.data.model.ClassRoom` | Project | Class data class | Type for `_todayClasses` |
| `com.syed.classconnect.data.model.NewsArticle` | Project | News article data class | Type for `_news` |
| `com.syed.classconnect.data.model.NewsSource` | Project | News source data class | Used in fallback news |
| `com.syed.classconnect.data.model.User` | Project | User data class | Type for `_currentUser` |
| `com.syed.classconnect.data.remote.NewsApiService` | Project | Retrofit API interface | `getEducationNews()` |
| `com.syed.classconnect.data.repository.AssignmentRepository` | Project | Assignment Firestore ops | `getAssignments()` |
| `com.syed.classconnect.data.repository.AuthRepository` | Project | User Firestore ops | `getUserById()` |
| `com.syed.classconnect.data.repository.ClassRepository` | Project | Class Firestore ops | `getClassesForStudent/Teacher()` |
| `com.syed.classconnect.data.repository.FeedRepository` | Project | Announcements Firestore ops | `getAnnouncements()` |
| `com.syed.classconnect.data.repository.NotificationRepository` | Project | Notification Firestore ops | `getNotifications()` |
| `com.syed.classconnect.BuildConfig` | Project (generated) | Build-time config values | `NEWS_API_KEY` |
| `dagger.hilt.android.lifecycle.HiltViewModel` | Hilt | ViewModel DI marker | `by viewModels()` works with Hilt |
| `kotlinx.coroutines.flow.first` | Coroutines Flow | Get first emission from Flow | Collect one snapshot from repository Flows |
| `kotlinx.coroutines.launch` | Coroutines | Start a coroutine | Parallel data loading |
| `java.util.Calendar` | Java | Calendar and date utilities | Getting today's day name |
| `javax.inject.Inject` | Javax / Hilt | Constructor injection marker | `@Inject constructor(...)` |

---

## 🔑 Every Keyword, Annotation & Concept Used

### `@HiltViewModel` + `@Inject constructor`
Hilt creates this ViewModel and auto-provides all 7 constructor parameters. No factory class needed.

### `launch { }` for parallel loading
Student home data loads 3 concurrent coroutines:
```kotlin
viewModelScope.launch {
    launch { /* unread notification count */ }
    launch { /* classes + deadlines + announcements */ }
}
```
The inner `launch` calls start concurrently — the classes and notifications load in parallel, halving the wait time.

### `.first()` on a Flow
`classRepository.getClassesForStudent(uid)` returns `Flow<List<ClassRoom>>`. Calling `.first()` suspends until the Flow emits its first value (the current Firestore snapshot), then returns that value. We don't need to keep listening — a single snapshot is sufficient for the home screen.

### `Timestamp.now()` + comparing seconds
`Timestamp` is Firebase's server-side timestamp type. `now.toDate().before(now.toDate())` converts to Java `Date` for comparison. `a.dueDate.seconds` gives the Unix epoch second for sorting.

### `getFallbackNews()`
If the News API key is blank or the API call fails, returns 4 hardcoded educational articles. This ensures the UI always has something to display, even offline or without an API key.

### `getDayName(): String`
Returns the English day name ("Monday", "Tuesday", etc.) regardless of the device's locale. Classes have schedules stored as `Map<String, String>` where the key is the English day name. Without this function, a device set to French would return "lundi" which would never match "Monday".

---

## 🏗️ Class Structure
`HomeViewModel @Inject constructor(7 dependencies) : ViewModel()` — serves both student and teacher home fragments.

---

## 📋 Properties

| Property | Type | Modifier | What It Holds | Why It Exists |
|----------|------|----------|--------------|--------------|
| `_currentUser` | `MutableLiveData<User?>` | `private` | Current logged-in user | Drives greeting text on home |
| `currentUser` | `LiveData<User?>` | `val` | Read-only | Observed by home fragments |
| `_todayClasses` | `MutableLiveData<List<ClassRoom>>` | `private` | Classes scheduled for today | Today's timetable section |
| `todayClasses` | `LiveData<List<ClassRoom>>` | `val` | Read-only | TodayClassesAdapter |
| `_upcomingDeadlines` | `MutableLiveData<List<Assignment>>` | `private` | Future assignments (max 5) | Deadline cards |
| `upcomingDeadlines` | `LiveData<List<Assignment>>` | `val` | Read-only | UpcomingDeadlinesAdapter |
| `_recentAnnouncements` | `MutableLiveData<List<Announcement>>` | `private` | Recent announcements (max 3) | Announcements section |
| `recentAnnouncements` | `LiveData<List<Announcement>>` | `val` | Read-only | RecentAnnouncementsAdapter |
| `_unreadCount` | `MutableLiveData<Int>` | `private` | Unread notification count | Notification badge |
| `unreadCount` | `LiveData<Int>` | `val` | Read-only | Badge in home fragment |
| `_news` | `MutableLiveData<List<NewsArticle>>` | `private` | Education news articles | Teacher home news section |
| `news` | `LiveData<List<NewsArticle>>` | `val` | Read-only | NewsAdapter |

---

## ⚙️ Functions

### `loadCurrentUser()`
**Purpose:** Loads the current user's name, role, and photo for the home screen greeting.
**Step by step:**
1. Gets UID from `auth.currentUser?.uid`, returns if null.
2. Calls `authRepository.getUserById(uid)` in a coroutine.
3. Sets `_currentUser.value`.

### `loadStudentHomeData(uid: String)`
**Purpose:** Loads all data for `StudentHomeFragment` in parallel.
**Step by step:**
1. Inner launch 1: collects `notificationRepository.getNotifications(uid)` Flow, counts unread.
2. Inner launch 2:
   - Gets all classes for this student via `classRepository.getClassesForStudent(uid).first()`.
   - Filters to today's classes using `getDayName()`.
   - For each class, gets assignments and filters to future due dates, sorts, takes first 5.
   - For each class, gets announcements, sorts by recency, takes first 3.
3. All exceptions are swallowed — offline tolerance.

### `loadTeacherHomeData(uid: String)`
**Purpose:** Loads data for `TeacherHomeFragment`.
**Step by step:**
1. Inner launch 1: unread notification count (same as student).
2. Inner launch 2: gets classes for this teacher, filters to today.
3. Inner launch 3: calls `loadNews()`.

### `loadNews()`
**Purpose:** Fetches education news from NewsAPI.
**Step by step:**
1. Gets `NEWS_API_KEY` from `BuildConfig`. If blank → uses fallback.
2. Calls `newsApiService.getEducationNews(apiKey = apiKey)`.
3. Filters out articles with null titles or "[Removed]" title.
4. Falls back to `getFallbackNews()` if response fails or is empty.

### `getDayName(): String`
**Purpose:** Returns English day name for today, locale-independent.

### `getFallbackNews(): List<NewsArticle>`
**Purpose:** Returns 4 hardcoded educational articles when the News API is unavailable.

---

## 🔄 Data Flow Diagram
```
StudentHomeFragment.onViewCreated()
        ↓
viewModel.loadCurrentUser() + viewModel.loadStudentHomeData(uid)
        ↓
                    ┌──────────────────────────────────────────────┐
                    │  Parallel coroutines                          │
                    │  launch 1: notifications → _unreadCount       │
                    │  launch 2: classes → today filter              │
                    │            assignments → upcoming 5            │
                    │            announcements → recent 3            │
                    └──────────────────────────────────────────────┘
                                        ↓
                    _todayClasses, _upcomingDeadlines, _recentAnnouncements set
                                        ↓
                    Fragment observers update adapters → UI displays data
```

---

## 🧩 Dependencies

| Depends On | Why |
|-----------|-----|
| `AuthRepository` | Load current user, student profile |
| `ClassRepository` | Get enrolled / taught classes |
| `AssignmentRepository` | Get upcoming deadlines |
| `FeedRepository` | Get recent class announcements |
| `NotificationRepository` | Get unread notification count |
| `NewsApiService` | Education news for teacher home |

---

## ⚠️ Important Notes & Gotchas
- All exceptions in `loadStudentHomeData` and `loadTeacherHomeData` are caught and ignored. This is intentional for offline tolerance but means errors are silent — no error state is shown to the user.
- `getDayName()` uses `Calendar.DAY_OF_WEEK` which returns 1 (Sunday) through 7 (Saturday). The explicit `when` avoids locale-dependent `SimpleDateFormat("EEEE")`.
- `.first()` on a Flow consumes only the first emission. If the user's class list changes while on the home screen, it won't auto-update. A full `collect {}` would be needed for real-time updates.

