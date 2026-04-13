# QuizAttemptActivity — Student takes a quiz with countdown timer

---

## 📁 Location
`app/src/main/java/com/syed/classconnect/ui/quiz/QuizAttemptActivity.kt`

---

## 🎯 What This File Does
QuizAttemptActivity is a full-screen Activity for students taking a quiz. Shows one question at a time with navigation, a countdown timer based on quiz duration, and auto-submits when time runs out. Records the attempt with selected answers, score, and timestamps.

---

## ⚙️ Key Functions
- `onCreate()`: Loads quiz, initializes timer, displays first question
- `showQuestion(index)`: Displays question text and radio buttons for options
- `nextQuestion()` / `prevQuestion()`: Navigate between questions
- `submitQuiz()`: Calculates score, creates QuizAttempt, saves to Firestore
- CountDownTimer: Shows remaining time, auto-submits on expiry

---

## 🔄 Data Flow
```
QuizListFragment → Intent(QuizAttemptActivity) with classId, quizId
    → viewModel.loadQuiz(classId, quizId) → displays questions
    → Student selects answers → stored in Map<String, Int>
    → Submit/Timer expires → calculate score
    → viewModel.submitAttempt(QuizAttempt)
    → Navigate to QuizResultFragment with score
```

