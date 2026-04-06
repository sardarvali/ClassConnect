# FCM Setup — Firebase Cloud Messaging Configuration

---

## 🎯 What This File Documents
How Firebase Cloud Messaging (FCM) is configured and used in ClassConnect for push notifications.

---

## Architecture

```
App Event (new assignment, chat message, etc.)
    │
    ▼
Repository creates AppNotification in Firestore
    (/notifications/{userId}/items/{notifId})
    │
    ▼
Cloud Function (if deployed) OR client-side sends FCM push
    │
    ▼
Firebase Cloud Messaging service delivers push
    │
    ▼
MyFirebaseMessagingService.onMessageReceived()
    │
    ▼
System notification displayed
```

---

## Token Management

### Token Registration
When the app launches, `MyFirebaseMessagingService.onNewToken()` is called with a unique device token. This token is saved in the user's Firestore document (`fcmToken` field).

### Token Refresh
FCM tokens can change. When they do, `onNewToken()` fires again, updating Firestore.

### Token Usage
To send a push notification to a specific user, query their `fcmToken` from Firestore and use the FCM API (v1) to send to that token.

---

## Notification Channels (Android 8+)

The app creates notification channels programmatically:
```kotlin
val channel = NotificationChannel(
    "classconnect_notifications",
    "ClassConnect",
    NotificationManager.IMPORTANCE_HIGH
)
notificationManager.createNotificationChannel(channel)
```

---

## In-App Notifications vs Push Notifications

| Feature | In-App (Firestore) | Push (FCM) |
|---------|-------------------|-----------|
| Storage | Firestore `/notifications/{uid}/items/` | Not persisted |
| Delivery | Read from database | Delivered by FCM service |
| Offline | Available when online | Queued by FCM for ~28 days |
| History | Persistent, browsable | Gone after dismissal |
| Real-time | Via Firestore listener | Via system notification |

ClassConnect uses BOTH: Firestore for persistent notification history, and FCM for system-level push alerts.

---

## Setup Steps

1. FCM is automatically enabled when you add `firebase-messaging-ktx` dependency
2. `google-services.json` contains the FCM sender ID
3. `MyFirebaseMessagingService` is registered in `AndroidManifest.xml`
4. No server-side setup needed for client-to-device messaging (handled by `onMessageReceived`)
5. For server-to-device: deploy Cloud Functions or use FCM REST API v1

---

## ⚠️ Important Notes
- FCM push notifications received in BACKGROUND are handled automatically by the system (shows notification)
- FCM push notifications received in FOREGROUND require `onMessageReceived()` to manually create a notification
- FCM tokens are device-specific — if a user has multiple devices, each has its own token
- The `fcmToken` field in the user document only stores the LATEST device's token

