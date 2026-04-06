# StudentHomeFragment — Student's main dashboard screen

---

## 📁 Location
`app/src/main/java/com/syed/classconnect/ui/home/StudentHomeFragment.kt`

---

## 🎯 What This File Does
StudentHomeFragment is the first screen students see after login. It displays a personalized greeting, today's classes, upcoming assignment deadlines, recent announcements across all enrolled classes, and education news. It also integrates with the SensorHandler for shake-to-refresh functionality.

---

## 📦 Imports — Every Line Explained

| Import | Library / Package | What It Is | Why This File Needs It |
|--------|------------------|-----------|----------------------|
| `android.content.Intent` | Android SDK | Start Activities | Opens ClassDetailActivity, WebViewActivity |
| `android.os.Bundle` | Android SDK | Saved state | Fragment lifecycle |
| `android.view.*` | Android SDK | View classes | Fragment inflation |
| `androidx.fragment.app.Fragment` | AndroidX | Base class | Fragment base |
| `androidx.fragment.app.viewModels` | AndroidX KTX | ViewModel delegation | `by viewModels()` |
| `androidx.recyclerview.widget.LinearLayoutManager` | AndroidX | RecyclerView layout | Horizontal/vertical scrolling lists |
| `com.syed.classconnect.databinding.FragmentStudentHomeBinding` | ViewBinding | Type-safe views | Layout binding |
| `com.syed.classconnect.sensor.SensorHandler` | App | Shake detection | Shake-to-refresh |
| `com.syed.classconnect.ui.classes.ClassDetailActivity` | App | Class detail | Navigate on class card tap |
| `com.syed.classconnect.ui.webview.WebViewActivity` | App | In-app browser | Open news articles |
| `com.syed.classconnect.util.*` | App | Extensions | show/hide, animations, toasts |
| `com.google.firebase.auth.FirebaseAuth` | Firebase Auth | Auth | Get current user UID |
| `dagger.hilt.android.AndroidEntryPoint` | Hilt | DI | Enables injection |

---

## ⚙️ Key Functions

### `onViewCreated(view, savedInstanceState)`
1. Sets up greeting text with user's name
2. Configures RecyclerViews for: today's classes (horizontal), upcoming deadlines, recent announcements, news
3. Registers SensorHandler for shake-to-refresh
4. Calls `viewModel.loadCurrentUser()` and `viewModel.loadStudentHomeData(uid)`
5. Observes LiveData: currentUser, todayClasses, upcomingDeadlines, recentAnnouncements, news

### `setupAdapters()`
Creates and attaches adapters for each section with click listeners for navigation.

### `onDestroyView()`
Unregisters SensorHandler and nulls binding.

---

## 🔄 Data Flow
```
Fragment.onViewCreated()
    → viewModel.loadStudentHomeData(uid)
    → HomeViewModel collects from multiple repositories:
        ├── ClassRepository.getClassesForStudent(uid) → filters today's classes
        ├── AssignmentRepository.getAssignments(classId) → upcoming deadlines
        ├── FeedRepository.getAnnouncements(classId) → recent announcements
        └── NewsApiService.getEducationNews() → news articles
    → LiveData updates → Fragment observes → adapters update RecyclerViews
```

---

## 🧩 This File Depends On

| Dependency | Why |
|-----------|-----|
| `HomeViewModel` | Loads all dashboard data |
| `SensorHandler` | Shake-to-refresh |
| `TodayClassesAdapter` | Displays today's class cards |
| `UpcomingDeadlinesAdapter` | Displays upcoming deadlines |
| `RecentAnnouncementsAdapter` | Displays recent announcements |
| `NewsAdapter` | Displays news articles |

