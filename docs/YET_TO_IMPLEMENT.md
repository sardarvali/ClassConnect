# ClassConnect — Yet-To-Implement Analysis

> **Generated:** March 6, 2026
> **Method:** Full source scan of every `.kt` and `.xml` file compared against documentation and project requirements.

---

## Summary

| Category | Count |
|----------|-------|
| ✅ **Fully Implemented** files | 107 `.kt` + 68 `.xml` |
| ⚠️ **Documented but NOT in source code** | 3 files |
| 🔴 **Exist in source but NOT documented** | 19 files |
| 🟡 **Partially implemented / stubbed** | 4 files |
| 🚧 **Missing features within existing files** | 12 features |

---

## ══════════════════════════════════════════════════
## SECTION 1: Files Documented but NOT in Source Code
## ══════════════════════════════════════════════════

These files have doc pages but **no corresponding `.kt` file exists** in the project:

| Doc File | Expected Source | Status |
|----------|----------------|--------|
| `docs/sensors/AppLifecycleObserver.md` | `sensor/AppLifecycleObserver.kt` | ❌ **Does not exist** — No lifecycle observer class in the `sensor/` package. Only `SensorHandler.kt` exists there. |
| `docs/attendance/QrCodeAnalyzer.md` | `ui/attendance/QrCodeAnalyzer.kt` | ❌ **Does not exist** — QR scanning is done via `IntentIntegrator` (ZXing) inline in `AttendanceFragment`, not a separate analyzer class. |
| `docs/chat/ChatActivity.md` | `ui/chat/ChatActivity.kt` | ❌ **Does not exist** — Chat is a Fragment tab inside `ClassDetailActivity`, not a standalone Activity. |
| `docs/classes/ClassSettingsBottomSheet.md` | `ui/classes/ClassSettingsBottomSheet.kt` | ❌ **Does not exist** — No class settings bottom sheet. Settings (class code copy/share) are handled inline in `ClassDetailActivity`. |

---

## ══════════════════════════════════════════════════
## SECTION 2: Source Files That Exist but Have NO Documentation
## ══════════════════════════════════════════════════

These `.kt` files exist in the project source but were **not included in the `/docs` folder**:

### Core App Files
| Source File | What It Is | Why It Matters |
|-------------|-----------|----------------|
| `ClassConnectApp.kt` | `@HiltAndroidApp` Application class | **Critical** — Entry point for Hilt DI. Initializes the entire dependency graph. |
| `ui/main/MainActivity.kt` | Main navigation host Activity | **Critical** — Hosts bottom navigation, role-based nav menu, biometric lock. |
| `ui/auth/AuthActivity.kt` | Auth navigation host Activity | **Important** — Hosts auth nav graph (Login, Register, ForgotPassword, etc.). |

### UI Files — Adapters (no docs)
| Source File | What It Is |
|-------------|-----------|
| `ui/classes/ClassAdapter.kt` | RecyclerView adapter for class cards in ClassListFragment |
| `ui/assignments/AssignmentsAdapter.kt` | RecyclerView adapter for assignment list items |
| `ui/quiz/QuizAdapter.kt` | RecyclerView adapter for quiz list items |
| `ui/quiz/QuizAttemptsAdapter.kt` | RecyclerView adapter for teacher's quiz attempt list |
| `ui/quiz/AddQuestionDialog.kt` | Dialog for manually adding quiz questions |
| `ui/classes/StudentsAdapter.kt` | RecyclerView adapter for student list |
| `ui/home/TodayClassesAdapter.kt` | Horizontal card adapter for today's classes |
| `ui/home/UpcomingDeadlinesAdapter.kt` | Adapter for upcoming deadline items |
| `ui/home/RecentAnnouncementsAdapter.kt` | Adapter for recent announcement items |
| `ui/home/NewsAdapter.kt` | Adapter for education news cards |
| `ui/admin/UserManagementAdapter.kt` | Adapter for admin user list |
| `ui/admin/RoleChangeLogAdapter.kt` | Adapter for role change history |
| `ui/admin/AssignTeacherAdapter.kt` | Adapter for teacher selection list |
| `ui/admin/AdminClassAdapter.kt` | Adapter for admin class list |
| `ui/admin/AdminClassCardAdapter.kt` | Card adapter for admin classes |
| `ui/notifications/NotificationsAdapter.kt` | Adapter for notification list |

