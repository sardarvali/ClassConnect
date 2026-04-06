# TeacherHomeFragment — Teacher's main dashboard screen

---

## 📁 Location
`app/src/main/java/com/syed/classconnect/ui/home/TeacherHomeFragment.kt`

---

## 🎯 What This File Does
TeacherHomeFragment is the first screen teachers see after login. Similar to StudentHomeFragment but tailored for teachers — shows today's teaching schedule, quick action buttons (create class, take attendance), and education news. Teachers see their own classes rather than enrolled classes.

---

## ⚙️ Key Functions

### `onViewCreated(view, savedInstanceState)`
1. Displays greeting with teacher's name
2. Sets up today's classes RecyclerView (horizontal)
3. Sets up news RecyclerView
4. Quick action buttons for common teacher tasks
5. Calls `viewModel.loadTeacherHomeData(uid)`
6. Observes LiveData: currentUser, todayClasses, news

---

## 🔄 Data Flow
```
Fragment.onViewCreated()
    → viewModel.loadTeacherHomeData(uid)
    → HomeViewModel:
        ├── ClassRepository.getClassesForTeacher(uid) → filters today's classes
        ├── NotificationRepository.getNotifications(uid) → unread count
        └── NewsApiService.getEducationNews() → news articles
    → LiveData updates → adapters update UI
```

---

## 🧩 This File Depends On

| Dependency | Why |
|-----------|-----|
| `HomeViewModel` | Loads teacher dashboard data |
| `TodayClassesAdapter` | Today's class cards |
| `NewsAdapter` | Education news |

