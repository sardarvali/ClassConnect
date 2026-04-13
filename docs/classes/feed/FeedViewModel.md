# FeedViewModel.kt — ViewModel that loads, sorts, and manages class announcements for the Feed tab

---

## 📁 File Location
`app/src/main/java/com/syed/classconnect/ui/classes/feed/FeedViewModel.kt`

---

## 🎯 What This File Does
`FeedViewModel` manages the data for `FeedFragment` and `PostAnnouncementDialog`. It loads the current user's role (to control teacher vs student UI), loads announcements from `FeedRepository` as a real-time Flow, sorts them (pinned first, then by recency), posts new announcements, and toggles pin status. It also holds the `currentUserName` string so `PostAnnouncementDialog` can include the author name without an extra Firestore lookup. The `FeedItem` sealed class wraps `Announcement` — designed for future expansion to other feed item types (e.g., materials).

---

## 📦 Every Import — Explained

| Import | From Library | What It Is | Why Used Here |
|--------|-------------|-----------|--------------|
| `androidx.lifecycle.LiveData` | AndroidX | Read-only observable | Public state properties |
| `androidx.lifecycle.MutableLiveData` | AndroidX | Writable observable | Private backing fields |
| `androidx.lifecycle.ViewModel` | AndroidX | Lifecycle-aware state container | FeedViewModel extends it |
| `androidx.lifecycle.viewModelScope` | AndroidX | Coroutine scope tied to ViewModel | All coroutines |
| `com.google.firebase.Timestamp` | Firebase | Server timestamp | `Timestamp.now()` for new announcement |
| `com.syed.classconnect.data.model.Announcement` | Project | Announcement data class | Feed item type |
| `com.syed.classconnect.data.repository.AuthRepository` | Project | User Firestore lookup | `getUserById()` |
| `com.syed.classconnect.data.repository.FeedRepository` | Project | Announcement Firestore ops | `getAnnouncements()`, `postAnnouncement()`, `togglePin()` |
| `dagger.hilt.android.lifecycle.HiltViewModel` | Hilt | ViewModel DI marker | `by viewModels()` |
| `kotlinx.coroutines.launch` | Coroutines | Start coroutine | All operations |
| `javax.inject.Inject` | Javax / Hilt | Constructor injection | `@Inject constructor(...)` |

---

## 🔑 Every Keyword, Annotation & Concept Used

### `sealed class FeedItem`
Wraps `Announcement` in a sealed class. Currently only one subtype (`AnnouncementItem`), but designed for future expansion — a `MaterialItem` subtype could be added to mix materials into the same feed without changing `FeedAdapter`'s outer type.

```kotlin
sealed class FeedItem {
    data class AnnouncementItem(val announcement: Announcement) : FeedItem()
}
```

### `compareByDescending<Announcement> { it.isPinned }.thenByDescending { it.createdAt.seconds }`
Chains two comparators:
1. `isPinned` descending → `true` (1) before `false` (0) → pinned items float to top.
2. `createdAt.seconds` descending → newer items first within each group.

### `var currentUserName: String = "Teacher"` with `private set`
`var` with `private set`: the property can be read publicly but only the ViewModel can write it. The Fragment reads `viewModel.currentUserName` in `PostAnnouncementDialog` without a separate LiveData observation.

### `feedRepository.getAnnouncements(classId).collect { announcements -> }`
`getAnnouncements()` returns a `Flow<List<Announcement>>` backed by a Firestore real-time listener. `collect {}` runs the lambda every time the Flow emits a new value (i.e., whenever Firestore data changes). The sorted+mapped list is set on `_feedItems`.

---

## 🏗️ Class Structure
`@HiltViewModel class FeedViewModel @Inject constructor(feedRepository, authRepository) : ViewModel()`

---

## 📋 Properties

| Property | Type | Modifier | What It Holds | Why It Exists |
|----------|------|----------|--------------|--------------|
| `_feedItems` | `MutableLiveData<List<FeedItem>>` | `private` | Sorted feed items | FeedFragment's RecyclerView data |
| `feedItems` | `LiveData<List<FeedItem>>` | `val` | Read-only | Observed by FeedFragment |
| `_userRole` | `MutableLiveData<String>` | `private` | "teacher"/"student"/"admin" | FAB visibility control |
| `userRole` | `LiveData<String>` | `val` | Read-only | Observed by FeedFragment |
| `currentUserName` | `String` | `var` with `private set` | Current user's display name | Author name in new announcements |

---

## ⚙️ Functions

### `loadUserRole(uid: String)`
Calls `authRepository.getUserById(uid)`, sets `_userRole` and `currentUserName`.

### `loadFeed(classId: String)`
Starts collecting the `FeedRepository.getAnnouncements(classId)` Flow. On each emission: sorts pinned first then by date, maps to `FeedItem.AnnouncementItem`, sets `_feedItems`. Exceptions set an empty list.

### `postAnnouncement(classId, title, body, authorId, authorName)`
Calls `feedRepository.postAnnouncement()` with a new `Announcement` containing `Timestamp.now()`. The real-time listener in `loadFeed` will automatically pick up the new item.

### `togglePin(classId, announcementId, pinned)`
Calls `feedRepository.togglePin()` — Firestore update. The real-time listener fires and re-sorts the feed automatically.

---

## 🔄 Data Flow Diagram
```
FeedFragment → viewModel.loadFeed(classId)
        ↓
FeedRepository.getAnnouncements() → callbackFlow → Firestore snapshot listener
        ↓
On data change: emits List<Announcement>
        ↓
FeedViewModel.collect: sort → map to FeedItem → _feedItems.value = sorted
        ↓
FeedFragment observes feedItems → adapter.submitList(items)
```

---

## 🧩 Dependencies

| Depends On | Why |
|-----------|-----|
| `FeedRepository` | Real-time announcements, post, pin toggle |
| `AuthRepository` | User role and name lookup |

