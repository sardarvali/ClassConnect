# QuizResultsTeacherFragment — Teacher views all student quiz attempts

---

## 📁 Location
`app/src/main/java/com/syed/classconnect/ui/quiz/QuizResultsFragment.kt`

---

## 🎯 What This File Does
QuizResultsFragment (teacher view) displays all student attempts for a quiz. Shows each student's score, percentage, and attempt time. Includes summary statistics: average score, highest/lowest score, and a chart visualization using MPAndroidChart.

---

## ⚙️ Key Functions
- Real-time attempt list via `viewModel.loadAllAttempts(classId, quizId)`
- Summary stats: average, min, max, pass rate
- Score distribution chart (BarChart via MPAndroidChart)
- QuizAttemptsAdapter for student-by-student results

