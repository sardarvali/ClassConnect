# RoleChangeLog — Data class for admin role change audit trail

---

## 📁 Location
`app/src/main/java/com/syed/classconnect/data/model/RoleChangeLog.kt`

---

## 🎯 What This File Does
The `RoleChangeLog` data class records every time an admin changes a user's role. This creates an audit trail showing who changed what, when, and why. Displayed in the RoleChangeHistoryFragment for accountability and transparency.

---

## 📦 Imports

| Import | Library / Package | What It Is | Why This File Needs It |
|--------|------------------|-----------|----------------------|
| `com.google.firebase.Timestamp` | Firebase Firestore | Server-synchronized timestamp | `changedAt` field |

---

## 📋 Properties

| Property | Type | What It Stores |
|----------|------|---------------|
| `id` | `String` | Firestore document ID |
| `changedByAdminId` | `String` | UID of the admin who made the change |
| `changedByAdminName` | `String` | Admin's display name (denormalized) |
| `targetUserId` | `String` | UID of the user whose role was changed |
| `targetUserName` | `String` | Target user's display name (denormalized) |
| `fromRole` | `String` | Previous role (e.g., "student") |
| `toRole` | `String` | New role (e.g., "teacher") |
| `reason` | `String` | Admin's stated reason for the change |
| `changedAt` | `Timestamp` | When the change was made |

---

## 📝 Full Annotated Source Code

```kotlin
package com.syed.classconnect.data.model
// Package: data model layer.

import com.google.firebase.Timestamp
// Timestamp: Firebase's time type.

data class RoleChangeLog(
    val id: String = "",                  // Firestore document ID
    val changedByAdminId: String = "",    // UID of the admin who made the change
    val changedByAdminName: String = "",  // Admin's name (denormalized)
    val targetUserId: String = "",        // UID of the affected user
    val targetUserName: String = "",      // Affected user's name (denormalized)
    val fromRole: String = "",            // Previous role
    val toRole: String = "",              // New role
    val reason: String = "",              // Why the change was made
    val changedAt: Timestamp = Timestamp.now() // When it happened
)
// Used by UserDetailViewModel when changing a user's role.
// Displayed in RoleChangeHistoryFragment with RoleChangeLogAdapter.
```

