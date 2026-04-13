package com.syed.classconnect.util

object Constants {
    // Firestore collections
    const val COLLECTION_USERS = "users"
    const val COLLECTION_INSTITUTIONS = "institutions"
    const val COLLECTION_CLASSES = "classes"
    const val COLLECTION_ANNOUNCEMENTS = "announcements"
    const val COLLECTION_MATERIALS = "materials"
    const val COLLECTION_ASSIGNMENTS = "assignments"
    const val COLLECTION_SUBMISSIONS = "submissions"
    const val COLLECTION_QUIZZES = "quizzes"
    const val COLLECTION_ATTEMPTS = "attempts"
    const val COLLECTION_ATTENDANCE = "attendance"
    const val COLLECTION_CHAT = "chat"
    const val COLLECTION_NOTIFICATIONS = "notifications"
    const val COLLECTION_ITEMS = "items"

    // User roles
    const val ROLE_ADMIN = "admin"
    const val ROLE_TEACHER = "teacher"
    const val ROLE_STUDENT = "student"

    // Notification channels
    const val CHANNEL_ASSIGNMENTS = "channel_assignments"
    const val CHANNEL_CHAT = "channel_chat"
    const val CHANNEL_ANNOUNCEMENTS = "channel_announcements"
    const val CHANNEL_ATTENDANCE = "channel_attendance"
    const val CHANNEL_GRADES = "channel_grades"

    // SharedPreferences keys
    const val PREF_ONBOARDING_COMPLETE = "onboarding_complete"
    const val PREF_THEME_MODE = "theme_mode"
    const val PREF_BIOMETRIC_ENABLED = "biometric_enabled"
    const val PREF_PERMISSIONS_REQUESTED = "permissions_requested"
    const val PREF_CAMPUS_WIFI_SSID = "campus_wifi_ssid"
    const val PREF_LESSON_PLANS = "lesson_plans"

    // Intent extras
    const val EXTRA_CLASS_ID = "class_id"
    const val EXTRA_CLASS_NAME = "class_name"
    const val EXTRA_CLASS_COLOR = "class_color"
    const val EXTRA_ASSIGNMENT_ID = "assignment_id"
    const val EXTRA_QUIZ_ID = "quiz_id"
    const val EXTRA_URL = "url"
    const val EXTRA_TITLE = "title"
    const val EXTRA_TAB_INDEX = "tab_index"
    const val EXTRA_USER_ROLE = "user_role"
    const val EXTRA_SHARED_BG_TRANSITION_NAME = "shared_bg_transition_name"
    const val EXTRA_SHARED_TITLE_TRANSITION_NAME = "shared_title_transition_name"

    // Shared-element transition name prefixes (suffixed with classId)
    const val TRANSITION_CLASS_HEADER_PREFIX = "class_header_"
    const val TRANSITION_CLASS_TITLE_PREFIX = "class_title_"

    // Class card preset colors
    val CLASS_COLORS = listOf(
        "#1565C0", "#2E7D32", "#E65100", "#6A1B9A",
        "#C62828", "#00695C", "#AD1457", "#4E342E"
    )

    // APIs
    const val GEMINI_BASE_URL = "https://generativelanguage.googleapis.com/"
    const val NEWS_BASE_URL = "https://newsapi.org/"

    // Attendance QR expiry
    const val QR_EXPIRY_MINUTES = 5L

    // File size limits
    const val MAX_FILE_SIZE_MB = 50
}