### UI Files — Screens (no docs)
| Source File | What It Is |
|-------------|-----------|
| `ui/profile/SettingsFragment.kt` | App settings: dark mode toggle, biometric lock toggle, about |
| `ui/onboarding/OnboardingFragment.kt` | First-launch onboarding walkthrough carousel |
| `ui/onboarding/OnboardingAdapter.kt` | ViewPager2 adapter for onboarding pages |
| `ui/permissions/PermissionsActivity.kt` | Runtime permission request screen (camera, BLE, notifications) |
| `ui/classes/feed/FeedFragment.kt` | Feed tab: announcements + materials in a class |
| `ui/classes/feed/FeedViewModel.kt` | ViewModel for feed tab |
| `ui/classes/feed/FeedAdapter.kt` | Adapter for feed items (announcements + materials) |
| `ui/classes/feed/PostAnnouncementDialog.kt` | Dialog for teachers to post announcements |
| `ui/webview/ClassConnectJSInterface.kt` | JavaScript bridge for WebView |
| `ui/admin/RoleChangeHistoryViewModel.kt` | ViewModel for role change history screen |
| `ui/admin/AdminClassesViewModel.kt` | ViewModel for admin classes screen |

### Data/Repository Files (no docs)
| Source File | What It Is |
|-------------|-----------|
| `data/repository/GeminiRepository.kt` | Wraps GeminiApiService with system prompt and error handling |
| `data/repository/StorageRepository.kt` | Firebase Storage upload/download for photos, submissions, materials |
| `data/repository/FeedRepository.kt` | Firestore CRUD for announcements and materials |
| `data/remote/GeminiModels.kt` | Retrofit data classes for Gemini API request/response |
| `data/model/Feed.kt` | Contains `Announcement` and `Material` data classes (combined file) |

### Utility Files (no docs)
| Source File | What It Is |
|-------------|-----------|
| `util/BiometricHelper.kt` | Biometric authentication helper (fingerprint/face lock) |

### Test Files (no docs)
| Source File | What It Is |
|-------------|-----------|
| `test/DateUtilsTest.kt` | Unit tests for DateUtils |
| `test/AuthRepositoryTest.kt` | Unit tests for AuthRepository |
| `test/ExampleUnitTest.kt` | Boilerplate test |
| `androidTest/LoginFragmentTest.kt` | Instrumented test for LoginFragment |
| `androidTest/ClassListFragmentTest.kt` | Instrumented test for ClassListFragment |
| `androidTest/HiltTestRunner.kt` | Custom test runner for Hilt |
| `androidTest/ExampleInstrumentedTest.kt` | Boilerplate test |

---

## ══════════════════════════════════════════════════
## SECTION 3: Features NOT YET Implemented in Source Code
## ══════════════════════════════════════════════════

These are features/behaviors that should exist based on the app's design but are missing or incomplete:

### 🔴 Critical — Missing Core Features

| # | Feature | Expected Location | Current Status |
|---|---------|------------------|----------------|
| 1 | **AppLifecycleObserver** | `sensor/AppLifecycleObserver.kt` | ❌ Not created. Needed for biometric lock timing — detecting when app goes to background and returns. `BiometricHelper.kt` exists but the observer that triggers it on lifecycle changes does not. |
| 2 | **QR Code Analyzer (CameraX)** | `ui/attendance/QrCodeAnalyzer.kt` | ❌ Not created. Attendance currently uses ZXing's `IntentIntegrator` which opens a separate Activity for scanning. A proper CameraX `ImageAnalysis.Analyzer` implementation would provide an in-app camera preview with real-time QR scanning embedded in AttendanceFragment. |
| 3 | **Submission File Upload** | `ui/assignments/AssignmentDetailFragment.kt` | ⚠️ Partial — Text submissions work, but file-based submissions (PDF, image upload to Firebase Storage) may not be fully wired. `StorageRepository.kt` exists but the file picker integration in `AssignmentDetailFragment` needs verification. |
| 4 | **Offline Support / Caching** | Throughout app | ⚠️ Firestore has built-in offline caching, but there is no explicit offline indicator UI, no offline-first architecture, and no manual cache management. Users get silent failures on network errors. |

