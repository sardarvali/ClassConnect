# ClassRepository — Data access for classes

---

## 📁 Location
`app/src/main/java/com/syed/classconnect/data/repository/ClassRepository.kt`

---

## 🎯 What This File Does
ClassRepository handles all class-related Firestore operations: querying classes for students/teachers/institutions, creating classes, joining classes by code, and fetching users for an institution. Returns `Flow<List<ClassRoom>>` for real-time lists and `Result<T>` for one-shot operations.

---

## ⚙️ Key Functions

| Function | Returns | Description |
|----------|---------|-------------|
| `getClassesForStudent(userId)` | `Flow<List<ClassRoom>>` | Real-time classes where studentIds contains userId |
| `getClassesForTeacher(teacherId)` | `Flow<List<ClassRoom>>` | Real-time classes where teacherId matches |
| `getAllClasses(institutionId)` | `Flow<List<ClassRoom>>` | Real-time classes for an institution (admin) |
| `getClassById(classId)` | `ClassRoom?` | One-shot fetch of a single class |
| `createClass(classRoom)` | `Result<Pair<String, String>>` | Creates class, returns (classId, classCode) |
| `joinClass(classCode, studentId)` | `Result<ClassRoom>` | Joins class by code, updates studentIds and user's classIds |
| `getClassPreviewByCode(code)` | `ClassRoom?` | Preview class info before joining |
| `getUsersForInstitution(institutionId, role?)` | `Flow<List<User>>` | Real-time users in institution (optional role filter) |

---

## ⚠️ Important Notes
- All `callbackFlow` functions include `awaitClose { sub.remove() }` to prevent listener leaks
- `joinClass()` updates BOTH the class's `studentIds` AND the user's `classIds` (two Firestore writes)
- Class codes are always compared in UPPERCASE
- `whereArrayContains` is used for student queries (single field inequality)

