package com.syed.classconnect.ui.classes

import com.google.android.material.tabs.TabLayout

/**
 * Enhanced TabLayout adapter with badge support for unread content
 */
class ClassTabBadgeHelper {

    /**
     * Updates tab badge to show unread count
     */
    fun updateTabBadge(
        tabLayout: TabLayout,
        tabIndex: Int,
        badgeText: String?,
        hasUnread: Boolean
    ) {
        val tab = tabLayout.getTabAt(tabIndex) ?: return
        
        if (badgeText != null && hasUnread) {
            val badge = tab.orCreateBadge
            badge.text = badgeText
            badge.isVisible = true
        } else {
            val badge = tab.badge
            badge?.isVisible = false
        }
    }

    /**
     * Clears all badges from tabs
     */
    fun clearAllBadges(tabLayout: TabLayout) {
        for (i in 0 until tabLayout.tabCount) {
            val tab = tabLayout.getTabAt(i) ?: continue
            val badge = tab.badge
            badge?.isVisible = false
        }
    }

    /**
     * Sets tab content badge (e.g., "3 new" for assignments)
     */
    fun setTabContentBadge(
        tabLayout: TabLayout,
        tabIndex: Int,
        contentType: TabContentType,
        count: Int
    ) {
        val tab = tabLayout.getTabAt(tabIndex) ?: return
        if (count > 0) {
            val badge = tab.orCreateBadge
            badge.number = count
            badge.isVisible = true
        }
    }
}

enum class TabContentType {
    ASSIGNMENTS,
    CHAT,
    STUDENTS,
    QUIZZES,
    FEED,
    ATTENDANCE
}

/**
 * Tab state persistence manager for class details
 */
class ClassTabStateManager {
    private val tabStateMap = mutableMapOf<String, Int>()

    /**
     * Saves the current tab index for a class
     */
    fun saveTabState(classId: String, tabIndex: Int) {
        tabStateMap[classId] = tabIndex
    }

    /**
     * Retrieves the saved tab index for a class
     */
    fun getTabState(classId: String, defaultTab: Int = 0): Int {
        return tabStateMap[classId] ?: defaultTab
    }

    /**
     * Clears saved state for a class
     */
    fun clearTabState(classId: String) {
        tabStateMap.remove(classId)
    }

    /**
     * Clears all saved states
     */
    fun clearAllTabStates() {
        tabStateMap.clear()
    }
}

