# AttendanceFragment — QR-based attendance for teachers and students

---

## 📁 Location
`app/src/main/java/com/syed/classconnect/ui/attendance/AttendanceFragment.kt`

---

## 🎯 What This File Does
AttendanceFragment handles both sides of QR-based attendance. Teachers can start an attendance session (generates a QR code) and view real-time present/absent counts. Students can scan the QR code to mark themselves present. Integrates with camera permissions and the BLE service.

---

## ⚙️ Key Functions
- **Teacher mode**: Start session → generates QR with UUID token and 5-minute expiry → displays QR code → real-time present count
- **Student mode**: Scan QR code → validates token and expiry → marks student as present via `FieldValue.arrayUnion`
- Checks camera permission before scanning
- Uses `AttendanceBleService` for BLE-based proximity (optional)

---

## 🔄 Data Flow
```
Teacher starts session:
    → AttendanceViewModel.startSession(classId)
    → Creates AttendanceRecord with qrToken + qrExpiresAt
    → Generates QR code bitmap from token string
    → Displays QR for students to scan

Student scans QR:
    → Camera → QR decoder extracts token
    → AttendanceViewModel.markPresent(classId, token, studentId)
    → Validates token matches Firestore + not expired
    → FieldValue.arrayUnion(studentId) on present list
```

