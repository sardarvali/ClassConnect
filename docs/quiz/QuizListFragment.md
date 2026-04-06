# QuizListFragment.kt — Displays class quizzes with role-based access: students take quizzes, teachers manage and view results

---

## 📁 File Location
`app/src/main/java/com/syed/classconnect/ui/quiz/QuizListFragment.kt`

---

## 🎯 What This File Does
`QuizListFragment` is a tab inside `ClassDetailActivity` that shows all quizzes for a class. The experience differs by role: **students** see only published quizzes, tap to take them (or see their result if already attempted), and have no FAB; **teachers/admins** see all quizzes including drafts, have a FAB to create new quizzes, and long-press to publish/unpublish/delete. The fragment observes `QuizViewModel.quizzes` (a real-time Firestore Flow) and shows an empty state when no quizzes exist. Without this fragment, the quiz tab in every class would be blank.

---

## 📦 Every Import — Explained

| Import | From Library | What It Is | Why Used Here |
|--------|-------------|-----------|--------------|
| `android.os.Bundle` | Android SDK | Key-value map | Fragment arguments |
| `android.view.LayoutInflater/View/ViewGroup` | Android SDK | View inflation | `onCreateView` |
| `androidx.fragment.app.Fragment` | AndroidX | Base Fragment | This extends it |
| `androidx.fragment.app.viewModels` | AndroidX | ViewModel delegate | `by viewModels()` |
| `androidx.recyclerview.widget.LinearLayoutManager` | RecyclerView | Vertical list layout | Quiz list |
| `com.google.android.material.dialog.MaterialAlertDialogBuilder` | Material | Styled dialog | Options and confirm dialogs |
| `com.google.android.material.snackbar.Snackbar` | Material | Inline feedback | Delete success/error |
| `com.google.firebase.auth.FirebaseAuth` | Firebase | Current user | UID for role check and attempts |
| `com.syed.classconnect.databinding.FragmentQuizListBinding` | ViewBinding | `fragment_quiz_list.xml` | All views |
| `com.syed.classconnect.util.Constants` | Project | Role strings, extras | `ROLE_TEACHER`, `EXTRA_CLASS_ID` |
| `com.syed.classconnect.util.NetworkResult` | Project | State wrapper | Loading/Success/Error |
| `dagger.hilt.android.AndroidEntryPoint` | Hilt | Enables injection | `@Inject FirebaseAuth` |

---

## 🔑 Every Keyword, Annotation & Concept Used

### `companion object { fun newInstance(classId) }`
Factory method. `ClassTabAdapter` calls `QuizListFragment.newInstance(classId)` to create the tab instance with the class ID pre-loaded in arguments. Avoids Fragment constructors (which Android doesn't allow with parameters).

### Role-gated FAB
```kotlin
viewModel.userRole.observe(viewLifecycleOwner) { role ->
    viewModel.loadQuizzes(classId)  // Load AFTER role is known (draft filtering)
    if (role == ROLE_TEACHER || role == ROLE_ADMIN) {
        binding.fab.show()
    } else {
        binding.fab.hide()
    }
}
```
The FAB to create quizzes is only shown to teachers/admins. Role is loaded first — `loadQuizzes` is called *inside* the role observer to guarantee the correct query (all vs published-only) runs with the right role.

### `handleStudentQuizClick(quiz)`
Calls `viewModel.hasStudentAttempted(classId, quizId, studentId)` with a callback. If already attempted, shows `QuizResultFragment` (their score). If not, starts `QuizAttemptActivity`.

### `showQuizOptionsDialog(quiz)` — teacher long-press
Shows a `MaterialAlertDialog` with options built dynamically: "Publish"/"Unpublish" based on `quiz.isPublished`, plus "Delete". Calls `viewModel.publishQuiz()` or `viewModel.deleteQuiz()`.

### `showDeleteQuizConfirmDialog(quiz)` *(private)*
Confirmation dialog before deletion. Warns that all student attempts will be deleted permanently.

---

## 📋 Properties

| Property | Type | Modifier | What It Holds | Why It Exists |
|----------|------|----------|--------------|--------------|
| `_binding` | `FragmentQuizListBinding?` | `private var` | ViewBinding | Memory-leak-safe pattern |
| `viewModel` | `QuizViewModel` | `private val` | Quiz state | All data |
| `auth` | `FirebaseAuth` | `@Inject lateinit var` | Current user | UID for role and attempts |
| `adapter` | `QuizAdapter` | `private lateinit var` | RecyclerView adapter | Quiz list |
| `classId` | `String` | `private lateinit var` | Current class ID | All queries |

---

## ⚙️ Functions

### `newInstance(classId): QuizListFragment` *(companion)*
Creates fragment with classId in arguments bundle.

### `onViewCreated(view, savedInstanceState)`
1. Reads `classId` from arguments.
2. Loads user role → triggers `loadQuizzes()` and FAB visibility.
3. Sets up `QuizAdapter` with `onClick` and `onLongPress` callbacks.
4. Observes `quizzes` → shows loading/empty/list states.
5. Observes `deleteResult` → shows Snackbar.

### `showQuizOptionsDialog(quiz)` *(private)*
Teacher long-press menu: Publish/Unpublish + Delete.

### `showDeleteQuizConfirmDialog(quiz)` *(private)*
Confirms before calling `viewModel.deleteQuiz()`.

### `handleStudentQuizClick(quiz)` *(private)*
Checks for existing attempt. Routes to result or attempt Activity.

---

## 🔄 Data Flow Diagram
```
ClassDetailActivity creates tab → QuizListFragment.newInstance(classId)
        ↓
onViewCreated: viewModel.loadUserRole(uid)
        ↓
userRole observed → viewModel.loadQuizzes(classId)
        ↓
QuizRepository.getQuizzes() or getQuizzesForStudent()
        ↓
Firestore real-time listener → emit List<Quiz>
        ↓
viewModel._quizzes = Success(list)
        ↓
Fragment: adapter.submitList(list)

Student taps quiz → handleStudentQuizClick()
        ↓
viewModel.hasStudentAttempted() → true → QuizResultFragment
                                → false → QuizAttemptActivity
```
