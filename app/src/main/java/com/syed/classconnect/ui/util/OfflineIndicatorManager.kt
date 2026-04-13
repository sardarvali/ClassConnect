package com.syed.classconnect.ui.util

import android.content.Context
import android.net.ConnectivityManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages offline state and sync status
 * Shows persistent banner when offline and sync status in toolbar
 */
@Singleton
class OfflineIndicatorManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val _isOffline = MutableLiveData(false)
    val isOffline: LiveData<Boolean> = _isOffline

    private val _syncStatus = MutableLiveData<SyncStatus>(SyncStatus.Synced)
    val syncStatus: LiveData<SyncStatus> = _syncStatus

    private val _lastSyncTime = MutableLiveData<Long?>(null)
    val lastSyncTime: LiveData<Long?> = _lastSyncTime

    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE)
            as? ConnectivityManager

    /**
     * Checks if device is connected to internet
     */
    fun isConnected(): Boolean {
        val network = connectivityManager?.activeNetwork
        return network != null
    }

    /**
     * Updates offline state
     */
    fun setOfflineState(isOffline: Boolean) {
        _isOffline.postValue(isOffline)
    }

    /**
     * Updates sync status
     */
    fun setSyncStatus(status: SyncStatus) {
        _syncStatus.postValue(status)
        if (status == SyncStatus.Synced) {
            _lastSyncTime.postValue(System.currentTimeMillis())
        }
    }

    /**
     * Gets human-readable sync time
     */
    fun getLastSyncTimeText(): String {
        val lastSync = _lastSyncTime.value ?: return ""
        val secondsAgo = (System.currentTimeMillis() - lastSync) / 1000

        return when {
            secondsAgo < 60 -> "Just now"
            secondsAgo < 3600 -> "${secondsAgo / 60} minutes ago"
            secondsAgo < 86400 -> "${secondsAgo / 3600} hours ago"
            else -> "${secondsAgo / 86400} days ago"
        }
    }
}

sealed class SyncStatus {
    object Syncing : SyncStatus()
    object Synced : SyncStatus()
    data class Error(val message: String) : SyncStatus()
}

/**
 * Conflict resolution data class
 */
data class SyncConflict(
    val itemId: String,
    val localVersion: Any,
    val remoteVersion: Any,
    val timestamp: Long = System.currentTimeMillis()
)

