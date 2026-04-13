# AssignTeacherBottomSheet — Admin assigns a teacher to a class

---

## 📁 Location
`app/src/main/java/com/syed/classconnect/ui/admin/AssignTeacherBottomSheet.kt`

---

## 🎯 What This File Does
AssignTeacherBottomSheet is a BottomSheetDialogFragment that lets admins assign or change the teacher for a class. Shows a list of available teachers in the institution. Selecting a teacher updates the class document's `teacherId` and `teacherName` fields.

---

## ⚙️ Key Functions
- Loads teachers: queries users where `role="teacher"` and `institutionId` matches
- Displays teacher list in RecyclerView
- Tap teacher → updates class document → dismisses

