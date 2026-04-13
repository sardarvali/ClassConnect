# UserManagementViewModel — See AdminViewModel

---

## 📁 File Location
`app/src/main/java/com/syed/classconnect/ui/admin/AdminViewModel.kt`

---

The project uses a single shared `AdminViewModel` for all admin management screens. There is no separate `UserManagementViewModel` file — user management, approval, role changes, and statistics are all handled by `AdminViewModel`.

See **[AdminViewModel.md](AdminViewModel.md)** for complete documentation.

## Functions Used by UserManagementFragment

| Function | Purpose |
|----------|---------|
| `loadAdminStats(uid)` | Starts real-time listener for all institution users |
| `pendingUsers` | `LiveData<NetworkResult<List<User>>>` — drives Pending tab |
| `users` | `LiveData<NetworkResult<List<User>>>` — drives Teachers/Students tabs |
| `approveUser(uid)` | Sets `isApproved = true` for a pending user |
| `rejectUser(uid)` | Marks user as rejected |
| `changeUserRole(uid, newRole, reason)` | Role change + audit log |
| `loadUsersForTab(role)` | Loads teachers or students for active tab |
| `approveResult` | `LiveData<ApproveResult>` — drives approve/reject Snackbar |
