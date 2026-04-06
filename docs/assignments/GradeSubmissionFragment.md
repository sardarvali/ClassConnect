# GradeSubmissionFragment — Teacher grades a student's submission

---

## 📁 Location
`app/src/main/java/com/syed/classconnect/ui/assignments/GradeSubmissionFragment.kt`

---

## 🎯 What This File Does
GradeSubmissionFragment allows teachers to view a student's submission, assign a grade (number input), and provide written feedback. Updates the submission status to "graded" in Firestore.

---

## ⚙️ Key Functions
- Displays student's text answer and/or file attachment
- Grade input (0 to totalMarks)
- Feedback text input
- `viewModel.gradeSubmission(classId, assignmentId, studentId, grade, feedback)`
- Updates: grade, feedback, status="graded" in Firestore

