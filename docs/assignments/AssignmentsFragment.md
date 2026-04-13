# AssignmentsFragment — Lists assignments for a class

---

## 📁 Location
`app/src/main/java/com/syed/classconnect/ui/assignments/AssignmentsFragment.kt`

---

## 🎯 What This File Does
AssignmentsFragment displays all assignments in a class sorted by due date. Teachers see a FAB to create new assignments. Students see submission status. Tapping an assignment opens AssignmentDetailFragment. Color-codes overdue assignments.

---

## ⚙️ Key Functions
- `onViewCreated()`: Sets up RecyclerView, FAB (teacher only), observes assignments
- `viewModel.loadAssignments(classId)`: Real-time Flow from Firestore
- Assignment cards show: title, due date, total marks, overdue indicator

---

## 🔄 Data Flow
```
ClassDetailActivity tab → AssignmentsFragment
    → viewModel.loadAssignments(classId)
    → AssignmentRepository.getAssignments(classId) → callbackFlow
    → Real-time list → AssignmentsAdapter → RecyclerView
    → Tap → AssignmentDetailFragment(classId, assignmentId)
```