### 🟡 Medium — Missing Polish Features

| # | Feature | Expected Location | Current Status |
|---|---------|------------------|----------------|
| 5 | **Class Settings Bottom Sheet** | `ui/classes/ClassSettingsBottomSheet.kt` | ❌ Not implemented. Teachers cannot edit class name, description, schedule, or color after creation. Cannot delete a class from the app (only admin can manage classes). |
| 6 | **Chat Activity (standalone)** | `ui/chat/ChatActivity.kt` | ❌ Not implemented. Chat only works inside ClassDetailActivity tab. No direct deep-link to chat from notifications. If a chat notification is tapped, it opens ClassDetailActivity but doesn't auto-switch to the Chat tab. |
| 7 | **Attendance History Fragment** | `ui/attendance/AttendanceHistoryFragment.kt` | ❌ Not a separate Fragment. History is shown inline within `AttendanceFragment` using `AttendanceHistoryAdapter`, but there is no dedicated detailed history view with per-student attendance tracking. |
| 8 | **Material Upload in Feed** | `ui/classes/feed/` | ⚠️ Partial — `PostAnnouncementDialog` exists for text announcements, but material upload (PDF, video, links) may not have a dedicated upload dialog. The `Material` model exists, `FeedRepository` has methods, but the upload UI flow needs verification. |
| 9 | **Push Notification Deep Links** | `service/MyFirebaseMessagingService.kt` | ⚠️ Partial — FCM service creates notifications but the click intent goes to `MainActivity`. It does NOT deep-link to the specific class/assignment/quiz that triggered the notification. |
| 10 | **Quiz Timer Persistence** | `ui/quiz/QuizAttemptActivity.kt` | ⚠️ If the app is killed during a quiz, the timer state is lost. No `onSaveInstanceState()` or Firestore backup of partial progress. Student would need to restart the quiz. |

### 🟢 Low — Nice-to-Have Missing Features

| # | Feature | Expected Location | Current Status |
|---|---------|------------------|----------------|
| 11 | **Submission Attachment Download** | `ui/assignments/` | ⚠️ Teachers can see submissions but downloading file attachments may not have a dedicated UI flow. |
| 12 | **Chat Media Messages** | `ui/chat/ChatFragment.kt` | ❌ `ChatMessage` model has `attachmentUrl` and `attachmentType` fields, but the UI only supports text messages. No image/file sending in chat. |
| 13 | **Admin User Delete** | `ui/admin/UserDetailFragment.kt` | ⚠️ Admin can manage roles but deleting a user from Firebase Auth (not just Firestore) requires Admin SDK or Cloud Functions, which are not deployed. |
| 14 | **Cloud Functions** | Backend | ❌ No Firebase Cloud Functions deployed. Needed for: server-side FCM push sending, admin user deletion, automatic notifications on data changes. Currently all notifications are created client-side. |
| 15 | **Timetable Widget Data Refresh** | `widget/TimetableWidget.kt` | ⚠️ Widget exists but the data refresh mechanism (SharedPreferences caching from main app) needs verification. Widget may show stale data. |
| 16 | **ProGuard/R8 Rules** | `proguard-rules.pro` | ⚠️ ProGuard rules file exists but may not have proper keep rules for Firebase, Retrofit, Gson model classes. Release builds could crash due to obfuscation. |

---

## ══════════════════════════════════════════════════
## SECTION 4: Architecture Gaps
## ══════════════════════════════════════════════════

| Gap | Description | Impact |
|-----|-------------|--------|
| **No interface abstraction for repositories** | All repositories are concrete classes (`AuthRepository`, `ClassRepository`, etc.). No interface + implementation split. | Testing requires real Firebase instances. Cannot easily swap with fakes for unit tests. |
| **Mixed LiveData and StateFlow** | Some ViewModels use `LiveData` (HomeViewModel, AssignmentsViewModel), others use `StateFlow` (AuthViewModel for registration). | Inconsistent observation patterns in Fragments. Should standardize on one. |
| **Direct Firestore access in some ViewModels** | `StudentsViewModel` queries Firestore directly instead of going through a repository. | Breaks the repository pattern. Harder to test and refactor. |
| **CoroutineScope in Activity** | `ClassDetailActivity` uses `CoroutineScope(Dispatchers.Main)` instead of `lifecycleScope`. | Potential memory leak — the scope is never cancelled if the Activity is destroyed during the coroutine. |
| **No error boundary / global error handler** | Exceptions in coroutines are caught per-call with try/catch. No global `CoroutineExceptionHandler`. | Unhandled exceptions in nested coroutines could crash the app silently. |
| **Hardcoded strings in Kotlin** | Some user-facing strings are hardcoded in Kotlin rather than `strings.xml`. | Cannot be translated / localized. |

