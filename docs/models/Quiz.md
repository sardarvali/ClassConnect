# Quiz — Data class representing a quiz/test

---

## 📁 Location
`app/src/main/java/com/syed/classconnect/data/model/Quiz.kt`

---

## 🎯 What This File Does
This file contains three related data classes: `QuizQuestion` (a single MCQ question), `Quiz` (a collection of questions with metadata), and `QuizAttempt` (a student's attempt at a quiz). Quizzes live at `/classes/{classId}/quizzes/{quizId}`. Attempts are at `/classes/{classId}/quizzes/{quizId}/attempts/{studentId}`.

---

## 📦 Imports — Every Line Explained

| Import | Library / Package | What It Is | Why This File Needs It |
|--------|------------------|-----------|----------------------|
| `com.google.firebase.Timestamp` | Firebase Firestore | Server-synchronized timestamp | Used for `startTime`, `endTime`, `createdAt`, `startedAt`, `submittedAt` |

---

## 📋 QuizQuestion Properties

| Property | Type | What It Stores |
|----------|------|---------------|
| `question` | `String` | The question text |
| `options` | `List<String>` | List of answer choices (typically 4) |
| `correctIndex` | `Int` | Index of the correct answer in `options` (0-based) |
| `marks` | `Int` | Points this question is worth |

## 📋 Quiz Properties

| Property | Type | What It Stores |
|----------|------|---------------|
| `id` | `String` | Firestore document ID |
| `title` | `String` | Quiz title |
| `description` | `String` | Quiz description/instructions |
| `durationMinutes` | `Int` | Time limit in minutes |
| `totalMarks` | `Int` | Sum of all question marks |
| `startTime` | `Timestamp?` | When quiz becomes available (nullable) |
| `endTime` | `Timestamp?` | When quiz closes (nullable) |
| `isPublished` | `Boolean` | Whether students can see this quiz |
| `questions` | `List<QuizQuestion>` | All questions in the quiz |
| `createdAt` | `Timestamp` | When the quiz was created |

## 📋 QuizAttempt Properties

| Property | Type | What It Stores |
|----------|------|---------------|
| `studentId` | `String` | UID of the student (also document ID) |
| `startedAt` | `Timestamp` | When the student started the quiz |
| `submittedAt` | `Timestamp` | When the student submitted |
| `answers` | `Map<String, Int>` | Question index → selected answer index |
| `score` | `Int` | Total points earned |
| `totalMarks` | `Int` | Maximum possible points |
| `studentName` | `String` | Denormalized student name |

---

## 📝 Full Annotated Source Code

```kotlin
package com.syed.classconnect.data.model
// Package: data model layer.

import com.google.firebase.Timestamp
// Timestamp: Firebase's time type for date/time fields.

data class QuizQuestion(
// QuizQuestion: a single multiple-choice question within a quiz.
    val question: String = "",
    // question: The question text displayed to students.
    val options: List<String> = emptyList(),
    // options: List of answer choices (e.g., ["Paris", "London", "Berlin", "Madrid"]).
    val correctIndex: Int = 0,
    // correctIndex: 0-based index of the correct answer in the options list.
    val marks: Int = 1
    // marks: How many points this question is worth.
)

data class Quiz(
// Quiz: a complete quiz with questions, timing, and publishing state.
    val id: String = "",
    // id: Firestore document ID.
    val title: String = "",
    // title: Quiz name shown in the quiz list.
    val description: String = "",
    // description: Instructions or description for students.
    val durationMinutes: Int = 30,
    // durationMinutes: Time limit. QuizAttemptActivity uses this for countdown.
    val totalMarks: Int = 0,
    // totalMarks: Sum of all question marks. Calculated when creating the quiz.
    val startTime: Timestamp? = null,
    // startTime: Optional. When the quiz becomes available. Null = always available.
    val endTime: Timestamp? = null,
    // endTime: Optional. When the quiz closes. Null = no deadline.
    val isPublished: Boolean = false,
    // isPublished: false = draft (only teacher sees), true = visible to students.
    val questions: List<QuizQuestion> = emptyList(),
    // questions: All questions in this quiz. Stored as a nested array in Firestore.
    val createdAt: Timestamp = Timestamp.now()
    // createdAt: When the teacher created this quiz.
)

data class QuizAttempt(
// QuizAttempt: a student's completed attempt at a quiz.
// Stored at: /classes/{classId}/quizzes/{quizId}/attempts/{studentId}
    val studentId: String = "",
    // studentId: UID of the student. Also the document ID.
    val startedAt: Timestamp = Timestamp.now(),
    // startedAt: When the student opened the quiz.
    val submittedAt: Timestamp = Timestamp.now(),
    // submittedAt: When the student submitted their answers.
    val answers: Map<String, Int> = emptyMap(),
    // answers: Maps question index (as string) to selected option index.
    // Example: {"0": 2, "1": 0, "2": 3} means Q0→option2, Q1→option0, Q2→option3.
    val score: Int = 0,
    // score: Total points earned. Calculated by comparing answers to correctIndex.
    val totalMarks: Int = 0,
    // totalMarks: Maximum possible points. Stored for percentage calculation.
    val studentName: String = ""
    // studentName: Denormalized name for display in teacher's results view.
)
```

