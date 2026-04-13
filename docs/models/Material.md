# Material — Data class for class materials/resources

---

## 📁 Location
`app/src/main/java/com/syed/classconnect/data/model/Feed.kt` (defined alongside Announcement)

---

## 🎯 What This File Does
The `Material` data class represents a learning resource shared by a teacher — PDFs, links, videos, or images. Stored at `/classes/{classId}/materials/{materialId}`.

---

## 📋 Properties

| Property | Type | What It Stores |
|----------|------|---------------|
| `id` | `String` | Firestore document ID |
| `title` | `String` | Material title |
| `description` | `String` | Description of the resource |
| `type` | `String` | `"pdf"` / `"link"` / `"video"` / `"image"` |
| `url` | `String` | URL to the resource (Firebase Storage or external link) |
| `uploadedBy` | `String` | UID of the uploader |
| `createdAt` | `Timestamp` | When the material was shared |

---

## 📝 Full Annotated Source Code

```kotlin
data class Material(
    val id: String = "",           // Firestore document ID
    val title: String = "",        // Material title
    val description: String = "",  // Description
    val type: String = "link",     // "pdf" | "link" | "video" | "image"
    val url: String = "",          // URL to the resource
    val uploadedBy: String = "",   // UID of uploader
    val createdAt: Timestamp = Timestamp.now() // When shared
)
```