---

## ══════════════════════════════════════════════════
## SECTION 5: Implementation Priority Roadmap
## ══════════════════════════════════════════════════

### Phase 1 — Critical Missing (Should exist for app to work properly)
1. ✅ Create `AppLifecycleObserver.kt` for biometric lock timing
2. ✅ Add deep-link handling in `MyFirebaseMessagingService` for notification tap → correct screen
3. ✅ Wire file upload in `AssignmentDetailFragment` to `StorageRepository`
4. ✅ Add `ClassSettingsBottomSheet` for editing class after creation

### Phase 2 — Improve Existing Features
5. ✅ Replace ZXing IntentIntegrator with CameraX `QrCodeAnalyzer` for in-app QR scanning
6. ✅ Add chat media support (images/files) — UI for sending + displaying
7. ✅ Quiz timer persistence via `onSaveInstanceState()` or Firestore partial save
8. ✅ Material upload dialog in Feed tab

### Phase 3 — Architecture Improvements
9. ✅ Extract repository interfaces for testability
10. ✅ Standardize on StateFlow throughout all ViewModels
11. ✅ Replace `CoroutineScope(Dispatchers.Main)` with `lifecycleScope` in ClassDetailActivity
12. ✅ Move hardcoded strings to `strings.xml`

### Phase 4 — Backend & Polish
13. ✅ Deploy Firebase Cloud Functions for server-side notifications and admin user deletion
14. ✅ Add ProGuard keep rules for all model classes, Retrofit interfaces, and Firebase models
15. ✅ Implement offline indicator UI (banner when no network)
16. ✅ Add comprehensive unit and integration tests

---

## ══════════════════════════════════════════════════
## SECTION 6: File-by-File Implementation Status
## ══════════════════════════════════════════════════

### Legend
- ✅ = Fully implemented and exists in source
- ⚠️ = Exists but partially implemented or needs work
- ❌ = Does not exist in source code

### Data Models
| File | Status | Notes |
|------|--------|-------|
| `User.kt` | ✅ | |
| `ClassRoom.kt` | ✅ | |
| `Assignment.kt` | ✅ | Contains both Assignment and Submission |
| `Quiz.kt` | ✅ | Contains Quiz, QuizQuestion, QuizAttempt |
| `Attendance.kt` | ✅ | Contains AttendanceRecord |
| `ChatMessage.kt` | ✅ | Has attachmentUrl/attachmentType fields but no UI uses them |
| `Feed.kt` | ✅ | Contains Announcement and Material |
| `Institution.kt` | ✅ | |
| `AppNotification.kt` | ✅ | |
| `RoleChangeLog.kt` | ✅ | |
| `NewsArticle.kt` | ✅ | Contains NewsArticle, NewsSource, NewsResponse |
| `NetworkResult.kt` | ✅ | In util/ package |

### Repositories
| File | Status | Notes |
|------|--------|-------|
| `AuthRepository.kt` | ✅ | |
| `ClassRepository.kt` | ✅ | |
| `AssignmentRepository.kt` | ✅ | |
| `QuizRepository.kt` | ✅ | |
| `AttendanceRepository.kt` | ✅ | |
| `ChatRepository.kt` | ✅ | |
| `NotificationRepository.kt` | ✅ | |
| `FeedRepository.kt` | ✅ | **No doc page** |
| `GeminiRepository.kt` | ✅ | **No doc page** |
| `StorageRepository.kt` | ✅ | **No doc page** |

