package com.syed.classconnect.ui.classes

/**
 * Enhanced class card design with visual elements
 */
data class EnhancedClassCard(
    val classId: String,
    val className: String,
    val teacherName: String,
    val teacherAvatarUrl: String? = null,
    val schedulePreview: List<String> = emptyList(), // e.g., ["MWF 10:00", "TuTh 2:00"]
    val unreadAssignments: Int = 0,
    val unreadChatMessages: Int = 0,
    val averageAttendance: Float = 0f,
    val studentCount: Int = 0,
    val gradientStartColor: Int = 0xFF6200EE.toInt(),
    val gradientEndColor: Int = 0xFF03DAC6.toInt(),
    val isActive: Boolean = true
)

/**
 * Class analytics data
 */
data class ClassStats(
    val classId: String,
    val studentCount: Int,
    val pendingAssignments: Int,
    val averageAttendance: Float,
    val averageGrade: Float,
    val lastActivityDate: Long = System.currentTimeMillis(),
    val totalSubmissions: Int,
    val submissionRate: Float // percentage
)

/**
 * Class list filter options
 */
sealed class ClassFilter {
    object Active : ClassFilter()
    object Archived : ClassFilter()
    object AllClasses : ClassFilter()
    data class ByRole(val role: String) : ClassFilter()
}

/**
 * Class list sort options
 */
enum class ClassSortOrder {
    ALPHABETICAL,
    RECENTLY_JOINED,
    MOST_ACTIVE,
    UPCOMING_DEADLINES
}

/**
 * Enhanced class list item with all details
 */
data class ClassListItem(
    val card: EnhancedClassCard,
    val stats: ClassStats? = null,
    val isSelected: Boolean = false,
    val lastVisited: Long? = null
)

