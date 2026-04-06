# Submission — Student's response to an Assignment

---

## 📁 Location
`app/src/main/java/com/syed/classconnect/data/model/Assignment.kt` (defined in the same file as Assignment)

---

## 🎯 What This File Does
The `Submission` data class is defined alongside `Assignment` in the same file. See [Assignment.md](Assignment.md) for the complete documentation including both classes, all imports, keywords, and full annotated source code.

---

## 📋 Quick Reference

| Property | Type | Description |
|----------|------|-------------|
| `studentId` | `String` | UID of submitting student (also the Firestore document ID) |
| `studentName` | `String` | Denormalized display name |
| `submittedAt` | `Timestamp` | Submission timestamp |
| `fileUrl` | `String` | URL to uploaded file in Firebase Storage |
| `textAnswer` | `String` | Text-based answer content |
| `grade` | `Int` | Teacher-assigned grade (-1 = ungraded) |
| `feedback` | `String` | Teacher's written feedback |
| `status` | `String` | `"submitted"` / `"graded"` / `"late"` |

---

## 🔄 Firestore Path
```
/classes/{classId}/assignments/{assignmentId}/submissions/{studentId}
```

The `studentId` serves as both the document ID and a field, ensuring one submission per student per assignment.

