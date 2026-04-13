# Assignment & Submission — Data classes for assignments and student submissions

---

## 📁 Location
`app/src/main/java/com/syed/classconnect/data/model/Assignment.kt`

---

## 🎯 What This File Does
This file contains two data classes: `Assignment` (a task created by a teacher) and `Submission` (a student's response to an assignment). Assignments live as subcollections under classes: `/classes/{classId}/assignments/{assignmentId}`. Submissions are nested deeper: `/classes/{classId}/assignments/{assignmentId}/submissions/{studentId}`.

---

## 📦 Imports — Every Line Explained

| Import | Library / Package | What It Is | Why This File Needs It |
|--------|------------------|-----------|----------------------|
| `com.google.firebase.Timestamp` | Firebase Firestore | Server-synchronized timestamp | Used for `dueDate`, `createdAt`, `submittedAt` fields |

---

## 📋 Assignment Properties

| Property | Type | What It Stores | Example |
|----------|------|---------------|---------|
| `id` | `String` | Firestore document ID | `"assign_001"` |
| `title` | `String` | Assignment title | `"Chapter 5 Exercises"` |
| `description` | `String` | Detailed instructions | `"Complete problems 1-20"` |
| `dueDate` | `Timestamp` | Submission deadline | Server timestamp |
| `totalMarks` | `Int` | Maximum possible grade | `100` |
| `createdAt` | `Timestamp` | When assignment was created | Server timestamp |
| `attachmentUrl` | `String` | URL to attached file in Storage | `"https://firebasestorage..."` |

## 📋 Submission Properties

| Property | Type | What It Stores | Example |
|----------|------|---------------|---------|
| `studentId` | `String` | UID of the submitting student | `"student_123"` |
| `studentName` | `String` | Display name (denormalized) | `"Arjun Singh"` |
| `submittedAt` | `Timestamp` | When submitted | Server timestamp |
| `fileUrl` | `String` | URL to submitted file | `"https://firebasestorage..."` |
| `textAnswer` | `String` | Text-based answer | `"The answer is 42"` |
| `grade` | `Int` | Grade given by teacher (-1 = ungraded) | `85` |
| `feedback` | `String` | Teacher's feedback | `"Good work!"` |
| `status` | `String` | `"submitted"` / `"graded"` / `"late"` | `"graded"` |

---

## 📝 Full Annotated Source Code

```kotlin
package com.syed.classconnect.data.model
// Package: data model layer.

import com.google.firebase.Timestamp
// Timestamp: Firebase time type for date fields.

data class Assignment(
// Assignment: a task assigned by a teacher to students in a class.
    val id: String = "",
    // id: Firestore document ID. Set via .copy(id = doc.id).
    val title: String = "",
    // title: Short name of the assignment.
    val description: String = "",
    // description: Full instructions for the assignment.
    val dueDate: Timestamp = Timestamp.now(),
    // dueDate: Deadline. Used by DateUtils.isOverdue() and daysUntil().
    val totalMarks: Int = 100,
    // totalMarks: Maximum grade. Used to calculate percentage in grading.
    val createdAt: Timestamp = Timestamp.now(),
    // createdAt: When the teacher created this assignment.
    val attachmentUrl: String = ""
    // attachmentUrl: URL to a file attachment (PDF, image) in Firebase Storage.
    // Empty string if no attachment.
)

data class Submission(
// Submission: a student's response to an assignment.
// Stored at: /classes/{classId}/assignments/{assignmentId}/submissions/{studentId}
    val studentId: String = "",
    // studentId: UID of the student. Also used as the document ID.
    val studentName: String = "",
    // studentName: Denormalized name for display in submission list.
    val submittedAt: Timestamp = Timestamp.now(),
    // submittedAt: When the student submitted their work.
    val fileUrl: String = "",
    // fileUrl: URL to the submitted file in Firebase Storage.
    val textAnswer: String = "",
    // textAnswer: Text-based answer (alternative to file upload).
    val grade: Int = -1,
    // grade: Score given by teacher. -1 means "not yet graded".
    val feedback: String = "",
    // feedback: Teacher's written feedback on the submission.
    val status: String = "submitted" // "submitted" | "graded" | "late"
    // status: Current state of the submission.
    // "submitted": student has submitted, teacher hasn't graded.
    // "graded": teacher has assigned a grade.
    // "late": submitted after the due date.
)
```

