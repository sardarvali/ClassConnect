# TimetableWidget — Home screen widget showing today's classes

---

## 📁 Location
`app/src/main/java/com/syed/classconnect/widget/TimetableWidget.kt`

---

## 🎯 What This File Does
TimetableWidget is an Android App Widget that displays today's class schedule on the user's home screen. Updates periodically (every 30 minutes by default) and shows class name, time, and subject for today's day of the week. Uses RemoteViews for widget layout since widgets can't use standard View hierarchies.

---

## ⚙️ Key Functions

### `onUpdate(context, appWidgetManager, appWidgetIds)`
Called on each widget update cycle:
1. Gets today's day name from Calendar
2. Reads user's classIds from SharedPreferences (cached from last app launch)
3. Queries Firestore for class documents
4. Filters classes where `schedule[todayDayName]` exists
5. Builds RemoteViews with today's class list
6. Updates widget via appWidgetManager

### `updateWidgetData(context)`
Static function called from MainActivity/HomeViewModel to cache class data for widget access (widgets can't use Hilt or complex DI).

---

## ⚠️ Important Notes
- Widgets use `RemoteViews` — limited to basic views (TextView, ImageView, LinearLayout)
- Firestore queries from widget must use a separate coroutine scope (not viewModelScope)
- Class data is cached in SharedPreferences because widget updates may happen when app is not running
- Widget configuration defined in `res/xml/timetable_widget_info.xml`

