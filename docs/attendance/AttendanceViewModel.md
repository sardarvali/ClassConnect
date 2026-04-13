# AttendanceViewModel — Attendance session management

---

## 📁 Location
`app/src/main/java/com/syed/classconnect/ui/attendance/AttendanceViewModel.kt`

---

## 🎯 What This File Does
AttendanceViewModel handles starting attendance sessions, generating QR tokens, marking students present, and loading attendance history. Used by AttendanceFragment.

---

## ⚙️ Key Functions
- `startSession(classId)`: Creates AttendanceRecord with UUID token and 5-min expiry
- `markPresent(classId, token, studentId)`: Validates token + expiry, adds to present list
- `loadHistory(classId)`: Loads past attendance records
- `endSession(classId, date)`: Calculates absent students (allStudentIds - present)