### Auth UI
| File | Status | Notes |
|------|--------|-------|
| `SplashActivity.kt` | ✅ | |
| `AuthActivity.kt` | ✅ | **No doc page** |
| `LoginFragment.kt` | ✅ | |
| `RegisterFragment.kt` | ✅ | |
| `ForgotPasswordFragment.kt` | ✅ | |
| `PendingApprovalFragment.kt` | ✅ | |
| `EmailVerificationWaitFragment.kt` | ✅ | |
| `AuthViewModel.kt` | ✅ | |
| `EmailVerificationViewModel.kt` | ✅ | |

### Home UI
| File | Status | Notes |
|------|--------|-------|
| `StudentHomeFragment.kt` | ✅ | |
| `TeacherHomeFragment.kt` | ✅ | |
| `HomeViewModel.kt` | ✅ | |
| `TodayClassesAdapter.kt` | ✅ | **No doc page** |
| `UpcomingDeadlinesAdapter.kt` | ✅ | **No doc page** |
| `RecentAnnouncementsAdapter.kt` | ✅ | **No doc page** |
| `NewsAdapter.kt` | ✅ | **No doc page** |

### Classes UI
| File | Status | Notes |
|------|--------|-------|
| `ClassListFragment.kt` | ✅ | |
| `ClassDetailActivity.kt` | ✅ | |
| `CreateClassBottomSheet.kt` | ✅ | |
| `JoinClassBottomSheet.kt` | ✅ | |
| `ClassSettingsBottomSheet.kt` | ❌ | **Not implemented** — cannot edit class after creation |
| `StudentsFragment.kt` | ✅ | |
| `ClassViewModel.kt` | ✅ | |
| `StudentsViewModel.kt` | ✅ | |
| `ClassTabAdapter.kt` | ✅ | **No doc page** |
| `ClassAdapter.kt` | ✅ | **No doc page** |

### Feed UI (Undocumented subsystem)
| File | Status | Notes |
|------|--------|-------|
| `FeedFragment.kt` | ✅ | **No doc page** — Announcements + Materials tab |
| `FeedViewModel.kt` | ✅ | **No doc page** |
| `FeedAdapter.kt` | ✅ | **No doc page** |
| `PostAnnouncementDialog.kt` | ✅ | **No doc page** |

### Assignments UI
| File | Status | Notes |
|------|--------|-------|
| `AssignmentsFragment.kt` | ✅ | |
| `CreateAssignmentFragment.kt` | ✅ | |
| `AssignmentDetailFragment.kt` | ✅ | ⚠️ File upload may be partial |
| `SubmissionListFragment.kt` | ✅ | |
| `GradeSubmissionFragment.kt` | ✅ | |
| `AssignmentsViewModel.kt` | ✅ | |
| `AssignmentsAdapter.kt` | ✅ | **No doc page** |

### Quiz UI
| File | Status | Notes |
|------|--------|-------|
| `QuizListFragment.kt` | ✅ | |
| `CreateQuizFragment.kt` | ✅ | |
| `QuizAttemptActivity.kt` | ✅ | ⚠️ No timer persistence on kill |
| `QuizResultFragment.kt` | ✅ | |
| `QuizResultsFragment.kt` | ✅ | |
| `QuizViewModel.kt` | ✅ | |
| `QuizAdapter.kt` | ✅ | **No doc page** |
| `QuizAttemptsAdapter.kt` | ✅ | **No doc page** |
| `AddQuestionDialog.kt` | ✅ | **No doc page** |

### Attendance UI
| File | Status | Notes |
|------|--------|-------|
| `AttendanceFragment.kt` | ✅ | |
| `AttendanceHistoryFragment.kt` | ❌ | **Not a separate Fragment** — inline in AttendanceFragment |
| `AttendanceViewModel.kt` | ✅ | |
| `AttendanceHistoryAdapter.kt` | ✅ | **No doc page** |
| `QrCodeAnalyzer.kt` | ❌ | **Not implemented** — uses ZXing IntentIntegrator instead |

### Chat UI
| File | Status | Notes |
|------|--------|-------|
| `ChatActivity.kt` | ❌ | **Not implemented** — chat is a Fragment tab, not standalone |
| `ChatFragment.kt` | ✅ | ⚠️ No media message support |
| `ChatViewModel.kt` | ✅ | |
| `ChatAdapter.kt` | ✅ | |

