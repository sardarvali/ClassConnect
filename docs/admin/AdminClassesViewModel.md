# AdminClassesViewModel — Class management ViewModel

---

## 📁 File Location
`app/src/main/java/com/syed/classconnect/ui/admin/AdminClassesViewModel.kt`

---

`AdminClassesViewModel` is the dedicated ViewModel for the admin classes screen. It loads institution classes, applies the unassigned-only filter, loads teachers for the current institution, and assigns a teacher to a class while also creating the related notification.

This is a separate ViewModel from `AdminViewModel`, which handles dashboard statistics and user approval/role-management flows.

