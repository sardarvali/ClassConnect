package com.syed.classconnect

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.syed.classconnect.sensor.AppLifecycleObserver
import com.syed.classconnect.util.Constants
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class ClassConnectApp : Application() {

    @Inject
    lateinit var appLifecycleObserver: AppLifecycleObserver

    @Inject
    lateinit var prefs: SharedPreferences

    override fun onCreate() {
        super.onCreate()

        val themeMode =
            prefs.getInt(Constants.PREF_THEME_MODE, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        AppCompatDelegate.setDefaultNightMode(themeMode)

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
            FirebaseCrashlytics.getInstance().isCrashlyticsCollectionEnabled = false
        } else {
            FirebaseCrashlytics.getInstance().isCrashlyticsCollectionEnabled = true
        }

        val previousHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            Timber.e(throwable, "Uncaught exception on thread: ${thread.name}")
            FirebaseCrashlytics.getInstance().recordException(throwable)
            previousHandler?.uncaughtException(thread, throwable)
        }

        ProcessLifecycleOwner.get().lifecycle.addObserver(appLifecycleObserver)
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nm = getSystemService(NotificationManager::class.java)
            val channels = listOf(
                NotificationChannel(
                    Constants.CHANNEL_ASSIGNMENTS,
                    getString(R.string.channel_assignments),
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = getString(R.string.channel_assignments_desc)
                },
                NotificationChannel(
                    Constants.CHANNEL_CHAT,
                    getString(R.string.channel_chat),
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = getString(R.string.channel_chat_desc)
                },
                NotificationChannel(
                    Constants.CHANNEL_ANNOUNCEMENTS,
                    getString(R.string.channel_announcements),
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = getString(R.string.channel_announcements_desc)
                },
                NotificationChannel(
                    Constants.CHANNEL_ATTENDANCE,
                    getString(R.string.channel_attendance),
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = getString(R.string.channel_attendance_desc)
                },
                NotificationChannel(
                    Constants.CHANNEL_GRADES,
                    getString(R.string.channel_grades),
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = getString(R.string.channel_grades_desc)
                }
            )
            channels.forEach { nm.createNotificationChannel(it) }
        }
    }
}
