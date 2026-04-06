# ClassListFragment — Displays all classes for the current user

---

## 📁 Location
`app/src/main/java/com/syed/classconnect/ui/classes/ClassListFragment.kt`

---

## 🎯 What This File Does
ClassListFragment shows a list of classes the user belongs to (students see enrolled classes, teachers see their own classes). Features a FAB button that opens either CreateClassBottomSheet (teachers) or JoinClassBottomSheet (students). Tapping a class card navigates to ClassDetailActivity.

---

## ⚙️ Key Functions

### `onViewCreated(view, savedInstanceState)`
1. Sets up RecyclerView with ClassAdapter
2. Determines user role to show appropriate FAB action
3. Observes class list from ClassViewModel (real-time Flow)
4. Shows empty state when no classes
5. Entrance animations for cards

---

## 🔄 Data Flow
```
Fragment.onViewCreated()
    → viewModel.loadClasses(uid, role)
    → ClassViewModel collects from ClassRepository
        ├── getClassesForStudent(uid) [if student]
        └── getClassesForTeacher(uid) [if teacher]
    → LiveData/Flow updates → ClassAdapter.submitList()
    → User taps class → startActivity(ClassDetailActivity) with class ID, name, color
```

---

## 🧩 This File Depends On

| Dependency | Why |
|-----------|-----|
| `ClassViewModel` | Loads class list |
| `ClassAdapter` | Displays class cards |
| `CreateClassBottomSheet` | Teacher creates new class |
| `JoinClassBottomSheet` | Student joins class by code |
| `ClassDetailActivity` | Navigation target on class tap |

