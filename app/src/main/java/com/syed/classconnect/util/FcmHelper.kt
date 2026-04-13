package com.syed.classconnect.util

import com.syed.classconnect.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import timber.log.Timber
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Sends FCM push notifications to specific device tokens.
 * Uses the FCM Legacy HTTP API.
 * Called from repositories whenever a notification should appear in the status bar.
 */
@Singleton
class FcmHelper @Inject constructor() {

    /**
     * Send a push notification to a single FCM token.
     * Runs on Dispatchers.IO — call from a coroutine.
     */
    suspend fun sendPush(
        token: String,
        title: String,
        body: String,
        data: Map<String, String> = emptyMap()
    ) = withContext(Dispatchers.IO) {
        try {
            val serverKey = BuildConfig.FCM_SERVER_KEY
            if (serverKey.isBlank()) {
                Timber.w("FCM server key not set — push not sent")
                return@withContext
            }

            val dataJson = JSONObject()
            data.forEach { (k, v) -> dataJson.put(k, v) }

            val payload = JSONObject().apply {
                put("to", token)
                put("priority", "high")
                put("notification", JSONObject().apply {
                    put("title", title)
                    put("body", body)
                    put("sound", "default")
                })
                put("data", dataJson)
            }

            val url = URL("https://fcm.googleapis.com/fcm/send")
            val conn = url.openConnection() as HttpURLConnection
            conn.apply {
                requestMethod = "POST"
                setRequestProperty("Authorization", "key=$serverKey")
                setRequestProperty("Content-Type", "application/json; charset=UTF-8")
                doOutput = true
                connectTimeout = 10_000
                readTimeout = 10_000
            }

            conn.outputStream.use { os ->
                os.write(payload.toString().toByteArray(Charsets.UTF_8))
            }

            val responseCode = conn.responseCode
            if (responseCode != HttpURLConnection.HTTP_OK) {
                Timber.w("FCM push failed: HTTP $responseCode")
            }
            conn.disconnect()
        } catch (e: Exception) {
            Timber.e(e, "FCM send error")
        }
    }
}

