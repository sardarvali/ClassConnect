# User — Data class representing a registered user

---

## 📁 Location
`app/src/main/java/com/syed/classconnect/data/model/User.kt`

---

## 🎯 What This File Does
The `User` data class represents a registered user in ClassConnect. Every person who signs up — student, teacher, or admin — has a corresponding User document in Firestore. This model maps directly to the `/users/{userId}` collection. Without this file, the app would have no way to represent or deserialize user information from the database.

---

## 📦 Imports — Every Line Explained

| Import | Library / Package | What It Is | Why This File Needs It |
|--------|------------------|-----------|----------------------|
| `com.google.firebase.Timestamp` | Firebase Firestore | A point in time with nanosecond precision | Used for `createdAt` and `roleChangedAt` fields — server-synchronized timestamps |
| `com.google.firebase.firestore.PropertyName` | Firebase Firestore | Annotation to map Firestore field names to Kotlin property names | Needed for boolean fields prefixed with `is` (e.g., `isApproved`) — Firestore serialization drops the `is` prefix without this annotation |

---

## 🔑 Kotlin & Android Keywords

### `package` — Package Declaration
```kotlin
package com.syed.classconnect.data.model
```
Declares which package this file belongs to. Packages organize related files into namespaces. This file is in the `data.model` package because it's a data model class.

---

### `data class` — Auto-Generated Data Holder
```kotlin
data class User(val name: String, val email: String)
```
`data class` automatically generates `equals()`, `hashCode()`, `toString()`, `copy()`, and `componentN()` functions. Used for all model objects where you care about the data values, not object identity. See [ARCHITECTURE.md](../setup/ARCHITECTURE.md) for more on data classes.

---

### `val` vs `var`
Most fields use `val` (read-only). Three fields use `var` (mutable): `isApproved`, `isRejected`, and `emailVerified`. These use `var` because Firestore's `toObject()` deserialization requires mutable setters for fields annotated with `@field:PropertyName`.

---

### `@field:PropertyName("isApproved")` — Firestore Field Name Mapping
```kotlin
@field:PropertyName("isApproved")
var isApproved: Boolean = false
```
Firestore's Java-based serializer has a bug with Kotlin boolean properties prefixed with `is`. Without this annotation, Firestore would look for a field named `approved` (dropping the `is` prefix). The `@field:PropertyName` forces Firestore to use the exact field name `isApproved`.

---

