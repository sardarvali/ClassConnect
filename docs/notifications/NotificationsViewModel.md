# NotificationsViewModel — Notification state management

---

## 📁 Location
`app/src/main/java/com/syed/classconnect/ui/notifications/NotificationsViewModel.kt`

---

## 🎯 What This File Does
NotificationsViewModel manages the notification list state. Collects real-time notifications from NotificationRepository and provides mark-as-read and delete operations.

---

## ⚙️ Key Functions
- `loadNotifications(userId)`: Collects real-time Flow from NotificationRepository
- `markAsRead(userId, notificationId)`: Sets isRead=true
- `markAllAsRead(userId)`: Batch updates all unread to read
- `deleteNotification(userId, notificationId)`: Deletes from Firestore

