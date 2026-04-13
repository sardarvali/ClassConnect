# AttendanceRepository — Data access for attendance sessions

---

## 📁 Location
`app/src/main/java/com/syed/classconnect/data/repository/AttendanceRepository.kt`

---

## 🎯 What This File Does
AttendanceRepository handles Firestore operations for attendance sessions. Creates sessions with QR tokens, validates token-based check-ins, manages present/absent lists, and retrieves attendance history.

---

## ⚙️ Key Functions

| Function | Returns | Description |
|----------|---------|-------------|
| `createSession(classId, record)` | `Result<Unit>` | Creates attendance document with ISO date as ID |
| `markPresent(classId, date, studentId)` | `Result<Unit>` | Atomic arrayUnion on present list |
| `getSession(classId, date)` | `AttendanceRecord?` | Fetch today's session |
| `validateToken(classId, date, token)` | `Boolean` | Check token matches and not expired |
| `endSession(classId, date, absentIds)` | `Result<Unit>` | Sets absent list |
| `getHistory(classId)` | `Flow<List<AttendanceRecord>>` | Real-time attendance history |

