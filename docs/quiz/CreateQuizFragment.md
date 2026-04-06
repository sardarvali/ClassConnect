# CreateQuizFragment.kt — BottomSheetDialogFragment for creating a new quiz with manual or AI-generated questions

---

## 📁 File Location
`app/src/main/java/com/syed/classconnect/ui/quiz/CreateQuizFragment.kt`

---

## 🎯 What This File Does
`CreateQuizFragment` is a `BottomSheetDialogFragment` that slides up over the quiz list when a teacher taps the FAB in `QuizListFragment`. It collects: title, description, duration (minutes), and a list of `QuizQuestion` objects added one-by-one via `AddQuestionDialog`. The teacher can save as a **draft** (not visible to students) or **publish** immediately (visible to students). `totalMarks` is automatically computed as the sum of each question's marks. Without this fragment, teachers cannot create any quizzes.

---

## 📦 Every Import — Explained

| Import | From Library | What It Is | Why Used Here |
|--------|-------------|-----------|--------------|
| `android.os.Bundle` | Android SDK | Key-value map | Fragment arguments |
| `android.view.LayoutInflater/View/ViewGroup` | Android SDK | View inflation | `onCreateView` |
| `androidx.fragment.app.viewModels` | AndroidX | ViewModel delegate | `by viewModels()` |
| `com.google.android.material.bottomsheet.BottomSheetDialogFragment` | Material | Slide-up sheet | This fragment type |
| `com.google.firebase.Timestamp` | Firebase | Server timestamp | `Quiz.createdAt` |
| `com.syed.classconnect.data.model.Quiz` | Project | Quiz data class | Built and saved |
| `com.syed.classconnect.data.model.QuizQuestion` | Project | Question data class | Collected in `questions` list |
| `com.syed.classconnect.databinding.FragmentCreateQuizBinding` | ViewBinding | `fragment_create_quiz.xml` | All form views |
| `com.syed.classconnect.util.Constants` | Project | Extra keys | `EXTRA_CLASS_ID` |
| `dagger.hilt.android.AndroidEntryPoint` | Hilt | Enables injection | `by viewModels()` with `@HiltViewModel` |

---

## 🔑 Every Keyword, Annotation & Concept Used

### `BottomSheetDialogFragment`
A Dialog that slides up from the bottom of the screen. Inherits both Fragment and Dialog behaviour. `dismiss()` slides it back down. Shown with `fragment.show(parentFragmentManager, tag)`.

### `questions = mutableListOf<QuizQuestion>()` — local state
The list of questions is held in-memory in the fragment (not in the ViewModel) for simplicity. Each time `AddQuestionDialog` confirms a question, it is appended. The count is shown as "N question(s) added".

### `questions.sumOf { it.marks }` — total marks calculation
Iterates all questions and sums their `marks` field. Result stored as `totalMarks` in the `Quiz` object so the leaderboard can show "X / totalMarks".

### `saveQuiz(published: Boolean)` — dual button
Both "Publish" and "Save Draft" call the same function with different `published` value. This avoids code duplication.

### Guard: `if (title.isEmpty() || questions.isEmpty()) return`
Prevents saving a quiz with no title or no questions. The ViewModel's `createQuiz` would still work, but it would create a useless empty quiz.

---

## 📋 Properties

| Property | Type | Modifier | What It Holds | Why It Exists |
|----------|------|----------|--------------|--------------|
| `_binding` | `FragmentCreateQuizBinding?` | `private var` | ViewBinding | Null on destroy |
| `viewModel` | `QuizViewModel` | `private val` | Shared quiz VM | `createQuiz()` call |
| `classId` | `String` | `private lateinit var` | Current class ID | Quiz creation target |
| `questions` | `MutableList<QuizQuestion>` | `private val` | Questions being built | Accumulated before save |

---

## ⚙️ Functions

### `newInstance(classId): CreateQuizFragment` *(companion)*
Factory: creates fragment with classId in arguments.

### `onViewCreated(view, savedInstanceState)`
1. Reads `classId` from arguments.
2. "Add Question" button → `showAddQuestionDialog()`.
3. "Publish" button → `saveQuiz(published = true)`.
4. "Save Draft" button → `saveQuiz(published = false)`.

### `showAddQuestionDialog()` *(private)*
Creates `AddQuestionDialog` with a callback lambda. On confirm, appends the question and updates the count text.

### `saveQuiz(published: Boolean)` *(private)*
1. Reads form fields (title, description, duration).
2. Guards against empty title or zero questions.
3. Computes `totalMarks`.
4. Builds `Quiz(...)` data class.
5. Calls `viewModel.createQuiz(classId, quiz)`.
6. Calls `dismiss()` to close the bottom sheet.

---

## 🔄 Data Flow Diagram
```
Teacher taps FAB in QuizListFragment
        ↓
CreateQuizFragment.newInstance(classId).show(...)
        ↓
Teacher fills title, description, duration
        ↓
Taps "Add Question" → AddQuestionDialog → question added to questions list
        ↓
Taps "Publish" or "Save Draft"
        ↓
saveQuiz(published) → builds Quiz object
        ↓
viewModel.createQuiz(classId, quiz)
        ↓
QuizRepository → Firestore: classes/{classId}/quizzes/{id}
        ↓
dismiss() → sheet closes → QuizListFragment real-time listener fires → quiz appears in list
```
