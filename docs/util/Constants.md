# Constants — App-wide constant values

---

## 📁 Location
`app/src/main/java/com/syed/classconnect/util/Constants.kt`

---

## 🎯 What This File Does
Constants.kt is a Kotlin `object` (singleton) that holds all constant values used across the app: Firestore collection names, role strings, intent extra keys, preference keys, class colors, and configuration values. Centralizing constants prevents typos and makes refactoring easy.

---

## 📋 Constants Reference

### Firestore Collection Names
| Constant | Value | Used For |
|----------|-------|---------|
| `COLLECTION_USERS` | `"users"` | `/users/{uid}` |
| `COLLECTION_CLASSES` | `"classes"` | `/classes/{classId}` |
| `COLLECTION_INSTITUTIONS` | `"institutions"` | `/institutions/{id}` |
| `COLLECTION_ASSIGNMENTS` | `"assignments"` | Subcollection under classes |
| `COLLECTION_SUBMISSIONS` | `"submissions"` | Subcollection under assignments |
| `COLLECTION_QUIZZES` | `"quizzes"` | Subcollection under classes |
| `COLLECTION_ATTEMPTS` | `"attempts"` | Subcollection under quizzes |
| `COLLECTION_ATTENDANCE` | `"attendance"` | Subcollection under classes |
| `COLLECTION_CHAT` | `"chat"` | Subcollection under classes |
| `COLLECTION_ANNOUNCEMENTS` | `"announcements"` | Subcollection under classes |
| `COLLECTION_MATERIALS` | `"materials"` | Subcollection under classes |
| `COLLECTION_NOTIFICATIONS` | `"notifications"` | `/notifications/{uid}` |
| `COLLECTION_ITEMS` | `"items"` | Subcollection under notifications |
| `COLLECTION_ROLE_CHANGES` | `"roleChanges"` | Role change audit log |

### Role Strings
| Constant | Value |
|----------|-------|
| `ROLE_ADMIN` | `"admin"` |
| `ROLE_TEACHER` | `"teacher"` |
| `ROLE_STUDENT` | `"student"` |

### Intent Extra Keys
| Constant | Value |
|----------|-------|
| `EXTRA_CLASS_ID` | `"class_id"` |
| `EXTRA_CLASS_NAME` | `"class_name"` |
| `EXTRA_CLASS_COLOR` | `"class_color"` |
| `EXTRA_URL` | `"url"` |

### Preference Keys
| Constant | Value |
|----------|-------|
| `PREF_BIOMETRIC_ENABLED` | `"biometric_enabled"` |
| `PREF_DARK_MODE` | `"dark_mode"` |
| `PREF_PERMISSIONS_REQUESTED` | `"permissions_requested"` |

### Class Colors
```kotlin
val CLASS_COLORS = listOf("#1565C0", "#2E7D32", "#C62828", "#6A1B9A", "#E65100", "#00838F")
```

### Configuration
| Constant | Value | Purpose |
|----------|-------|---------|
| `QR_EXPIRY_MINUTES` | `5` | QR code expiry time |
| `MAX_NAME_LENGTH` | `100` | Maximum name length |

