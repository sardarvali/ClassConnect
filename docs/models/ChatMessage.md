# ChatMessage — Data class for real-time chat messages

---

## 📁 Location
`app/src/main/java/com/syed/classconnect/data/model/ChatMessage.kt`

---

## 🎯 What This File Does
The `ChatMessage` data class represents a single message in a class chat room. Messages are stored at `/classes/{classId}/chat/{messageId}` in Firestore. The chat system supports text messages, file attachments (images, PDFs), message deletion (soft delete), and emoji reactions.

---

## 📦 Imports — Every Line Explained

| Import | Library / Package | What It Is | Why This File Needs It |
|--------|------------------|-----------|----------------------|
| `com.google.firebase.Timestamp` | Firebase Firestore | Server-synchronized timestamp | Used for the `timestamp` field |

---

## 📋 Properties

| Property | Type | What It Stores |
|----------|------|---------------|
| `id` | `String` | Firestore document ID |
| `senderId` | `String` | UID of the message sender |
| `senderName` | `String` | Display name of sender (denormalized) |
| `senderPhotoUrl` | `String` | Avatar URL of sender (denormalized) |
| `text` | `String` | Message text content |
| `attachmentUrl` | `String` | URL to attached file (empty if no attachment) |
| `attachmentType` | `String` | `"image"` or `"pdf"` (empty if no attachment) |
| `timestamp` | `Timestamp` | When the message was sent |
| `isDeleted` | `Boolean` | Soft delete flag — shows "This message was deleted" |
| `reactions` | `Map<String, String>` | userId → emoji mapping (e.g., `{"uid1": "👍", "uid2": "❤️"}`) |

---

## 📝 Full Annotated Source Code

```kotlin
package com.syed.classconnect.data.model
// Package: data model layer.

import com.google.firebase.Timestamp
// Timestamp: Firebase's time type. Used for message ordering.

data class ChatMessage(
// ChatMessage: a single message in a class chat room.
// Stored at: /classes/{classId}/chat/{messageId}
    val id: String = "",
    // id: Firestore document ID. Set via .copy(id = doc.id).
    val senderId: String = "",
    // senderId: UID of the user who sent this message.
    // Used to determine if current user is the sender (for UI alignment and delete permission).
    val senderName: String = "",
    // senderName: Denormalized display name. Avoids extra Firestore reads.
    val senderPhotoUrl: String = "",
    // senderPhotoUrl: Denormalized avatar URL for the chat bubble.
    val text: String = "",
    // text: The message text content. Replaced with "This message was deleted" on soft delete.
    val attachmentUrl: String = "",
    // attachmentUrl: URL to an image or PDF attachment in Firebase Storage.
    val attachmentType: String = "", // "image" | "pdf"
    // attachmentType: Type of attachment. Empty string if no attachment.
    val timestamp: Timestamp = Timestamp.now(),
    // timestamp: When the message was sent. Used for ordering (ascending).
    val isDeleted: Boolean = false,
    // isDeleted: Soft delete flag. When true, the message text shows "This message was deleted".
    // The original text is overwritten in Firestore for data privacy.
    val reactions: Map<String, String> = emptyMap() // userId → emoji
    // reactions: Maps user UIDs to emoji reactions.
    // Each user can have one reaction per message (updating replaces their previous reaction).
)
```

