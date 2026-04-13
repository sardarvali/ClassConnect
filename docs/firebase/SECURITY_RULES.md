# Firestore Security Rules — Complete Reference

---

## 📁 Location
`firestore.rules` (project root)

---

## 🎯 What This File Does
Firestore security rules control who can read, write, update, and delete data. They run on Firebase's servers — even if someone bypasses the app, these rules prevent unauthorized access. Without proper rules, anyone with the Firebase project config could read/modify all data.

---

## Rule Structure

```
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Rules for each collection/path
  }
}
```

---

## Helper Functions

### `isAdmin()`
```
function isAdmin() {
    return request.auth != null &&
        get(/databases/$(database)/documents/users/$(request.auth.uid)).data.role == 'admin';
}
```
Reads the requesting user's document to check if their role is "admin". **Warning**: Each `get()` call counts as one read operation and has a cost.

### `isTeacherOrAdmin()`
Checks if the user is a teacher or admin by reading their user document.

### `isMemberOrTeacher(classId)`
Checks if the user is either:
- A student enrolled in the class (`uid in cls.studentIds`)
- The class teacher (`cls.teacherId == uid`)
- An admin (via `isAdmin()`)

---

## Rules by Collection

### `/users/{userId}`
| Operation | Who Can Do It |
|-----------|--------------|
| Read | Any authenticated user |
| Create | Only if `request.auth.uid == userId` (can only create your own) |
| Update | Own user OR admin |
| Delete | Admin only |

### `/institutions/{instId}`
| Operation | Who Can Do It |
|-----------|--------------|
| Read | Any authenticated user |
| Create | Authenticated user who sets themselves as `adminId` |
| Update/Delete | Only the institution's admin |

### `/classes/{classId}`
| Operation | Who Can Do It |
|-----------|--------------|
| Read | Class members (students + teacher) OR admin |
| Create | Teacher or admin |
| Update | Class teacher OR admin |
| Delete | Admin only |

### `/classes/{classId}/assignments/{assignmentId}`
| Operation | Who Can Do It |
|-----------|--------------|
| Read | Class members/teacher |
| Create/Update/Delete | Teacher or admin |

### `/classes/{classId}/assignments/{assignmentId}/submissions/{studentId}`
| Operation | Who Can Do It |
|-----------|--------------|
| Read | The student themselves OR teacher/admin |
| Create/Update | The student themselves OR teacher/admin |
| Delete | Teacher or admin |

### `/classes/{classId}/chat/{messageId}`
| Operation | Who Can Do It |
|-----------|--------------|
| Read | Class members/teacher |
| Create | Any authenticated user |
| Update/Delete | Message sender OR teacher/admin |

### `/notifications/{userId}/items/{notifId}`
| Operation | Who Can Do It |
|-----------|--------------|
| Read/Write | Only the notification owner (`request.auth.uid == userId`) |

---

## ⚠️ Important Notes
- `get()` calls in rules count toward read quotas — minimize their use
- Rules are evaluated server-side — cannot be bypassed by client code
- Rules do NOT filter query results — they reject queries that COULD return unauthorized data
- `request.resource.data` = the data being written; `resource.data` = the existing data
- Deploy rules with: `firebase deploy --only firestore:rules`

