# SubmissionListFragment — Teacher views all submissions for an assignment

---

## 📁 Location
`app/src/main/java/com/syed/classconnect/ui/assignments/SubmissionListFragment.kt`

---

## 🎯 What This File Does
SubmissionListFragment displays all student submissions for a specific assignment. Shows student name, submission time, status (submitted/graded/late), and grade if assigned. Tapping a submission opens GradeSubmissionFragment.

---

## ⚙️ Key Functions
- `viewModel.loadAllSubmissions(classId, assignmentId)`: Real-time Flow of submissions
- Submission list with status indicators and grade display
- Tap → GradeSubmissionFragment for grading

