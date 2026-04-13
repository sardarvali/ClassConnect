# QuizViewModel.kt — Shared ViewModel for all quiz operations: list, create, attempt, results

---

## 📁 File Location
`app/src/main/java/com/syed/classconnect/ui/quiz/QuizViewModel.kt`

---

## 🎯 What This File Does
`QuizViewModel` is a single ViewModel shared across all quiz-related screens: `QuizListFragment` (list quizzes), `CreateQuizFragment` (create new quiz), `QuizAttemptActivity` (take quiz with timer), `QuizResultFragment` (student results), and `QuizResultsFragment` (teacher view of all results). It manages: user role loading (teachers see all quizzes including drafts; students see published only), real-time quiz lists via Firestore Flow, individual quiz detail, quiz creation, attempt submission, and loading all attempts for teacher review. Without this ViewModel, no quiz-related screen has any data.

---

## 📋 Properties

| Property | Type | What It Holds |
|----------|------|--------------|
| `_quizzes` | `MutableLiveData<NetworkResult<List<Quiz>>>` | All quizzes for a class (real-time) |
| `quizzes` | `LiveData<NetworkResult<List<Quiz>>>` | Read-only — observed by QuizListFragment |
| `_quizDetail` | `MutableLiveData<Quiz?>` | Single quiz for detail/attempt views |
| `quizDetail` | `LiveData<Quiz?>` | Read-only — used by QuizAttemptActivity |
| `_attempt` | `MutableLiveData<QuizAttempt?>` | Current student's existing attempt |
| `attempt` | `LiveData<QuizAttempt?>` | Read-only — prevents re-taking a quiz |
| `_submitResult` | `MutableLiveData<NetworkResult<Unit>>` | Result of submitting an attempt |
| `submitResult` | `LiveData<NetworkResult<Unit>>` | Read-only — navigates to result screen on success |
| `_userRole` | `MutableLiveData<String>` | "teacher"/"student"/"admin" |
| `userRole` | `LiveData<String>` | Controls FAB visibility, what quizzes are shown |
| `_allAttempts` | `MutableLiveData<NetworkResult<List<QuizAttempt>>>` | All students' attempts (teacher view) |
| `allAttempts` | `LiveData<NetworkResult<List<QuizAttempt>>>` | Powers QuizResultsFragment |

---

## ⚙️ Functions

### `loadUserRole(uid)`
Gets user's role from Firestore. Sets `_userRole`. Called first — all other loads may depend on role.

### `loadQuizzes(classId)`
Starts collecting `quizRepository.getQuizzes(classId)` or `getQuizzesForStudent(classId)` based on role:
- Teacher/Admin: sees ALL quizzes (including drafts)
- Student: sees only `isPublished = true`
Updates `_quizzes` on each Firestore snapshot.

### `loadQuizDetail(classId, quizId)`
Fetches a single quiz document. Used by `QuizAttemptActivity` to get questions.

### `loadAttempt(classId, quizId, studentId)`
Checks if the student already has a completed attempt. If so, `QuizAttemptActivity` navigates directly to results.

### `submitAttempt(classId, quizId, attempt)`
Writes the student's `QuizAttempt` to Firestore. Sets `_submitResult` to `Loading` then `Success`/`Error`.

### `createQuiz(classId, quiz)`
Calls `quizRepository.createQuiz()`. No result observable — creation errors are silently dropped (the real-time listener will not show the quiz if creation fails).

### `loadAllAttempts(classId, quizId)`
Loads all student attempts for a quiz. Powers the teacher's `QuizResultsFragment` table.

---

## 🔄 Data Flow Diagram — Student Taking a Quiz
```
QuizListFragment: student taps a quiz
        ↓
Navigates to QuizAttemptActivity with classId + quizId
        ↓
viewModel.loadUserRole(uid) + viewModel.loadQuizDetail(classId, quizId)
        ↓
viewModel.loadAttempt(classId, quizId, studentId)
  ├── attempt != null → already taken → navigate to QuizResultFragment
  └── attempt == null → show questions
        ↓
Student answers questions + timer runs out (or taps Submit)
        ↓
viewModel.submitAttempt(classId, quizId, QuizAttempt(...))
        ↓
quizRepository.submitAttempt() → Firestore write
        ↓
_submitResult = Success → QuizAttemptActivity navigates to QuizResultFragment
```

---

## 🧩 Dependencies

| Depends On | Why |
|-----------|-----|
| `QuizRepository` | All Firestore quiz operations |
| `AuthRepository` | User role lookup |

---

## ⚠️ Important Notes & Gotchas
- `loadQuizzes()` uses `collect {}` inside a `launch`. The Flow emits every time Firestore data changes — this is a real-time listener, not a one-shot load.
- Role must be loaded before `loadQuizzes()` — otherwise the wrong query runs (student sees all, teacher sees filtered). The fragments call `loadUserRole()` first and wait for `userRole.observe()` before calling `loadQuizzes()`.
- `createQuiz()` has no error result LiveData. If creation fails, the quiz simply won't appear in the list — no error message is shown.

### Referenced By
| File | How it uses this ViewModel |
|------|--------------------------|
| `QuizListFragment` | `by viewModels()` — list + role |
| `CreateQuizFragment` | `by viewModels()` — createQuiz |
| `QuizAttemptActivity` | `by viewModels()` — detail + attempt + submit |
| `QuizResultFragment` | `by viewModels()` — loads attempt result |
| `QuizResultsFragment` | `by viewModels()` — allAttempts for teacher |


