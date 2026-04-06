# QuizRepository — Data access for quizzes and attempts

---

## 📁 Location
`app/src/main/java/com/syed/classconnect/data/repository/QuizRepository.kt`

---

## 🎯 What This File Does
QuizRepository handles all Firestore operations for quizzes and quiz attempts. Provides real-time quiz lists (all for teachers, published-only for students), CRUD operations, attempt submission/retrieval, and quiz publishing control.

---

## ⚙️ Key Functions

| Function | Returns | Description |
|----------|---------|-------------|
| `getQuizzes(classId)` | `Flow<List<Quiz>>` | All quizzes (teacher view) |
| `getQuizzesForStudent(classId)` | `Flow<List<Quiz>>` | Published quizzes only |
| `createQuiz(classId, quiz)` | `Result<String>` | Creates quiz, returns quizId |
| `getQuizById(classId, quizId)` | `Quiz?` | One-shot fetch |
| `submitAttempt(classId, quizId, attempt)` | `Result<Unit>` | Saves student attempt |
| `getAttempt(classId, quizId, studentId)` | `QuizAttempt?` | Get student's existing attempt |
| `getAllAttempts(classId, quizId)` | `Flow<List<QuizAttempt>>` | Real-time all attempts |
| `publishQuiz(classId, quizId, published)` | `Unit` | Toggle isPublished |
| `deleteQuiz(classId, quizId)` | `Result<Unit>` | Deletes quiz + all attempts |

---

## ⚠️ Important Notes
- Student quiz queries filter by `isPublished=true` — draft quizzes are invisible to students
- `deleteQuiz()` first deletes all attempt documents (subcollection), then the quiz document
- Attempt document ID is the `studentId` — one attempt per student per quiz
- `getAllAttempts()` uses real-time listener so teacher sees new attempts as they come in

