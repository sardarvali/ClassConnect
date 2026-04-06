# AdminDashboardFragment — Admin's main dashboard screen

---

## 📁 Location
`app/src/main/java/com/syed/classconnect/ui/admin/AdminDashboardFragment.kt`

---

## 🎯 What This File Does
AdminDashboardFragment is the first screen admins see after login. It shows institution statistics (total users, pending approvals, total classes), quick action cards for user management and class management, and pending approval requests. The admin can navigate to UserManagementFragment, AdminClassesFragment, or approve/reject users directly.

---

## ⚙️ Key Functions

### `onViewCreated(view, savedInstanceState)`
1. Displays admin greeting and institution name
2. Shows stat cards with animated counters (total users, pending, classes)
3. Sets up pending approvals RecyclerView
4. Quick action cards: Manage Users, Manage Classes, Role History
5. Observes AdminViewModel for user lists and stats

---

## 🔄 Data Flow
```
Fragment.onViewCreated()
    → viewModel.loadDashboardData(institutionId)
    → AdminViewModel queries:
        ├── AuthRepository.getUsersForInstitutionOnce() → user stats
        └── ClassRepository.getAllClasses() → class count
    → LiveData updates → stat cards animate → pending list updates
```

---

## 🧩 This File Depends On

| Dependency | Why |
|-----------|-----|
| `AdminViewModel` | Loads institution data and user lists |
| `UserManagementAdapter` | Displays pending approval list |

