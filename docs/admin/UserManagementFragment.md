# UserManagementFragment — Admin manages institution users

---

## 📁 Location
`app/src/main/java/com/syed/classconnect/ui/admin/UserManagementFragment.kt`

---

## 🎯 What This File Does
UserManagementFragment displays all users in the admin's institution with filtering by role (all/students/teachers) and search. Shows user approval status with approve/reject quick actions. Tapping a user navigates to UserDetailFragment for full management.

---

## ⚙️ Key Functions
- RecyclerView with UserManagementAdapter
- Filter chips: All, Students, Teachers, Pending
- Search by name or email
- Quick approve/reject actions via swipe or button
- `viewModel.loadUsers(institutionId)` → real-time user list

