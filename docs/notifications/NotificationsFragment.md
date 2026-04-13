# NotificationsFragment.kt — In-app notification list with swipe-to-delete and mark-all-read

---

## 📁 File Location
`app/src/main/java/com/syed/classconnect/ui/notifications/NotificationsFragment.kt`

---

## 🎯 What This File Does
`NotificationsFragment` displays the user's in-app notification history in a vertically-scrolling list. Tapping a notification marks it as read (updating the unread badge count in `MainActivity`). Swiping left or right deletes a notification. The toolbar menu has a "Mark all as read" option. An empty state is shown when there are no notifications. Without this fragment, users cannot see past assignment/quiz/chat notification history.

---

## 📦 Every Import — Explained

| Import | From Library | What It Is | Why Used Here |
|--------|-------------|-----------|--------------|
| `android.os.Bundle` | Android SDK | State map | Lifecycle |
| `android.view.Menu/MenuInflater/MenuItem/View/ViewGroup` | Android SDK | Menu + view types | Toolbar menu |
| `androidx.core.view.MenuProvider` | AndroidX | Menu lifecycle API | Add menu to Activity's toolbar |
| `androidx.fragment.app.Fragment` | AndroidX | Base Fragment | This extends it |
| `androidx.fragment.app.viewModels` | AndroidX | ViewModel delegate | `by viewModels()` |
| `androidx.lifecycle.Lifecycle` | AndroidX | Lifecycle state | `addMenuProvider(..., RESUMED)` |
| `androidx.recyclerview.widget.ItemTouchHelper` | RecyclerView | Swipe/drag gestures | Swipe-to-delete |
| `androidx.recyclerview.widget.LinearLayoutManager` | RecyclerView | Vertical list | RecyclerView layout |
| `androidx.recyclerview.widget.RecyclerView` | RecyclerView | RecyclerView class | `ItemTouchHelper` callback |
| `com.google.firebase.auth.FirebaseAuth` | Firebase | Current user UID | Load and mutate this user's notifications |
| `com.syed.classconnect.R` | Project | Resource IDs | Menu inflation |
| `com.syed.classconnect.databinding.FragmentNotificationsBinding` | ViewBinding | Fragment XML | All views |
| `com.syed.classconnect.util.NetworkResult` | Project | State wrapper | Loading/Success/Error |
| `com.syed.classconnect.util.hide/show` | Project extensions | Toggle view visibility | Empty state / list toggle |
| `dagger.hilt.android.AndroidEntryPoint` | Hilt | Enables injection | `@Inject FirebaseAuth` |

---

## 🔑 Every Keyword, Annotation & Concept Used

### `ItemTouchHelper.SimpleCallback(0, LEFT or RIGHT)`
- First parameter `0` = no drag directions (no drag-to-reorder).
- Second parameter `LEFT or RIGHT` = swipe in either direction triggers delete.
- `onSwiped(holder, direction)` — called when swipe completes. Gets the notification at `holder.adapterPosition` and calls `viewModel.deleteNotification(uid, notif.id)`.
- Must be attached with `.attachToRecyclerView(binding.rvNotifications)`.

### `MenuProvider` with `addMenuProvider(..., Lifecycle.State.RESUMED)`
Modern replacement for `onCreateOptionsMenu`. The menu is only active while the fragment is RESUMED. Automatically removed when the fragment is destroyed — no manual cleanup needed.

### Tap-to-mark-as-read
```kotlin
adapter = NotificationsAdapter { notification ->
    viewModel.markAsRead(uid, notification.id)
}
```
The lambda is passed to the adapter constructor. When the user taps any notification row, `markAsRead` is called → Firestore updates `isRead = true` → unread count decreases → `MainActivity` notification badge updates.

---

## 📋 Properties

| Property | Type | Modifier | What It Holds | Why It Exists |
|----------|------|----------|--------------|--------------|
| `_binding` | `FragmentNotificationsBinding?` | `private var` | ViewBinding | Null in onDestroyView |
| `viewModel` | `NotificationsViewModel` | `private val` | Notification state | All data operations |
| `auth` | `FirebaseAuth` | `@Inject lateinit var` | Current user | UID for Firestore queries |
| `adapter` | `NotificationsAdapter` | `private lateinit var` | List adapter | Renders notifications |

---

## ⚙️ Functions

### `onViewCreated(view, savedInstanceState)`
1. Creates `NotificationsAdapter` with tap-to-read callback.
2. Sets up RecyclerView with `LinearLayoutManager`.
3. Attaches `ItemTouchHelper` for swipe-to-delete.
4. Calls `viewModel.loadNotifications(uid)`.
5. Observes `viewModel.notifications` for Loading/Success/Error states.
6. Adds `MenuProvider` for "Mark all read" menu item.

---

## 🔄 Data Flow Diagram
```
Fragment opens → viewModel.loadNotifications(uid)
        ↓
NotificationRepository.getNotificationsFlow(uid) — real-time Firestore
        ↓
_notifications = Success(list) → adapter.submitList(list)

User taps notification → viewModel.markAsRead(uid, notifId)
        ↓
Firestore: isRead = true → unread count updated → MainActivity badge refreshes

User swipes notification → viewModel.deleteNotification(uid, notifId)
        ↓
Firestore: document deleted → real-time listener fires → list updates

User taps "Mark all read" menu → viewModel.markAllAsRead(uid)
        ↓
Firestore batch update: all isRead = true
```