### AI UI
| File | Status | Notes |
|------|--------|-------|
| `AIBuddyFragment.kt` | ✅ | |
| `LessonPlannerFragment.kt` | ✅ | |
| `AIViewModel.kt` | ✅ | |
| `AIChatAdapter.kt` | ✅ | **No doc page** |

### Notifications UI
| File | Status | Notes |
|------|--------|-------|
| `NotificationsFragment.kt` | ✅ | |
| `NotificationsViewModel.kt` | ✅ | |
| `NotificationsAdapter.kt` | ✅ | **No doc page** |
| `MyFirebaseMessagingService.kt` | ✅ | ⚠️ No deep-link on tap |

### Profile UI
| File | Status | Notes |
|------|--------|-------|
| `ProfileFragment.kt` | ✅ | |
| `ProfileViewModel.kt` | ✅ | |
| `SettingsFragment.kt` | ✅ | **No doc page** |

### Admin UI
| File | Status | Notes |
|------|--------|-------|
| `AdminDashboardFragment.kt` | ✅ | |
| `UserManagementFragment.kt` | ✅ | |
| `UserDetailFragment.kt` | ✅ | |
| `AdminClassesFragment.kt` | ✅ | |
| `AssignTeacherBottomSheet.kt` | ✅ | |
| `RoleChangeHistoryFragment.kt` | ✅ | |
| `AdminViewModel.kt` | ✅ | |
| `UserDetailViewModel.kt` | ✅ | |
| `RoleChangeHistoryViewModel.kt` | ✅ | **No doc page** |
| `AdminClassesViewModel.kt` | ✅ | |
| `UserManagementAdapter.kt` | ✅ | **No doc page** |
| `RoleChangeLogAdapter.kt` | ✅ | **No doc page** |
| `AssignTeacherAdapter.kt` | ✅ | **No doc page** |
| `AdminClassAdapter.kt` | ✅ | **No doc page** |
| `AdminClassCardAdapter.kt` | ✅ | **No doc page** |

### Other UI
| File | Status | Notes |
|------|--------|-------|
| `WebViewActivity.kt` | ✅ | |
| `ClassConnectJSInterface.kt` | ✅ | **No doc page** |
| `OnboardingFragment.kt` | ✅ | **No doc page** |
| `OnboardingAdapter.kt` | ✅ | **No doc page** |
| `PermissionsActivity.kt` | ✅ | |
| `MainActivity.kt` | ✅ | **No doc page** |

### Sensors & Widget
| File | Status | Notes |
|------|--------|-------|
| `SensorHandler.kt` | ✅ | |
| `AppLifecycleObserver.kt` | ❌ | **Not implemented** |
| `TimetableWidget.kt` | ✅ | ⚠️ Data refresh mechanism needs verification |

### DI
| File | Status | Notes |
|------|--------|-------|
| `AppModule.kt` | ✅ | |
| `RepositoryModule.kt` | ✅ | |

### Utilities
| File | Status | Notes |
|------|--------|-------|
| `Extensions.kt` | ✅ | |
| `Constants.kt` | ✅ | |
| `ValidationUtils.kt` | ✅ | |
| `DateUtils.kt` | ✅ | |
| `NetworkUtils.kt` | ✅ | |
| `NetworkResult.kt` | ✅ | |
| `PermissionManager.kt` | ✅ | Actually `PermissionsActivity.kt` |
| `BiometricHelper.kt` | ✅ | **No doc page** |

### App Entry
| File | Status | Notes |
|------|--------|-------|
| `ClassConnectApp.kt` | ✅ | **No doc page** — `@HiltAndroidApp` Application class |

---

## Quick Reference: What to Build Next

### Must Build (app incomplete without these):
1. `AppLifecycleObserver.kt` — biometric lock lifecycle detection
2. `ClassSettingsBottomSheet.kt` — edit/delete class after creation
3. FCM notification deep-linking (tap notification → correct screen)
4. File upload wiring in AssignmentDetailFragment

### Should Build (improves UX significantly):
5. CameraX `QrCodeAnalyzer` for in-app QR scanning
6. Chat media messages (image/file sending)
7. Dedicated `AttendanceHistoryFragment` with per-student tracking
8. Material upload dialog in Feed

### Nice to Have:
9. Cloud Functions for server-side operations
10. Offline indicator UI
11. Quiz timer persistence
12. Comprehensive test suite

