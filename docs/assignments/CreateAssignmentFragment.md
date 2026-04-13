# CreateAssignmentFragment — Teacher creates a new assignment

---

## 📁 Location
`app/src/main/java/com/syed/classconnect/ui/assignments/CreateAssignmentFragment.kt`

---

## 🎯 What This File Does
CreateAssignmentFragment provides a form for teachers to create assignments with title, description, due date (date picker), total marks, and optional file attachment. On creation, sends notifications to all students in the class.

---

## ⚙️ Key Functions
- Form fields: title, description, due date picker, total marks, file attachment
- `viewModel.createAssignment(classId, assignment)` → AssignmentRepository → Firestore
- Notification batch: creates in-app notifications for all students in the class

