# ClassListViewModel (ClassViewModel) — Class list and management state

---

## 📁 Location
`app/src/main/java/com/syed/classconnect/ui/classes/ClassViewModel.kt`

---

## 🎯 What This File Does
ClassViewModel manages class list state, class creation, and class joining operations. Used by ClassListFragment, CreateClassBottomSheet, and JoinClassBottomSheet. Collects real-time class data from ClassRepository via Flows.

---

## 📋 Properties

| Property | Type | What It Stores |
|----------|------|---------------|
| `classes` | `LiveData<List<ClassRoom>>` | Real-time list of user's classes |
| `createResult` | `LiveData<NetworkResult<Pair<String, String>>>` | Create class result (classId, classCode) |
| `joinResult` | `LiveData<NetworkResult<ClassRoom>>` | Join class result |
| `classPreview` | `LiveData<ClassRoom?>` | Preview of class being joined |
| `userRole` | `LiveData<String>` | Current user's role |

---

## ⚙️ Key Functions
- `loadClasses(uid, role)`: Collects from student or teacher class Flow
- `createClass(classRoom)`: Calls ClassRepository.createClass()
- `joinClass(code, uid)`: Calls ClassRepository.joinClass()
- `loadClassPreview(code)`: Fetches class info for join preview

