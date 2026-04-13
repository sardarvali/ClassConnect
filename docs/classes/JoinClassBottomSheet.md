# JoinClassBottomSheet — Student joins a class by code

---

## 📁 Location
`app/src/main/java/com/syed/classconnect/ui/classes/JoinClassBottomSheet.kt`

---

## 🎯 What This File Does
JoinClassBottomSheet is a BottomSheetDialogFragment where students enter a 6-character class code to join a class. Shows a preview of the class before confirming. Validates the code format and checks if the class exists.

---

## ⚙️ Key Functions
- `onViewCreated()`: Sets up code input, preview section, join button
- Code entry: Validates format → `viewModel.previewClass(code)` → shows class info
- Join button: `viewModel.joinClass(code, uid)` → adds student to class → dismisses

---

## 🔄 Data Flow
```
Student enters code → viewModel.joinClass(code, uid)
    → ClassRepository.joinClass(code, uid)
    → Firestore: query classes where classCode == code.uppercase()
    → Adds studentId to studentIds array
    → Updates user's classIds array
    → Returns ClassRoom → BottomSheet dismisses
```

