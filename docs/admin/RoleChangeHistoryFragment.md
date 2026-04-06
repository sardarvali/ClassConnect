# RoleChangeHistoryFragment — Admin views role change audit trail

---

## 📁 Location
`app/src/main/java/com/syed/classconnect/ui/admin/RoleChangeHistoryFragment.kt`

---

## 🎯 What This File Does
RoleChangeHistoryFragment displays the audit trail of all role changes in the institution. Shows who changed whose role, from what to what, when, and why. Uses RoleChangeLogAdapter and RoleChangeHistoryViewModel.

---

## ⚙️ Key Functions
- RecyclerView with RoleChangeLogAdapter
- `viewModel.loadHistory(institutionId)`: Queries role change log collection
- Each entry shows: admin name, target user, from→to role, reason, timestamp

