# QuizAttempt — A student's completed attempt at a Quiz

---

## 📁 Location
`app/src/main/java/com/syed/classconnect/data/model/Quiz.kt` (defined in the same file as Quiz)

---

## 🎯 What This File Does
`QuizAttempt` is defined alongside `Quiz` and `QuizQuestion` in Quiz.kt. See [Quiz.md](Quiz.md) for the complete documentation including all three classes, imports, keywords, and full annotated source code.

---

## 📋 Quick Reference

| Property | Type | Description |
|----------|------|-------------|
| `studentId` | `String` | UID of the student (also Firestore document ID) |
| `startedAt` | `Timestamp` | When the student started the quiz |
| `submittedAt` | `Timestamp` | When the student submitted answers |
| `answers` | `Map<String, Int>` | Question index (string) → selected option index |
| `score` | `Int` | Total points earned |
| `totalMarks` | `Int` | Maximum possible points |
| `studentName` | `String` | Denormalized student name for teacher's results view |

---

## 🔄 Firestore Path
```
/classes/{classId}/quizzes/{quizId}/attempts/{studentId}
```

