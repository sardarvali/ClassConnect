package com.syed.classconnect.ui.notifications

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.material.bottomnavigation.BottomNavigationView
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages notification badges on bottom navigation items.
 * Displays unread notification count with red circular badge.
 */
@Singleton
class NotificationBadgeManager @Inject constructor() {

    private val _unreadCount = MutableLiveData(0)
    val unreadCount: LiveData<Int> = _unreadCount

    /**
     * Updates the unread notification count
     */
    fun setUnreadCount(count: Int) {
        _unreadCount.value = count
    }

    /**
     * Increments the unread notification count
     */
    fun incrementCount() {
        _unreadCount.value = (_unreadCount.value ?: 0) + 1
    }

    /**
     * Decrements the unread notification count
     */
    fun decrementCount() {
        _unreadCount.value = ((_unreadCount.value ?: 1) - 1).coerceAtLeast(0)
    }

    /**
     * Resets unread notification count to 0
     */
    fun clearCount() {
        _unreadCount.value = 0
    }

    /**
     * Applies badge to bottom navigation item
     * Note: Resource ID 'notifications' may vary based on your menu structure
     */
    fun applyBadgeToBottomNav(bottomNav: BottomNavigationView, count: Int) {
        try {
            // Try to find notifications menu item - adjust ID based on your menu
            val menuItemIds = intArrayOf(
                android.R.id.home,
                android.R.id.edit,
                android.R.id.background
            )

            // Use first available menu item or catch exception if none exist
            for (menuItemId in menuItemIds) {
                try {
                    val badge = bottomNav.getOrCreateBadge(menuItemId)
                    badge.isVisible = count > 0
                    badge.number = if (count > 99) {
                        99 // Show +99 for counts > 99
                    } else {
                        count
                    }
                    return
                } catch (e: Exception) {
                    // Try next menu item
                }
            }
        } catch (e: Exception) {
            // Handle case where notifications menu item doesn't exist
        }
    }

    /**
     * Removes badge from bottom navigation
     */
    fun removeBadgeFromBottomNav(bottomNav: BottomNavigationView) {
        try {
            // Try to find and remove badge from any available menu item
            for (i in 0 until bottomNav.menu.size()) {
                try {
                    val badge = bottomNav.getBadge(bottomNav.menu.getItem(i).itemId)
                    badge?.isVisible = false
                } catch (e: Exception) {
                    // Continue trying other items
                }
            }
        } catch (e: Exception) {
            // Handle case where no menu items exist
        }
    }
}

