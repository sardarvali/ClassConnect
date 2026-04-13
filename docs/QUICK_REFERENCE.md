# ClassConnect - Quick Reference & Checklist

> **Date:** April 10, 2026  
> **Purpose:** Quick lookup for developers and designers

---

## 🎯 Quick Navigation

### Find a Component (Fast Lookup)

| Component | Location | Purpose |
|-----------|----------|---------|
| **LoginFragment** | `ui/auth/LoginFragment.kt` | Email/password login |
| **StudentHomeFragment** | `ui/home/StudentHomeFragment.kt` | Student dashboard |
| **TeacherHomeFragment** | `ui/home/TeacherHomeFragment.kt` | Teacher dashboard |
| **AdminDashboardFragment** | `ui/home/AdminDashboardFragment.kt` | Admin dashboard |
| **AdminClassesFragment** | `ui/admin/AdminClassesFragment.kt` | Class management |
| **UserManagementFragment** | `ui/admin/UserManagementFragment.kt` | User management |
| **AttendanceFragment** | `ui/attendance/AttendanceFragment.kt` | QR code scanning |
| **ClassDetailActivity** | `ui/classes/ClassDetailActivity.kt` | Class details view |
| **StudentsFragment** | `ui/classes/StudentsFragment.kt` | Class students list |
| **FeedFragment** | `ui/classes/feed/FeedFragment.kt` | Class announcements |
| **AssignmentsFragment** | `ui/assignments/AssignmentsFragment.kt` | Assignment list |
| **QuizListFragment** | `ui/quiz/QuizListFragment.kt` | Available quizzes |
| **QuizAttemptActivity** | `ui/quiz/QuizAttemptActivity.kt` | Take quiz |
| **SplashActivity** | `ui/splash/SplashActivity.kt` | App startup |
| **ProfileFragment** | `ui/profile/ProfileFragment.kt` | User profile |
| **SettingsManager** | `ui/settings/SettingsManager.kt` | App settings |

---

## 🏗️ Architecture Overview (ASCII Art)

```
┌─────────────────────────────────────────────────────────────────┐
│                    ANDROID CLASSCONNECT                         │
└─────────────────────────────────────────────────────────────────┘

                      UI LAYER (Fragments)
        ┌──────────┬──────────┬──────────┬──────────┐
        │  Admin   │ Attendance│  Home   │  Quiz    │
        │  Mgmt    │  Tracking │ Dashboard│Creation │
        └──────────┴──────────┴──────────┴──────────┘
                          │
        ┌─────────────────┼─────────────────┐
        │                 │                 │
    VIEWMODEL LAYER  (State Management)
        │                 │                 │
    Manage StateFlow    Coroutines      Error Handling
        │                 │                 │
        └─────────────────┼─────────────────┘
                          │
        ┌─────────────────┼─────────────────┐
        │                 │                 │
    REPOSITORY LAYER  (Data Abstraction)
        │                 │                 │
    Firestore Queries  Remote Config   Local Cache
        │                 │                 │
        └─────────────────┼─────────────────┘
                          │
    ┌───────────────────────────────────────────┐
    │        FIREBASE (Backend Services)        │
    ├───────────────────────────────────────────┤
    │ • Firestore (Database)                    │
    │ • Authentication                          │
    │ • Storage (Files)                         │
    │ • Realtime Listeners                      │
    └───────────────────────────────────────────┘
```

---

## 🚀 Data Flow Example: Loading Assignments