### `?` — Nullable Type
```kotlin
val roleChangedAt: Timestamp? = null
```
The `?` makes the type nullable. `Timestamp?` means this field can be `null`. Used when a field may not exist in the Firestore document (e.g., `roleChangedAt` is only set after an admin changes a user's role).

---

### Default Parameter Values
```kotlin
val role: String = "student"
```
Every field has a default value. This is **required** for Firestore deserialization — `toObject(User::class.java)` calls the no-argument constructor and then sets fields. Without defaults, deserialization throws an exception.

---

## 🏗️ Class Structure

```kotlin
data class User(
    val uid: String = "",                    // Firebase Auth UID
    val name: String = "",                   // Full display name
    val email: String = "",                  // Email address
    val photoUrl: String = "",               // Profile photo URL (Firebase Storage)
    val role: String = "student",            // "admin" | "teacher" | "student"
    val institutionId: String = "",           // Links to /institutions/{id}
    var isApproved: Boolean = false,          // Has admin approved this user?
    var isRejected: Boolean = false,          // Has admin rejected this user?
    val fcmToken: String = "",               // FCM push notification token
    val classIds: List<String> = emptyList(), // IDs of classes user belongs to
    val createdAt: Timestamp = Timestamp.now(), // Account creation time
    val bio: String = "",                    // User bio/description
    val accountType: String = "institution",  // "institution" | "independent"
    var emailVerified: Boolean = false,       // Has email been verified?
    val roleChangedAt: Timestamp? = null,     // When role was last changed
    val roleChangedBy: String = ""           // UID of admin who changed role
)
```

---

## 📋 Properties

| Property | Type | Modifier | What It Stores |
|----------|------|----------|---------------|
| `uid` | `String` | `val` | Firebase Auth unique identifier — matches the Firestore document ID |
| `name` | `String` | `val` | User's full display name, sanitized to max 100 chars |
| `email` | `String` | `val` | User's email address used for login |
| `photoUrl` | `String` | `val` | URL to profile photo in Firebase Storage |
| `role` | `String` | `val` | User role: `"admin"`, `"teacher"`, or `"student"` |
| `institutionId` | `String` | `val` | References the institution this user belongs to (empty for independent users) |
| `isApproved` | `Boolean` | `var` | Whether admin has approved this user (institution path) or email verified (independent path) |
| `isRejected` | `Boolean` | `var` | Whether admin has rejected this user's registration |
| `fcmToken` | `String` | `val` | Firebase Cloud Messaging token for push notifications |
| `classIds` | `List<String>` | `val` | List of class IDs the user is enrolled in (students) |
| `createdAt` | `Timestamp` | `val` | Server timestamp of account creation |
| `bio` | `String` | `val` | User's biography text for profile page |
| `accountType` | `String` | `val` | Registration path: `"institution"` (with code) or `"independent"` (email verify) |
| `emailVerified` | `Boolean` | `var` | Whether the user has verified their email address |
| `roleChangedAt` | `Timestamp?` | `val` | Timestamp of last role change by admin (null if never changed) |
| `roleChangedBy` | `String` | `val` | UID of the admin who last changed this user's role |

---

## 🔄 Data Flow

```
Registration → AuthRepository creates User document in /users/{uid}
Login → AuthRepository reads User document → returns User object
Profile → ProfileViewModel reads/updates User fields
Admin → UserDetailViewModel changes role, isApproved, isRejected
FCM → MyFirebaseMessagingService updates fcmToken
```

---

## 🧩 This File Depends On

| Dependency | Why |
|-----------|-----|
| `com.google.firebase.Timestamp` | For `createdAt` and `roleChangedAt` timestamp fields |
| `com.google.firebase.firestore.PropertyName` | For correct Firestore field name mapping on `is`-prefixed booleans |

---

## ⚠️ Important Notes
- All fields MUST have default values for Firestore `toObject()` deserialization to work
- `isApproved`, `isRejected`, `emailVerified` use `var` + `@field:PropertyName` due to Firestore's Java serializer behavior with `is`-prefixed booleans
- `uid` in the data class is duplicated from the Firestore document ID — we use `.copy(uid = snap.id)` after deserialization to ensure consistency
- `accountType` was added in Prompt 2 — older documents may not have this field (defaults to `"institution"`)

---

## 📝 Full Annotated Source Code

```kotlin
package com.syed.classconnect.data.model
// Declares this file belongs to the data.model package.
// All model/data classes are organized here.

import com.google.firebase.Timestamp
// Timestamp: Firebase's time representation with nanosecond precision.
// Used for createdAt, roleChangedAt fields.
// More precise than java.util.Date.

import com.google.firebase.firestore.PropertyName
// PropertyName: Annotation that tells Firestore the exact field name to use.
// Without it, Firestore drops the "is" prefix from boolean getters.

data class User(
// data class: Kotlin auto-generates equals(), hashCode(), toString(), copy(), componentN().
// User: represents a registered user in the ClassConnect system.

    val uid: String = "",
    // uid: Firebase Auth unique identifier.
    // Matches the Firestore document ID in /users/{uid}.
    // val: cannot be reassigned after creation.
    // Default "": required for Firestore toObject() no-arg constructor.

    val name: String = "",
    // name: User's full display name.
    // Sanitized to max 100 characters during registration (ValidationUtils.sanitize).

    val email: String = "",
    // email: User's email address.
    // Used for email/password authentication.

    val photoUrl: String = "",
    // photoUrl: URL to the user's profile photo stored in Firebase Storage.
    // Empty string if no photo uploaded.

    val role: String = "student", // "admin" | "teacher" | "student"
    // role: Determines what the user can do in the app.
    // "admin": manage users, classes, institution settings.
    // "teacher": create classes, assignments, quizzes, take attendance.
    // "student": join classes, submit work, take quizzes, mark attendance.
    // Default "student": safest default — least privileges.

    val institutionId: String = "",
    // institutionId: Links this user to an institution document in /institutions/{id}.
    // Empty for independent users (accountType == "independent").

    @field:PropertyName("isApproved")
    // @field:PropertyName: Forces Firestore to use "isApproved" as the field name.
    // Without this, Firestore's Java serializer would use "approved" (drops "is" prefix).
    var isApproved: Boolean = false,
    // isApproved: true if the user is approved to use the app.
    // Institution path: set to true by admin.
    // Independent path: set to true after email verification.
    // var: must be mutable for Firestore deserialization with @PropertyName.

    @field:PropertyName("isRejected")
    var isRejected: Boolean = false,
    // isRejected: true if admin explicitly rejected this user.
    // Used to show rejection message instead of "pending" message.

    val fcmToken: String = "",
    // fcmToken: Firebase Cloud Messaging token.
    // Updated on each app launch and when token refreshes.
    // Used by Cloud Functions to send push notifications to this specific device.

    val classIds: List<String> = emptyList(),
    // classIds: List of class document IDs this user belongs to.
    // Updated when a student joins a class (FieldValue.arrayUnion).
    // Used by the home screen widget to query today's schedule.

    val createdAt: Timestamp = Timestamp.now(),
    // createdAt: When this user account was created.
    // Set to FieldValue.serverTimestamp() during registration for accuracy.
    // Default Timestamp.now() is a client-side fallback.

    val bio: String = "",
    // bio: User's biography/description shown on the profile page.
    // Empty by default; user can edit in ProfileFragment.

    // Prompt 2 additions
    val accountType: String = "institution",  // "institution" | "independent"
    // accountType: Which registration path the user took.
    // "institution": registered with an institution code → admin approval required.
    // "independent": registered without code → email verification → auto-approve.

    @field:PropertyName("emailVerified")
    var emailVerified: Boolean = false,
    // emailVerified: Whether the user has clicked the verification link in their email.
    // Only relevant for independent users.
    // Set to true by EmailVerificationViewModel after polling detects verification.

    val roleChangedAt: Timestamp? = null,
    // roleChangedAt: When an admin last changed this user's role.
    // Nullable (?) because it's null until a role change occurs.

    val roleChangedBy: String = ""
    // roleChangedBy: UID of the admin who performed the last role change.
    // Empty until a role change occurs.
)
// End of User data class.
// This class is used throughout the entire application:
// - AuthRepository: creates/reads User documents
// - HomeViewModel: displays user info on home screen
// - ProfileViewModel: edits user profile
// - AdminViewModel: manages user roles and approvals
// - ChatAdapter: displays sender info in chat messages
```

