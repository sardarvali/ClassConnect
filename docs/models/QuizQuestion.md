# QuizQuestion — Data class for a single multiple-choice quiz question (embedded in Quiz)

---

## 📁 File Location
`app/src/main/java/com/syed/classconnect/data/model/Quiz.kt`

> Note: `QuizQuestion` is defined inside `Quiz.kt` alongside `Quiz` and `QuizAttempt`.

---

## 🎯 What This File Does
`QuizQuestion` represents a single multiple-choice question stored as a list within the `Quiz` document in Firestore. It holds the question text, four answer options, the index of the correct answer (0–3), and the marks awarded for a correct answer. When a student submits a quiz, their answers (`Map<Int, Int>` of question index → selected option index) are compared against each `QuizQuestion.correctIndex` to compute the score. Without this data class, quiz questions cannot be serialized to/from Firestore or validated on submission.

---

## 📦 Source Definition

```kotlin
data class QuizQuestion(
    val question: String = "",
    val options: List<String> = emptyList(),   // exactly 4 options: [A, B, C, D]
    val correctIndex: Int = 0,                 // 0=A, 1=B, 2=C, 3=D
    val marks: Int = 1                         // points awarded for a correct answer
)
```

---

## 🔑 Every Field — Explained

| Field | Type | Default | Purpose |
|-------|------|---------|---------|
| `question` | `String` | `""` | The question text shown to the student |
| `options` | `List<String>` | `emptyList()` | Exactly 4 answer choices shown as radio buttons |
| `correctIndex` | `Int` | `0` | Index into `options` that is the correct answer |
| `marks` | `Int` | `1` | Points awarded if the student selects `options[correctIndex]` |

---

## 🔑 Key Concepts

### Embedded in `Quiz` (not a subcollection)
`QuizQuestion` objects are stored as a **Firestore array** inside the `Quiz` document, not as separate documents. This means:
- All questions load with the quiz in a single Firestore read (efficient)
- Maximum quiz size is limited by the 1MB Firestore document limit (~200–300 questions before hitting limits)
- No server-side query by question is possible (only by quiz)

### Scoring with `correctIndex`
```kotlin
// QuizAttemptActivity scores submitted answers:
val score = quiz.questions.mapIndexed { index, question ->
    if (attempt.answers[index] == question.correctIndex) question.marks else 0
}.sum()
```

### `totalMarks = questions.sumOf { it.marks }`
Stored on the `Quiz` document at creation time. Used to display "8 / 10" on the result screen.

### Default `marks = 1`
Every question is worth 1 point by default. `CreateQuizFragment` does not currently expose a per-question marks field — all questions are equal weight. `AddQuestionDialog` could be extended to support custom marks.

---

## 🧩 Related Files

| File | Relationship |
|------|-------------|
| `Quiz.kt` | `QuizQuestion` is defined here as a nested data class |
| `AddQuestionDialog.kt` | Creates `QuizQuestion` instances from teacher input |
| `QuizAttemptActivity.kt` | Displays questions from `quiz.questions` list |
| `QuizRepository.kt` | Serializes/deserializes the entire `questions` array |
| `QuizAttempt.kt` | Stores `answers: Map<Int,Int>` that reference question indices |