```
┌─────────────────────────────────────────────────────────────────┐
│  AssignmentsFragment.kt                                         │
│  ┌──────────────────────────────────────────────────────────┐   │
│  │ 1. User opens Assignments                               │   │
│  │ 2. Fragment calls viewModel.loadAssignments()           │   │
│  │ 3. Observes viewModel.assignmentsState (StateFlow)      │   │
│  │    • Collects state updates                             │   │
│  │    • Lifecycle-aware (stops when paused)                │   │
│  └──────────────────────────────────────────────────────────┘   │
│                        │                                         │
│                        │ (calls)                                │
│                        ▼                                         │
│  ┌──────────────────────────────────────────────────────────┐   │
│  │  AssignmentsViewModel.kt                                │   │
│  │  ┌──────────────────────────────────────────────────────┐│   │
│  │  │ Emits states:                                        ││   │
│  │  │ • Loading → Show skeleton                           ││   │
│  │  │ • Success(data) → Update adapter                    ││   │
│  │  │ • Error(msg) → Show error banner                    ││   │
│  │  │                                                      ││   │
│  │  │ Uses viewModelScope.launch:                         ││   │
│  │  │ • Safe cancellation on ViewModel destruction        ││   │
│  │  │ • Automatic error handling                          ││   │
│  │  └──────────────────────────────────────────────────────┘│   │
│  │                        │                                 │   │
│  │                        │ (calls)                        │   │
│  │                        ▼                                 │   │
│  │  ┌──────────────────────────────────────────────────────┐│   │
│  │  │  AssignmentRepository.kt                            ││   │
│  │  │  ┌──────────────────────────────────────────────────┐││   │
│  │  │  │ Single source of truth for data:                │││   │
│  │  │  │ • Fetches from Firestore                        │││   │
│  │  │  │ • Applies filtering/sorting                     │││   │
│  │  │  │ • Handles pagination (20 items/page)           │││   │
│  │  │  │ • Caches locally                                │││   │
│  │  │  └──────────────────────────────────────────────────┘││   │
│  │  │                        │                             │   │
│  │  │                        │                             │   │
│  │  │                        ▼                             │   │
│  │  │  ┌──────────────────────────────────────────────────┐││   │
│  │  │  │  Firebase Firestore                             │││   │
│  │  │  │  Query: assignments                             │││   │
│  │  │  │  where classId = "current_class"                │││   │
│  │  │  │  orderBy createdAt desc                         │││   │
│  │  │  │  limit 20                                        │││   │
│  │  │  └──────────────────────────────────────────────────┘││   │
│  │  └──────────────────────────────────────────────────────┘│   │
│  └──────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│  LAZY LOADING IN ACTION                                         │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  Initial Load:        ┌─────────────────┐                      │
│  ┌────┐┌────┐┌────┐  │ Page 0           │                      │
│  │ 1  ││ 2  ││ 3  │  │ Items 1-20       │                      │
│  ├────┤├────┤├────┤  │ ✓ Loaded         │                      │
│  │ 4  ││ 5  ││ 6  │  └─────────────────┘                      │
│  ├────┤├────┤├────┤                                            │
│  │ 7  ││ 8  ││ 9  │  User scrolls to item 18:                 │
│  ├────┤├────┤├────┤  viewModel.loadNextPage() called          │
│  │ 10 ││ 11 ││ 12 │                                            │
│  └────┘└────┘└────┘  ┌─────────────────┐                      │
│  │ ... │ ... │ ... │  │ Page 1           │                      │
│  │ ... │ ... │ ... │  │ Items 21-40      │                      │
│  │ ... │ ... │ ... │  │ ⏳ Loading...     │                      │
│  │ 20  │ 21* │ 22* │  └─────────────────┘                      │
│  └────┘└────┘└────┘                                            │
│          ↑                                                      │
│      View scrolls     After load completes:                    │
│      here → triggers  Adapter updates RecyclerView             │
│      pagination       New items smoothly inserted              │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

---

## 📋 Lazy Loading Checklist

### When to Use Lazy Loading

```
✅ USE LAZY LOADING WHEN:
  □ List has > 20 items
  □ Items have heavy data (images, videos)
  □ Network requests are involved
  □ Users may not view all data
  
  Examples: Assignments, Classes, Students, Quiz questions

❌ DON'T USE WHEN:
  □ Small lists (< 20 items)
  □ All data fits in memory
  □ Real-time updates on all items needed
  
  Examples: Form options, settings, quick actions
```

### Lazy Loading Techniques

```
┌─────────────────────┬────────────────┬──────────────────┐
│ Technique           │ When to Use    │ Difficulty      │
├─────────────────────┼────────────────┼──────────────────┤
│ Pagination          │ Large lists    │ Easy ✅          │
│ ViewPager Caching   │ Tab content    │ Easy ✅          │
│ Image Lazy Load     │ Avatar/photos  │ Easy ✅          │
│ Virtual Scrolling   │ Very large lists│ Hard ⚠️          │
│ Firestore Listeners │ Real-time      │ Medium ⚠️        │
│ Cursor-based Load   │ Very large DB  │ Hard ⚠️          │
└─────────────────────┴────────────────┴──────────────────┘
```

---

## 🔍 Common Patterns (Copy-Paste Ready)

### Pattern 1: Simple StateFlow Collection

```kotlin
// In Fragment
override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    
    viewLifecycleOwner.lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.uiState.collect { state ->
                when (state) {
                    is Loading -> showProgressBar()
                    is Success -> showData(state.data)
                    is Error -> showError(state.message)
                }
            }
        }
    }
}
```

### Pattern 2: Pagination Implementation

```kotlin
// In ViewModel
class MyViewModel : ViewModel() {
    private val _page = MutableStateFlow(0)
    
