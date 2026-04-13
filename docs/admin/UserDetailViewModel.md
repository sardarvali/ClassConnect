# UserDetailViewModel — Admin user detail management

---

## 📁 Location
`app/src/main/java/com/syed/classconnect/ui/admin/UserDetailViewModel.kt`

---

## 🎯 What This File Does
UserDetailViewModel manages state for the user detail admin screen. Loads a single user's data, their classes, and provides role change, approval, rejection, and deletion operations with audit logging.

---

## 📋 Properties

| Property | Type | What It Stores |
|----------|------|---------------|
| `user` | `LiveData<User?>` | Target user data |
| `userClasses` | `LiveData<List<ClassRoom>>` | Classes the user belongs to |
| `roleChangeResult` | `LiveData<NetworkResult<Unit>>` | Role change operation result |
| `deleteResult` | `LiveData<NetworkResult<Unit>>` | Delete operation result |

---

## ⚙️ Key Functions
- `loadUser(uid)`: Fetches user from Firestore
- `loadUserClasses(uid, role)`: Queries classes for this user
- `changeRole(uid, newRole, reason, adminUid, adminName)`: Updates role + logs change
- `approveUser(uid, approved)`: Toggles approval
- `deleteUser(uid)`: Deletes user document

