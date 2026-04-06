# Firestore Database Schema — Complete Reference

---

## What is a NoSQL Document Database?

Unlike SQL databases with fixed tables and columns, Firestore uses **collections** (like folders) containing **documents** (like files). Each document has fields (key-value pairs) and can contain **subcollections** (nested folders).

| SQL Concept | Firestore Equivalent |
|------------|---------------------|
| Database | Project |
| Table | Collection |
| Row | Document |
| Column | Field |
| Foreign Key | Document ID reference (string) |
| JOIN | Manual client-side join (or denormalization) |

---

## Complete Schema Diagram

```
Firestore Root
├── users/{userId}
│   ├── uid: String
│   ├── name: String
│   ├── email: String
│   ├── photoUrl: String
│   ├── role: "admin" | "teacher" | "student"
│   ├── institutionId: String
│   ├── isApproved: Boolean
│   ├── isRejected: Boolean
│   ├── fcmToken: String
│   ├── classIds: [String]
│   ├── createdAt: Timestamp
│   ├── bio: String
│   ├── accountType: "institution" | "independent"
│   ├── emailVerified: Boolean
│   ├── roleChangedAt: Timestamp?
│   └── roleChangedBy: String
│
├── institutions/{institutionId}
│   ├── id: String
│   ├── name: String
│   ├── adminId: String
│   ├── joinCode: String (6 chars, UPPERCASE)
│   └── createdAt: Timestamp
│
├── classes/{classId}
│   ├── id: String
│   ├── name: String
│   ├── subject: String
│   ├── description: String
│   ├── teacherId: String
│   ├── teacherName: String
│   ├── institutionId: String
│   ├── studentIds: [String]
│   ├── classCode: String (6 chars, UPPERCASE)
│   ├── schedule: Map<String, String>
│   ├── color: String (hex)
│   ├── createdAt: Timestamp
│   │
│   ├── announcements/{announcementId}
│   │   ├── id, title, body, authorId, authorName, createdAt, isPinned
│   │
│   ├── materials/{materialId}
│   │   ├── id, title, description, type, url, uploadedBy, createdAt
│   │
│   ├── assignments/{assignmentId}
│   │   ├── id, title, description, dueDate, totalMarks, createdAt, attachmentUrl
│   │   └── submissions/{studentId}
│   │       ├── studentId, studentName, submittedAt, fileUrl, textAnswer
│   │       ├── grade, feedback, status
│   │
│   ├── quizzes/{quizId}
│   │   ├── id, title, description, durationMinutes, totalMarks
│   │   ├── startTime?, endTime?, isPublished, createdAt
│   │   ├── questions: [{question, options[], correctIndex, marks}]
│   │   └── attempts/{studentId}
│   │       ├── studentId, startedAt, submittedAt, answers, score, totalMarks, studentName
│   │
│   ├── attendance/{date}  (ISO date string as doc ID)
│   │   ├── date, teacherId, qrToken, qrExpiresAt, present[], absent[], sessionCreatedAt
│   │
│   └── chat/{messageId}
│       ├── id, senderId, senderName, senderPhotoUrl, text
│       ├── attachmentUrl, attachmentType, timestamp, isDeleted
│       └── reactions: Map<userId, emoji>
│
├── notifications/{userId}
│   └── items/{notificationId}
│       ├── id, title, body, type, referenceId, isRead, createdAt
│
└── roleChanges/{logId}
    ├── id, changedByAdminId, changedByAdminName
    ├── targetUserId, targetUserName
    ├── fromRole, toRole, reason, changedAt
```

---

## Collection: /users/{userId}

### Document Structure

| Field | Type | Required | Description | Example |
|-------|------|----------|-------------|---------|
| uid | String | ✅ | Firebase Auth UID | `"abc123xyz"` |
| name | String | ✅ | Full display name | `"Arjun Singh"` |
| email | String | ✅ | Email address | `"arjun@email.com"` |
| photoUrl | String | ❌ | Profile photo URL | `"https://..."` |
| role | String | ✅ | User role | `"student"` |
| institutionId | String | ❌ | Institution reference | `"inst_456"` |
| isApproved | Boolean | ✅ | Admin approval status | `true` |
| isRejected | Boolean | ✅ | Rejection status | `false` |
| fcmToken | String | ❌ | Push notification token | `"token_abc"` |
| classIds | Array<String> | ❌ | Enrolled class IDs | `["cls1", "cls2"]` |
| createdAt | Timestamp | ✅ | Account creation time | Server timestamp |
| bio | String | ❌ | User biography | `"Student at..."` |
| accountType | String | ✅ | Registration path | `"institution"` |
| emailVerified | Boolean | ✅ | Email verified | `true` |
| roleChangedAt | Timestamp | ❌ | Last role change time | Timestamp or null |
| roleChangedBy | String | ❌ | Admin who changed role | `"admin_uid"` |

### When Created
Registration (either path)

### When Updated
Login (fcmToken), profile edit (name, bio, photoUrl), role change (role, roleChangedAt, roleChangedBy), email verification (emailVerified, isApproved)

### Queries Used
```
// Get user by UID:
db.collection("users").document(uid).get()

// Get all users in an institution:
db.collection("users").whereEqualTo("institutionId", id).get()

// Get teachers in institution:
db.collection("users").whereEqualTo("institutionId", id).whereEqualTo("role", "teacher").get()

// Real-time user observation:
db.collection("users").document(uid).addSnapshotListener(...)
```

---

## Understanding Subcollections

```
/classes/{classId}/assignments/{assignmentId}
```

Subcollections are collections nested inside a document. Key behaviors:
- **Deleting the parent document does NOT delete subcollections** — must delete manually
- Subcollection queries are scoped to the parent: `firestore.collection("classes").document(classId).collection("assignments")`
- Cannot query across subcollections of different parents (no "all assignments across all classes" query)

This is why `deleteAssignment()` first deletes all submissions (subcollection), then the assignment.

---

## Real-time vs One-time Reads

| Scenario | Approach | Code |
|----------|----------|------|
| Class list (user browsing) | Real-time (`addSnapshotListener`) | `callbackFlow { ... }` |
| User login check | One-time (`get()`) | `doc.get().await()` |
| Chat messages | Real-time | `callbackFlow { ... }` |
| Assignment submission | One-time | `doc.set(data).await()` |
| Admin user list | One-time from SERVER | `get(Source.SERVER).await()` |
| Quiz attempt check | One-time | `doc.get().await()` |
| Notification count | Real-time | `addSnapshotListener` |

Real-time listeners are more expensive (keep connection open) but provide instant updates. Use one-time reads for data that doesn't change often.

