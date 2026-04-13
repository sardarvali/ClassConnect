# FcmHelper.kt — Singleton that sends FCM push notifications via the Legacy HTTP API

---

## 📁 File Location
`app/src/main/java/com/syed/classconnect/util/FcmHelper.kt`

---

## 🎯 What This File Does
`FcmHelper` is a `@Singleton` utility class that sends Firebase Cloud Messaging (FCM) push notifications directly from the Android app to a specific device token using the FCM Legacy HTTP API. Repositories (e.g., `AssignmentRepository`, `ChatRepository`) call `sendPush()` whenever an action should trigger a push notification on another user's device. It reads the FCM server key from `BuildConfig.FCM_SERVER_KEY` (set in `local.properties`). Without this class, no push notifications would be delivered when users create assignments, post messages, or take other notification-worthy actions.

> ⚠️ **Architecture Note:** The FCM Legacy HTTP API (used here) is deprecated. The recommended approach is to send pushes from a secure server-side environment (Firebase Cloud Functions) rather than from the Android client — because the server key must never be exposed in an APK. In a production app, this should be migrated to Cloud Functions.

---

## 📦 Every Import — Explained

| Import | From Library | What It Is | Why Used Here |
|--------|-------------|-----------|--------------|
| `com.syed.classconnect.BuildConfig` | Project (generated) | Build-time configuration | `FCM_SERVER_KEY` |
| `kotlinx.coroutines.Dispatchers` | Coroutines | Thread dispatcher | `withContext(Dispatchers.IO)` |
| `kotlinx.coroutines.withContext` | Coroutines | Switch coroutine thread | Run network call on IO thread |
| `org.json.JSONObject` | Android/Java | JSON builder | Build FCM payload |
| `timber.log.Timber` | Timber | Logging | Warn/error on failures |
| `java.net.HttpURLConnection` | Java | HTTP connection | Raw HTTP POST to FCM endpoint |
| `java.net.URL` | Java | URL builder | `URL("https://fcm.googleapis.com/fcm/send")` |
| `javax.inject.Inject` | Javax / Hilt | Constructor injection | `@Inject constructor()` |
| `javax.inject.Singleton` | Javax / Hilt | One instance per app | `@Singleton` |

---

## 🔑 Every Keyword, Annotation & Concept Used

### `withContext(Dispatchers.IO) { }`
All network operations must run off the main thread. `withContext(Dispatchers.IO)` switches the current coroutine to the IO thread pool for the duration of the block, then returns to the original thread. The caller uses `suspend fun sendPush(...)` so they must call it from a coroutine.

### `JSONObject().apply { put(key, value) }`
`apply` configures the object and returns itself. Builds the JSON payload:
```json
{
  "to": "{fcm_token}",
  "priority": "high",
  "notification": { "title": "...", "body": "...", "sound": "default" },
  "data": { "type": "assignment", "referenceId": "abc123" }
}
```
`"priority": "high"` — required for the notification to appear immediately on locked screens (rather than being deferred).

### `URL("...").openConnection() as HttpURLConnection`
Opens a raw HTTP connection. `.apply { ... }` configures method, headers, timeout. `doOutput = true` enables writing a request body.

### `conn.outputStream.use { os -> os.write(payload.toByteArray()) }`
`use { }` is Kotlin's auto-close pattern for `Closeable`. Equivalent to try-with-resources in Java. Ensures the stream is always closed, even if an exception is thrown.

### `conn.responseCode != HTTP_OK`
`HttpURLConnection.HTTP_OK` = 200. Any non-200 response is logged as a warning. Errors are non-fatal — a failed push notification does not break the triggering action.

### `BuildConfig.FCM_SERVER_KEY`
Generated at build time from `local.properties`:
```properties
fcm.server.key=AAAA...your_key_here
```
If blank, `sendPush` returns early with a warning. This prevents crashes in development without a server key configured.

### `if (serverKey.isBlank()) { Timber.w("..."); return@withContext }`
`return@withContext` returns from the `withContext` lambda (not from `sendPush`). This is a labeled return — necessary because a plain `return` inside a lambda is illegal in Kotlin.

---

## 🏗️ Class Structure
`@Singleton class FcmHelper @Inject constructor()` — no constructor dependencies.

---

## ⚙️ Functions

### `sendPush(token, title, body, data): Unit` *(suspend)*
**Purpose:** Send one FCM push notification to a device.
**Parameters:**
- `token`: The recipient's FCM device token (stored in their `User.fcmToken` Firestore field)
- `title`: Notification title (shown in the status bar)
- `body`: Notification body text
- `data`: Optional metadata map (e.g., `"type" to "assignment"`, `"referenceId" to assignmentId`)
**Step by step:**
1. Reads `BuildConfig.FCM_SERVER_KEY`. Returns early if blank.
2. Builds `dataJson` from the `data` map.
3. Builds the FCM JSON payload.
4. Opens `HttpURLConnection` to `https://fcm.googleapis.com/fcm/send`.
5. Sets headers: `Authorization: key={serverKey}`, `Content-Type: application/json`.
6. Writes payload to output stream.
7. Reads `responseCode`. Logs warning if not 200.
8. Disconnects.
9. Any exception: logged with `Timber.e(e, "FCM send error")`.

---

## 🔄 Data Flow Diagram
```
AssignmentRepository.createAssignment(classId, assignment)
        ↓
Gets list of student UIDs from class
        ↓
For each student: authRepository.getUserById(uid) → gets fcmToken
        ↓
fcmHelper.sendPush(token, "New Assignment", "...", data = mapOf(...))
        ↓
FcmHelper builds JSON payload → HTTP POST to fcm.googleapis.com
        ↓
FCM servers deliver to student's device → status bar notification
        ↓
Student taps notification → app opens to AssignmentDetailFragment
```

---

## ⚠️ Important Notes & Gotchas
- **Security**: The FCM server key is embedded in the APK (via `BuildConfig`). Anyone who decompiles the APK can extract it and send push notifications to any device in your FCM project. **In production, this must be moved to Cloud Functions.**
- The `data` map is included in the FCM `"data"` field (not `"notification"`). The `MyFirebaseMessagingService.onMessageReceived()` reads these data extras to determine which screen to navigate to.
- Failures are silently logged — no error is surfaced to the user or ViewModel. A missed push notification is considered non-critical.
- `connectTimeout = 10_000` and `readTimeout = 10_000` (10 seconds each). On slow networks this coroutine can suspend for up to 20 seconds before the `catch` runs.

