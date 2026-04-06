# NotificationRepository — Data access for in-app notifications

---

## 📁 Location
`app/src/main/java/com/syed/classconnect/data/repository/NotificationRepository.kt`

---

## 🎯 What This File Does
NotificationRepository handles Firestore operations for in-app notifications stored at `/notifications/{userId}/items/{notifId}`. Provides real-time notification list, mark-as-read (single and batch), delete, and add operations.

---

## ⚙️ Key Functions

| Function | Returns | Description |
|----------|---------|-------------|
| `getNotifications(userId)` | `Flow<List<AppNotification>>` | Real-time notifications ordered by createdAt DESC |
| `markAsRead(userId, notificationId)` | `Unit` | Sets isRead=true |
| `markAllAsRead(userId)` | `Unit` | Batch updates all unread → read |
| `deleteNotification(userId, notificationId)` | `Unit` | Deletes notification document |
| `addNotification(userId, notification)` | `Unit` | Creates new notification |
| `getUnreadCount(userId)` | `Flow<Int>` | Real-time count of unread notifications |

---

## ⚠️ Important Notes
- Notifications are per-user: `/notifications/{userId}/items/{notifId}`
- `markAllAsRead()` uses a Firestore batch write for atomicity
- `getNotifications()` maps documents with `.copy(id = it.id)` to preserve document IDs
- `getUnreadCount()` uses a separate listener for efficient badge updates

