# MyFirebaseMessagingService — FCM push notification handler

---

## 📁 Location
`app/src/main/java/com/syed/classconnect/service/MyFirebaseMessagingService.kt`

---

## 🎯 What This File Does
MyFirebaseMessagingService extends FirebaseMessagingService to handle incoming FCM push notifications and token refreshes. When a push notification arrives, it creates an Android system notification with the appropriate channel, icon, and click action. When the FCM token refreshes, it updates the token in Firestore.

---

## ⚙️ Key Functions

### `onNewToken(token: String)`
Called when the FCM token changes (first install, token refresh).
1. Gets current user UID from FirebaseAuth
2. Updates `fcmToken` field in the user's Firestore document
3. Runs on IO dispatcher to avoid blocking

### `onMessageReceived(remoteMessage: RemoteMessage)`
Called when a push notification arrives while the app is in the foreground.
1. Extracts title and body from the notification payload
2. Creates a NotificationChannel (Android 8+)
3. Builds a notification with icon, title, body, and click intent
4. Shows the notification via NotificationManager

---

## ⚠️ Important Notes
- FCM messages received while app is in BACKGROUND are handled by the system tray (not this service)
- `onNewToken()` is called on first app install — saves token for future push targeting
- Notification channel ID must match between creation and notification building
- Click intent opens MainActivity by default

