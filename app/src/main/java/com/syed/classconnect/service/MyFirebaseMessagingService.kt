package com.syed.classconnect.service

import android.Manifest
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.syed.classconnect.R
import com.syed.classconnect.data.repository.AuthRepository
import com.syed.classconnect.ui.classes.ClassDetailActivity
import com.syed.classconnect.ui.main.MainActivity
import com.syed.classconnect.util.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MyFirebaseMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var authRepository: AuthRepository

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        val uid = authRepository.currentUser?.uid ?: return
        CoroutineScope(Dispatchers.IO).launch {
            authRepository.updateFcmToken(uid, token)
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val title = message.notification?.title ?: message.data["title"] ?: "ClassConnect"
        val body = message.notification?.body ?: message.data["body"] ?: ""
        val type = message.data["type"] ?: "general"
        val referenceId = message.data["referenceId"] ?: ""
        showNotification(title, body, type, referenceId)
        saveNotificationToFirestore(title, body, type, referenceId)
    }

    private fun showNotification(title: String, body: String, type: String, referenceId: String) {
        val channelId = when (type) {
            "assignment" -> Constants.CHANNEL_ASSIGNMENTS
            "chat" -> Constants.CHANNEL_CHAT
            "announcement" -> Constants.CHANNEL_ANNOUNCEMENTS
            "attendance" -> Constants.CHANNEL_ATTENDANCE
            "grade" -> Constants.CHANNEL_GRADES
            else -> Constants.CHANNEL_ANNOUNCEMENTS
        }

        // Deep-link: route to correct screen based on notification type
        val intent = when (type) {
            "chat" -> Intent(this, ClassDetailActivity::class.java).apply {
                putExtra(Constants.EXTRA_CLASS_ID, referenceId)
                putExtra(Constants.EXTRA_TAB_INDEX, 4) // Chat tab
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }

            "assignment", "grade" -> Intent(this, ClassDetailActivity::class.java).apply {
                putExtra(Constants.EXTRA_CLASS_ID, referenceId)
                putExtra(Constants.EXTRA_TAB_INDEX, 1) // Assignments tab
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }

            "quiz" -> Intent(this, ClassDetailActivity::class.java).apply {
                putExtra(Constants.EXTRA_CLASS_ID, referenceId)
                putExtra(Constants.EXTRA_TAB_INDEX, 3) // Quiz tab
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }

            "attendance" -> Intent(this, ClassDetailActivity::class.java).apply {
                putExtra(Constants.EXTRA_CLASS_ID, referenceId)
                putExtra(Constants.EXTRA_TAB_INDEX, 2) // Attendance tab
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }

            else -> Intent(this, MainActivity::class.java).apply {
                putExtra("openNotifications", true)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
        }
        val pendingIntent = PendingIntent.getActivity(
            this, System.currentTimeMillis().toInt(), intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notifications)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED
            ) {
                NotificationManagerCompat.from(this)
                    .notify(System.currentTimeMillis().toInt(), notification)
            }
        } else {
            val nm = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            nm.notify(System.currentTimeMillis().toInt(), notification)
        }
    }

    private fun saveNotificationToFirestore(
        title: String,
        body: String,
        type: String,
        referenceId: String
    ) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val notifData = hashMapOf(
            "title" to title,
            "body" to body,
            "type" to type,
            "referenceId" to referenceId,
            "isRead" to false,
            "createdAt" to FieldValue.serverTimestamp()
        )
        FirebaseFirestore.getInstance()
            .collection(Constants.COLLECTION_NOTIFICATIONS)
            .document(uid)
            .collection(Constants.COLLECTION_ITEMS)
            .add(notifData)
    }
}

