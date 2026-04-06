# ClassDetailActivity.kt — Tabbed class hub with ViewPager2, gradient header, class code pill, and role-gated Students tab

---

## 📁 File Location
`app/src/main/java/com/syed/classconnect/ui/classes/ClassDetailActivity.kt`

---

## 🎯 What This File Does
`ClassDetailActivity` is the central hub for a single class. It uses `ViewPager2` with a `TabLayout` to host five always-visible tabs (Feed, Assignments, Attendance, Quizzes, Chat) plus a sixth Students tab shown only to teachers and admins. The toolbar shows the class name. A gradient header is rendered using the class's colour from Firestore. A class code "pill" below the toolbar can be tapped to copy the code or long-pressed to share it. Deep-link navigation (e.g., from a push notification) is supported via `EXTRA_TAB_INDEX`. Without this Activity, no class tab content is accessible.

---

## 📦 Every Import — Explained

| Import | From Library | What It Is | Why Used Here |
|--------|-------------|-----------|--------------|
| `android.content.ClipData/ClipboardManager` | Android SDK | Clipboard API | Copy class code |
| `android.content.Context/Intent` | Android SDK | Context + navigation | `start()` companion method |
| `android.graphics.drawable.GradientDrawable` | Android SDK | Gradient drawable | Gradient header background |
| `android.os.Bundle` | Android SDK | State map | `onCreate` param |
| `android.view.View` | Android SDK | View class | Pill visibility |
| `android.widget.Toast` | Android SDK | Short feedback | "Code copied" toast |
| `androidx.appcompat.app.AppCompatActivity` | AndroidX | Base Activity | This extends it |
| `androidx.core.content.ContextCompat` | AndroidX | Safe color loading | `endColor` of gradient |
| `com.google.firebase.auth.FirebaseAuth` | Firebase | Current user UID | Role fallback fetch |
| `com.google.firebase.firestore.FirebaseFirestore` | Firebase | Firestore client | Fetch role if not in intent |
| `com.google.android.material.tabs.TabLayoutMediator` | Material | Tab-ViewPager sync | Attach tab titles |
| `com.syed.classconnect.databinding.ActivityClassDetailBinding` | ViewBinding | `activity_class_detail.xml` | All views |
| `com.syed.classconnect.ui.assignments.AssignmentsFragment` | Project | Assignments tab | Tab fragment |
| `com.syed.classconnect.ui.attendance.AttendanceFragment` | Project | Attendance tab | Tab fragment |
| `com.syed.classconnect.ui.chat.ChatFragment` | Project | Chat tab | Tab fragment |
| `com.syed.classconnect.ui.classes.feed.FeedFragment` | Project | Feed tab | Tab fragment |
| `com.syed.classconnect.ui.quiz.QuizListFragment` | Project | Quiz tab | Tab fragment |
| `com.syed.classconnect.util.Constants` | Project | Extra key constants | Intent extras |
| `com.syed.classconnect.util.toColorInt` | Project | String → Int colour | `classColor.toColorInt()` |
| `dagger.hilt.android.AndroidEntryPoint` | Hilt | DI enabled | Tab fragments need Hilt |
| `kotlinx.coroutines.CoroutineScope/Dispatchers/launch` | Coroutines | Async scope | Firestore role fallback |
| `kotlinx.coroutines.tasks.await` | Coroutines/Firebase | Task → coroutine | `firestore.get().await()` |

---

## 🔑 Every Keyword, Annotation & Concept Used

### Class code pill — tap to copy, long-press to share
```kotlin
binding.classCodePill.setOnClickListener { /* copy to clipboard */ }
binding.classCodePill.setOnLongClickListener {
    startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND)...))
    true
}
```
A small pill under the toolbar showing the 6-character class code. Students use this code to join via `JoinClassBottomSheet`. Long-press opens the system share sheet.

### Gradient header
```kotlin
val gradient = GradientDrawable(
    GradientDrawable.Orientation.TOP_BOTTOM,
    intArrayOf(classColor.toColorInt(), surfaceColor)
)
binding.headerBackground.background = gradient
```
The header fades from the class's accent colour at the top to the surface background at the bottom. `toColorInt()` is a project extension that parses `"#1E6FFF"` to an `Int`.

### Role resolution
The user role is passed via `EXTRA_USER_ROLE` in the intent (from `ClassListFragment`). If absent, it is fetched from Firestore in a coroutine. This fallback handles cases where the Activity is started from a notification deep-link that doesn't include the role extra.

### `setupTabs(classId, userRole)` — role-gated Students tab
Students get 5 tabs (no Students tab). Teachers/admins get 6 tabs. The `StudentsFragment` tab is appended dynamically based on role.

### Deep-link via `EXTRA_TAB_INDEX`
Push notifications can include a tab index. `binding.viewPager.post { viewPager.currentItem = openTab }` runs after `ViewPager2` has laid out, ensuring the correct tab is selected.

### `overridePendingTransition` — slide-up/slide-down animations
`start()` applies a slide-up entrance. `finish()` applies a slide-down exit. Creates a card-expand feel when opening a class.

---

## 📋 Properties

| Property | Type | Modifier | What It Holds | Why It Exists |
|----------|------|----------|--------------|--------------|
| `binding` | `ActivityClassDetailBinding` | `private lateinit var` | ViewBinding | All views |

---

## ⚙️ Functions

### `onCreate(savedInstanceState)`
1. Reads `classId`, `className`, `classColor`, `classCode` from intent.
2. Sets up toolbar (back button, title).
3. Shows class code pill with copy/share handlers.
4. Applies gradient header.
5. Resolves user role from intent or Firestore.
6. Calls `setupTabs(classId, role)`.

### `setupTabs(classId, userRole)` *(private)*
Builds the fragment list, attaches `ClassTabAdapter`, connects `TabLayoutMediator`, handles deep-link tab index.

### `onSupportNavigateUp(): Boolean`
Handles the back arrow in the toolbar. Calls `onBackPressedDispatcher.onBackPressed()`.

### `finish()` *(override)*
Applies slide-down exit animation on close.

### `start(context, classId, className, color, classCode)` *(companion)*
Factory method for starting this Activity with all required extras and slide-up animation.

---

## 🔄 Data Flow Diagram
```
ClassListFragment: user taps a class card
        ↓
ClassDetailActivity.start(context, classId, name, color, code)
        ↓
onCreate: resolves role → setupTabs(classId, role)
        ↓
ClassTabAdapter wraps [Feed, Assignments, Attendance, Quizzes, Chat, (Students)]
        ↓
ViewPager2 + TabLayoutMediator show tabs
        ↓
Each tab's Fragment loads its own data independently via its ViewModel
```
