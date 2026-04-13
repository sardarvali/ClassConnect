# Announcement — Data class for class announcements

---

## 📁 Location
`app/src/main/java/com/syed/classconnect/data/model/Feed.kt` (defined alongside Material)

---

## 🎯 What This File Does
The `Announcement` data class represents a pinnable announcement in a class feed. Teachers post announcements to communicate with students. Stored at `/classes/{classId}/announcements/{announcementId}`.

---

## 📦 Imports

| Import | Library / Package | What It Is | Why This File Needs It |
|--------|------------------|-----------|----------------------|
| `com.google.firebase.Timestamp` | Firebase Firestore | Server-synchronized timestamp | `createdAt` field |

---

## 📋 Properties

| Property | Type | What It Stores |
|----------|------|---------------|
| `id` | `String` | Firestore document ID |
| `title` | `String` | Announcement title |
| `body` | `String` | Announcement body text |
| `authorId` | `String` | UID of the teacher who posted it |
| `authorName` | `String` | Teacher's display name (denormalized) |
| `createdAt` | `Timestamp` | When the announcement was posted |
| `isPinned` | `Boolean` | Whether the announcement is pinned to top of feed |

---

## 📝 Full Annotated Source Code

```kotlin
data class Announcement(
    val id: String = "",           // Firestore document ID
    val title: String = "",        // Announcement title
    val body: String = "",         // Full announcement text
    val authorId: String = "",     // UID of the author (teacher)
    val authorName: String = "",   // Denormalized author name
    val createdAt: Timestamp = Timestamp.now(), // When posted
    val isPinned: Boolean = false  // Pinned announcements appear at top
)
```

