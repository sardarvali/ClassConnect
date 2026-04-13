package com.syed.classconnect.widget

import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.widget.RemoteViews
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.syed.classconnect.R
import com.syed.classconnect.util.Constants
<<<<<<< HEAD
import java.util.Calendar
=======
import com.syed.classconnect.util.ScheduleUtils
>>>>>>> final

class TimetableWidget : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (widgetId in appWidgetIds) {
            updateWidget(context, appWidgetManager, widgetId)
        }
        scheduleNextUpdate(context)
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == ACTION_UPDATE) {
            val manager = AppWidgetManager.getInstance(context)
            val ids = manager.getAppWidgetIds(ComponentName(context, TimetableWidget::class.java))
            onUpdate(context, manager, ids)
        }
    }

    private fun updateWidget(context: Context, manager: AppWidgetManager, widgetId: Int) {
        val views = RemoteViews(context.packageName, R.layout.widget_timetable)

        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid == null) {
            views.setTextViewText(R.id.tv_widget_title, "ClassConnect")
            views.setTextViewText(R.id.tv_widget_content, "Sign in to see your schedule")
            manager.updateAppWidget(widgetId, views)
            return
        }

        views.setTextViewText(R.id.tv_widget_title, "Today's Classes")
        views.setTextViewText(R.id.tv_widget_content, "Loading…")
        manager.updateAppWidget(widgetId, views)

<<<<<<< HEAD
        val today = Calendar.getInstance()
            .getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, java.util.Locale.ENGLISH) ?: ""

        FirebaseFirestore.getInstance()
            .collection(Constants.COLLECTION_CLASSES)
            .whereArrayContains("studentIds", uid)
            .get()
            .addOnSuccessListener { snap ->
                val todayClasses = snap.documents
                    .filter { doc ->
                        @Suppress("UNCHECKED_CAST")
                        val schedule = doc.get("schedule") as? Map<String, String> ?: emptyMap()
                        schedule.containsKey(today)
                    }
                    .joinToString("\n") { doc ->
                        @Suppress("UNCHECKED_CAST")
                        val schedule = doc.get("schedule") as? Map<String, String> ?: emptyMap()
                        val name = doc.getString("name") ?: ""
                        val time = schedule[today] ?: ""
                        "• $name  $time"
                    }

                val content = if (todayClasses.isBlank()) "No classes today 🎉" else todayClasses
                views.setTextViewText(R.id.tv_widget_content, content)
                manager.updateAppWidget(widgetId, views)
=======
        FirebaseFirestore.getInstance()
            .collection(Constants.COLLECTION_USERS)
            .document(uid)
            .get()
            .addOnSuccessListener { userDoc ->
                val role = userDoc.getString("role") ?: Constants.ROLE_STUDENT
                val query = if (role == Constants.ROLE_TEACHER || role == Constants.ROLE_ADMIN) {
                    FirebaseFirestore.getInstance()
                        .collection(Constants.COLLECTION_CLASSES)
                        .whereEqualTo("teacherId", uid)
                } else {
                    FirebaseFirestore.getInstance()
                        .collection(Constants.COLLECTION_CLASSES)
                        .whereArrayContains("studentIds", uid)
                }

                query.get()
                    .addOnSuccessListener { snap ->
                        val todayClasses = snap.documents
                            .flatMap { doc ->
                                val schedule = (doc["schedule"] as? Map<*, *>)
                                    ?.mapNotNull { (day, value) ->
                                        val dayName = day as? String
                                        val slotValue = value as? String
                                        if (dayName.isNullOrBlank() || slotValue.isNullOrBlank()) {
                                            null
                                        } else {
                                            dayName to slotValue
                                        }
                                    }
                                    ?.toMap()
                                    ?: emptyMap()

                                val slots = ScheduleUtils.slotsForDay(schedule)
                                if (slots.isEmpty()) {
                                    emptyList()
                                } else {
                                    val name = doc.getString("name") ?: ""
                                    slots.map { slot ->
                                        WidgetClassLine(
                                            startMinutes = ScheduleUtils.startMinutesForSlot(slot)
                                                ?: Int.MAX_VALUE,
                                            line = "• $name  $slot"
                                        )
                                    }
                                }
                            }
                            .sortedBy { it.startMinutes }
                            .joinToString("\n") { it.line }

                        val content =
                            if (todayClasses.isBlank()) "No classes today 🎉" else todayClasses
                        views.setTextViewText(R.id.tv_widget_content, content)
                        manager.updateAppWidget(widgetId, views)
                    }
                    .addOnFailureListener {
                        views.setTextViewText(R.id.tv_widget_content, "Could not load schedule")
                        manager.updateAppWidget(widgetId, views)
                    }
>>>>>>> final
            }
            .addOnFailureListener {
                views.setTextViewText(R.id.tv_widget_content, "Could not load schedule")
                manager.updateAppWidget(widgetId, views)
            }

        // Tap opens MainActivity
        val launchIntent = context.packageManager
            .getLaunchIntentForPackage(context.packageName)
        val pendingIntent = PendingIntent.getActivity(
            context, 0, launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.widget_root, pendingIntent)
    }

    /** Schedule an alarm to refresh the widget every 30 minutes. */
    private fun scheduleNextUpdate(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, TimetableWidget::class.java).apply {
            action = ACTION_UPDATE
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val triggerAt = SystemClock.elapsedRealtime() + 30 * 60 * 1000L
        alarmManager.set(AlarmManager.ELAPSED_REALTIME, triggerAt, pendingIntent)
    }

    companion object {
        const val ACTION_UPDATE = "com.syed.classconnect.WIDGET_UPDATE"
    }
<<<<<<< HEAD
=======

    private data class WidgetClassLine(
        val startMinutes: Int,
        val line: String
    )
>>>>>>> final
}
