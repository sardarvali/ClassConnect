# Notification (AppNotification) — Data class for in-app notifications

---

## 📁 Location
`app/src/main/java/com/syed/classconnect/data/model/AppNotification.kt`

---

## 🎯 What This File Does
The `AppNotification` data class represents an in-app notification. These are separate from push notifications (FCM) — they persist in Firestore so users can view their notification history. Stored at `/notifications/{userId}/items/{notificationId}`. The class is named `AppNotification` to avoid collision with Android's `android.app.Notification` class.

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
| `title` | `String` | Notification title (e.g., "New Assignment in Math") |
| `body` | `String` | Notification body text |
| `type` | `String` | `"assignment"` / `"quiz"` / `"chat"` / `"attendance"` / `"announcement"` |
| `referenceId` | `String` | ID of the related entity (classId, assignmentId, etc.) |
| `isRead` | `Boolean` | Whether the user has read this notification |
| `createdAt` | `Timestamp` | When the notification was created |

---

## 📝 Full Annotated Source Code

```kotlin
package com.syed.classconnect.data.model
// Package: data model layer.

import com.google.firebase.Timestamp
// Timestamp: Firebase's time type.

data class AppNotification(
// Named AppNotification to avoid collision with android.app.Notification.
    val id: String = "",           // Firestore document ID
    val title: String = "",        // Notification title
    val body: String = "",         // Notification body/message
    val type: String = "",         // Category: "assignment" | "quiz" | "chat" | "attendance" | "announcement"
    val referenceId: String = "",  // ID of the related entity for deep linking
    val isRead: Boolean = false,   // Whether user has seen this notification
    val createdAt: Timestamp = Timestamp.now() // When created (server timestamp preferred)
)
```

