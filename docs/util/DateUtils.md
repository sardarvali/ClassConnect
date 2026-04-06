# DateUtils — Date formatting and comparison helpers

---

## 📁 Location
`app/src/main/java/com/syed/classconnect/util/DateUtils.kt`

---

## 🎯 What This File Does
DateUtils provides helper functions for date formatting, comparison, and display. Used for assignment due dates, attendance dates, message timestamps, and notification times.

---

## ⚙️ Key Functions

### `todayIsoString(): String`
Returns today's date as ISO string (e.g., "2026-03-06"). Used as attendance document IDs.

### `formatTimestamp(timestamp: Timestamp): String`
Formats a Firebase Timestamp into a human-readable string (e.g., "Mar 6, 2026 at 10:30 AM").

### `formatRelative(timestamp: Timestamp): String`
Returns relative time string: "Just now", "5m ago", "2h ago", "Yesterday", or formatted date.

### `isOverdue(dueDate: Timestamp): Boolean`
Returns true if the due date has passed.

### `daysUntil(dueDate: Timestamp): Int`
Returns number of days until the due date (negative if overdue).

### `formatDuration(minutes: Int): String`
Formats minutes into "X hours Y minutes" display string.