    fun loadNextPage() {
        viewModelScope.launch {
            try {
                val newItems = repository.getItems(page = _page.value)
                _page.value++
                _items.value = _items.value + newItems
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }
}

// In Adapter
override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    if (position >= itemCount - 5) {
        onLoadMore?.invoke()  // Trigger load when near end
    }
}
```

### Pattern 3: Firestore Pagination

```kotlin
// In Repository
suspend fun getItems(page: Int, pageSize: Int = 20): List<Item> {
    val query = firestore.collection("items")
        .orderBy("createdAt", Query.Direction.DESCENDING)
        .limit(pageSize.toLong())
        .offset(page * pageSize)
    
    return query.get().await().toObjects(Item::class.java)
}
```

### Pattern 4: Image Lazy Loading

```kotlin
// In ViewHolder
fun bind(item: Item) {
    Glide.with(itemView)
        .load(item.imageUrl)
        .placeholder(R.drawable.skeleton)
        .thumbnail(0.1f)  // Show low-res first
        .into(imageView)
}
```

### Pattern 5: Tab Lazy Loading

```kotlin
// In Activity
viewPager.offscreenPageLimit = 1  // Cache adjacent tabs
viewPager.adapter = TabAdapter(supportFragmentManager, lifecycle)

// Each Fragment loads only when visible
override fun onViewCreated(...) {
    if (isVisible) {
        viewModel.loadData()
    }
}
```

---

## 🧪 Testing Checklist

```
UNIT TESTS:
  □ ViewModels emit correct states
  □ Repositories handle errors
  □ Validation functions work
  □ Date/time utilities correct

INTEGRATION TESTS:
  □ Fragments display data correctly
  □ Navigation works end-to-end
  □ Pagination triggers correctly
  □ Images load without crash

PERFORMANCE TESTS:
  □ List scrolls at 60 FPS
  □ Loading time < 2s
  □ Memory < 300 MB
  □ No ANR (Application Not Responding)

ACCESSIBILITY TESTS:
  □ Content descriptions present
  □ Touch targets ≥ 48dp
  □ Contrast ratio ≥ 4.5:1
  □ TalkBack navigation works
```

---

## 🛠️ Debugging Tips

### Common Issues

| Issue | Solution |
|-------|----------|
| **Blank screen on load** | Check ViewModel initialization, StateFlow emission |
| **Scroll janky** | Use DiffUtil, limit offscreen pages, profile with Profiler |
| **Images don't load** | Check URL, Glide cache, network permissions |
| **Memory leak** | Use `viewLifecycleOwner.lifecycleScope`, check listeners |
| **Pagination stuck** | Verify Firestore offset, check loading state logic |
| **Fragment not visible** | Check navigation graph, fragment lifecycle |

### Debug Commands

```bash
# Check memory usage
adb shell dumpsys meminfo com.syed.classconnect

# Monitor frame rate (detect janky scrolling)
adb shell dumpsys gfxinfo com.syed.classconnect framestats

# Check database queries
adb logcat | grep "Firestore"

# Profile network
Android Studio Profiler → Network tab
```

---

## 📊 Performance Targets

| Metric | Target | Current* |
|--------|--------|---------|
| **Time to First Paint** | < 1s | 0.8s |
| **List Load (20 items)** | < 500ms | 420ms |
| **Scroll Framerate** | 60 FPS | 55-60 FPS |
| **Memory Usage** | < 250 MB | 185 MB |
| **APK Size** | < 30 MB | 28 MB |

*Estimated values - run Profiler to verify

---

## 🚀 Release Checklist

Before submitting to Play Store:

```
CODE QUALITY:
  □ No lint warnings
  □ 70%+ test coverage
  □ Code reviewed by 2 people
  
FUNCTIONALITY:
  □ All screens responsive
  □ Navigation works fully
  □ Error handling for all cases
  
PERFORMANCE:
  □ Profiler: 60 FPS sustained
  □ Memory stable (no leaks)
  □ Battery impact acceptable
  
SECURITY:
  □ API keys not in source
  □ Permissions justified
  □ Data encryption enabled
  
ACCESSIBILITY:
  □ TalkBack tested
  □ Contrast ratios met
  □ Touch targets ≥ 48dp
  
DESIGN:
  □ Dark mode tested
  □ All screen sizes tested
  □ Brand colors consistent
  □ Typography consistent
```

---

## 📞 Quick Reference Links

### In This Project

- **Main docs**: `docs/syedexplain.md`
- **Architecture**: `docs/setup/ARCHITECTURE.md`
- **Dependencies**: `docs/setup/DEPENDENCIES.md`
- **Component Library**: `docs/COMPONENT_LIBRARY.md`
- **UI Improvements**: `docs/IMPROVEMENTS_UI.md`
- **Technical Guide**: `docs/IMPROVEMENTS_TECHNICAL.md`

### External Resources

- [Android Architecture Components](https://developer.android.com/topic/architecture)
- [Firestore Best Practices](https://firebase.google.com/docs/firestore/best-practices)
- [Material Design 3](https://m3.material.io/)
- [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html)
- [Android Profiler](https://developer.android.com/studio/profile/android-profiler)

---

## 🎯 Key Takeaway

**ClassConnect is built with:**
- ✅ **Modern Architecture** (MVVM + Clean)
- ✅ **Lazy Loading** (Pagination, efficient scrolling)
- ✅ **Reactive State** (StateFlow for smooth updates)
- ✅ **Memory Safe** (Lifecycle-aware, no leaks)
- ✅ **User Friendly** (Skeleton loading, error handling)

**To add a new feature:**
1. Create Fragment + ViewModel
2. Set up StateFlow in ViewModel
3. Collect in Fragment with `repeatOnLifecycle`
4. Implement Repository if needed
5. Add to Navigation graph
6. Write tests

Done! 🚀

---

*Last Updated: April 10, 2026*

