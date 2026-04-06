# StudentsFragment — Displays enrolled students in a class

---

## 📁 Location
`app/src/main/java/com/syed/classconnect/ui/classes/StudentsFragment.kt`

---

## 🎯 What This File Does
StudentsFragment shows a list of all students enrolled in a class. Uses a companion object factory method `newInstance(classId)` to pass the class ID via arguments Bundle. Shows student count and an empty state when no students are enrolled.

---

## ⚙️ Key Functions
- `newInstance(classId)`: Static factory method to create fragment with classId argument
- `onViewCreated()`: Sets up RecyclerView with StudentsAdapter, calls `viewModel.loadStudents(classId)`
- Observes NetworkResult<List<User>> for Loading/Success/Error states

---

## 🔄 Data Flow
```
ClassDetailActivity → StudentsFragment.newInstance(classId)
    → viewModel.loadStudents(classId)
    → StudentsViewModel reads class doc → gets studentIds list
    → Queries /users where uid IN studentIds (chunked by 30 for Firestore limit)
    → Returns sorted list of User objects
    → StudentsAdapter.submitList(students)
```

