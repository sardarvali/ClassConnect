# CreateClassBottomSheet — Teacher creates a new class

---

## 📁 Location
`app/src/main/java/com/syed/classconnect/ui/classes/CreateClassBottomSheet.kt`

---

## 🎯 What This File Does
CreateClassBottomSheet is a BottomSheetDialogFragment that allows teachers to create a new class. Fields: name, subject, description, schedule (day/time pairs), and color selection. On creation, generates a unique 6-character class code. Validates all inputs and shows the generated code on success.

---

## ⚙️ Key Functions
- `onViewCreated()`: Sets up form fields, color picker, schedule day/time inputs, submit button
- Form submission: Validates → creates `ClassRoom` object → `viewModel.createClass()` → shows code on success

---

## 🔄 Data Flow
```
Teacher fills form → taps Create
    → ClassViewModel.createClass(classRoom)
    → ClassRepository.createClass(classRoom)
    → Firestore: creates /classes/{id} with auto-generated classCode
    → Returns Pair(classId, classCode)
    → BottomSheet shows success with class code → dismisses
```

