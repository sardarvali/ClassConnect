# AssignmentDetailFragment — View assignment details and submit work

---

## 📁 Location
`app/src/main/java/com/syed/classconnect/ui/assignments/AssignmentDetailFragment.kt`

---

## 🎯 What This File Does
AssignmentDetailFragment shows full assignment details. For students: displays submission form (text answer or file upload) and shows existing submission/grade. For teachers: shows a "View Submissions" button leading to SubmissionListFragment.

---

## ⚙️ Key Functions
- Loads assignment details and existing submission (if student)
- Students: text answer input + file upload + submit button
- Teachers: view submissions button → SubmissionListFragment
- Shows grade and feedback if already graded

