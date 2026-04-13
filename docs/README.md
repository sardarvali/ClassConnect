# ClassConnect — Full Project Documentation

---

## What is ClassConnect?

ClassConnect is a comprehensive Android educational platform that connects students, teachers, and administrators. It provides classroom management, assignments, quizzes, real-time chat, QR-based attendance, AI-powered study assistance, and push notifications — all in a single app backed by Firebase.

---

## Who Uses It?

ClassConnect has three user roles with different permissions:

| Feature | Student | Teacher | Admin |
|---------|---------|---------|-------|
| Join classes | ✅ (by code) | ❌ | ❌ |
| Create classes | ❌ | ✅ | ✅ |
| View assignments | ✅ | ✅ | ✅ |
| Create assignments | ❌ | ✅ | ✅ |
| Submit assignments | ✅ | ❌ | ❌ |
| Grade submissions | ❌ | ✅ | ✅ |
| Take quizzes | ✅ | ❌ | ❌ |
| Create quizzes | ❌ | ✅ | ✅ |
| View quiz results | Own only | All students | All students |
| Mark attendance (QR) | ✅ (scan) | ✅ (generate) | ✅ (generate) |
| Class chat | ✅ | ✅ (+ clear chat) | ✅ (+ clear chat) |
| AI Study Buddy | ✅ | ✅ | ✅ |
| Lesson Planner (AI) | ❌ | ✅ | ✅ |
| Manage users | ❌ | ❌ | ✅ |
| Approve/reject users | ❌ | ❌ | ✅ |
| Change user roles | ❌ | ❌ | ✅ |
| Create institutions | ❌ | ❌ | ✅ |

---

## How the App Works — 5-Minute Overview

1. **Registration**: Users sign up as student or teacher. Two paths available: institution (needs admin approval) or independent (needs email verification).
2. **Home Screen**: Role-specific dashboard — today's classes, upcoming deadlines (students), news, quick actions.
3. **Classes**: Teachers create classes with a unique 6-character code. Students join using that code. Each class has tabs: Feed, Assignments, Quizzes, Attendance, Students, Chat.
4. **Assignments**: Teachers create, students submit text or files, teachers grade with feedback.
5. **Quizzes**: Teachers create MCQs (manually or AI-generated), students take timed quizzes, automatic scoring.
6. **Attendance**: Teachers generate a QR code; students scan to check in. QR expires after 5 minutes.
7. **Chat**: Real-time messaging within each class. Supports reactions, deletion, and teacher moderation.
8. **AI Features**: Study Buddy (conversational AI for learning), Lesson Planner (generates lesson plans), Quiz Generator (AI creates questions).
9. **Admin**: Manages institution users, approves registrations, changes roles, assigns teachers to classes.
10. **Notifications**: In-app + push notifications for assignments, quizzes, chat, announcements.

---

## Registration: Two Paths Explained

```
User opens app for first time
         │
         ▼
    RegisterFragment
         │
    ┌────┴─────────────┐
    │ Institution code  │
    │ provided?         │
    └──┬─────────────┬──┘
      YES            NO
       │              │
       ▼              ▼
  Validate code   Create account
  → Create account → Send verification email
  → isApproved:false → EmailVerificationWaitFragment
       │              │
       ▼         User taps link in email
  PendingApproval     │
  Fragment            ▼
       │         Firebase isEmailVerified = true
       │              │
  Admin approves       ▼
  in dashboard    Auto-approve:
       │          isApproved = true
       ▼              │
  FCM notification     ▼
  sent to user    Navigate to MainActivity
       │
       ▼
  Navigate to MainActivity
```

**Path A (Institution)**: User enters institution code → account created with `isApproved=false` → waits on PendingApprovalFragment → admin approves → user can access app.

**Path B (Independent)**: User registers without code → verification email sent → user clicks link → app auto-approves → user can access app.

---

## Architecture Overview

```
┌─────────────────────────────────────────────────────┐
│                    VIEW LAYER                        │
│         Fragments + Activities + Adapters            │
│  (Only: show data, capture input, navigate)          │
└──────────────────┬──────────────────────────────────┘
                   │ observes StateFlow / LiveData
                   │ calls ViewModel functions
┌──────────────────▼──────────────────────────────────┐
│                 VIEWMODEL LAYER                      │
│    (Business logic, state management, coroutines)    │
│    Survives screen rotation. Never references View.  │
└──────────────────┬──────────────────────────────────┘
                   │ calls suspend functions
                   │ collects Flow
┌──────────────────▼──────────────────────────────────┐
│                REPOSITORY LAYER                      │
│     (All data: Firestore, Retrofit, SharedPrefs)     │
│     Returns: Flow<T> for real-time, Result<T> once   │
└──────────┬─────────────────────────┬────────────────┘
           │                         │
┌──────────▼──────────┐   ┌──────────▼──────────────┐
│  Firebase           │   │  Retrofit (REST APIs)    │
│  Auth, Firestore,   │   │  Gemini AI, NewsAPI      │
│  Storage, FCM       │   │                          │
└─────────────────────┘   └──────────────────────────┘
```

See [setup/ARCHITECTURE.md](setup/ARCHITECTURE.md) for detailed architecture documentation.

---

## Complete Screen Map

| Screen | Role | Purpose |
|--------|------|---------|
| SplashActivity | All | Entry point, routing logic |
| LoginFragment | All | Email/password + Google Sign-In |
| RegisterFragment | All | Dual-path registration |
| ForgotPasswordFragment | All | Password reset |
| PendingApprovalFragment | All | Wait for admin approval (Path A) |
| EmailVerificationWaitFragment | All | Wait for email verification (Path B) |
| StudentHomeFragment | Student | Dashboard: today's classes, deadlines, news |
| TeacherHomeFragment | Teacher | Dashboard: schedule, quick actions, news |
| AdminDashboardFragment | Admin | Stats, pending approvals, management |
| ClassListFragment | Student/Teacher | List of enrolled/owned classes |
| ClassDetailActivity | All | Tabbed class hub (6 tabs) |
| AssignmentsFragment | All | Assignment list per class |
| CreateAssignmentFragment | Teacher | Create new assignment |
| AssignmentDetailFragment | All | View/submit assignment |
| SubmissionListFragment | Teacher | View all submissions |
| GradeSubmissionFragment | Teacher | Grade a submission |
| QuizListFragment | All | Quiz list per class |
| CreateQuizFragment | Teacher | Create quiz (manual + AI) |
| QuizAttemptActivity | Student | Take a quiz with timer |
| QuizResultFragment | Student | View quiz results |
| QuizResultsFragment | Teacher | View all student results |
| AttendanceFragment | All | QR generate (teacher) / scan (student) |
| ChatFragment | All | Real-time class messaging |
| AIBuddyFragment | All | AI study assistant chat |
| LessonPlannerFragment | Teacher | AI lesson plan generator |
| NotificationsFragment | All | Notification history |
| ProfileFragment | All | View/edit profile |
| SettingsFragment | All | App settings (theme, biometric) |
| UserManagementFragment | Admin | Manage institution users |
| UserDetailFragment | Admin | View/edit user, change role |
| RoleChangeHistoryFragment | Admin | Role change audit trail |
| WebViewActivity | All | In-app browser |

---

## Key Technologies

| Technology | What | Why |
|-----------|------|-----|
| Kotlin | Programming language | Modern, null-safe, concise |
| Firebase Auth | Authentication | Email/password + Google Sign-In |
| Cloud Firestore | Database | Real-time NoSQL, offline support |
| Firebase Storage | File storage | Profile photos, submissions |
| Firebase Cloud Messaging | Push notifications | Assignment/chat alerts |
| Firebase Crashlytics | Crash reporting | Production debugging |
| Hilt | Dependency injection | Testable, loosely coupled code |
| Retrofit + OkHttp | HTTP client | Gemini AI + NewsAPI calls |
| Kotlin Coroutines + Flow | Async programming | Non-blocking, lifecycle-aware |
| ViewBinding | View access | Type-safe, null-safe XML access |
| Material Design 3 | UI components | Modern Android UI |
| Gemini 2.0 Flash | AI API | Study buddy, lesson plans, quiz gen |
| ZXing + ML Kit | QR codes | Attendance scanning |
| MPAndroidChart | Charts | Quiz statistics visualization |
| Glide | Image loading | Profile photos, news thumbnails |
| Markwon | Markdown rendering | AI response formatting |

---

## How Data Flows — End-to-End Example

**Student submits an assignment:**

```
1. Student opens AssignmentDetailFragment
2. Student types answer OR selects file
3. Taps "Submit"
4. AssignmentDetailFragment → viewModel.submitAssignment(classId, assignmentId, submission)
5. AssignmentsViewModel → viewModelScope.launch { assignmentRepository.submitAssignment(...) }
6. AssignmentRepository:
   a. If file: StorageRepository.uploadSubmission(classId, assignmentId, studentId, uri)
      → Firebase Storage upload → returns download URL
   b. Creates Submission document in Firestore
      → /classes/{classId}/assignments/{assignmentId}/submissions/{studentId}
   c. Returns Result.success(Unit)
7. ViewModel updates _submitResult = NetworkResult.Success(Unit)
8. Fragment observes submitResult → shows "Submitted successfully" → navigates back
9. Teacher's SubmissionListFragment receives real-time update (Firestore listener)
```

---

## Documentation Index

### Setup
| File | Description |
|------|-------------|
| [setup/SETUP.md](setup/SETUP.md) | Complete project setup guide |
| [setup/ARCHITECTURE.md](setup/ARCHITECTURE.md) | MVVM + Repository pattern explained |
| [setup/DEPENDENCIES.md](setup/DEPENDENCIES.md) | Every Gradle dependency explained |

### App Entry Points
| File | Description |
|------|-------------|
| [app-entry/ClassConnectApp.md](app-entry/ClassConnectApp.md) | `@HiltAndroidApp` Application class, Timber, Crashlytics, notification channels |
| [app-entry/MainActivity.md](app-entry/MainActivity.md) | Main nav host, role-based bottom nav, biometric lock, FCM token, notification badge |
| [app-entry/AuthActivity.md](app-entry/AuthActivity.md) | Auth nav host, intent-based routing to login/pending/verify screens |

### Authentication
| File | Description |
|------|-------------|
| [auth/SplashActivity.md](auth/SplashActivity.md) | App entry point with routing logic |
| [auth/LoginFragment.md](auth/LoginFragment.md) | Email/password + Google Sign-In |
| [auth/RegisterFragment.md](auth/RegisterFragment.md) | Dual-path registration |
| [auth/ForgotPasswordFragment.md](auth/ForgotPasswordFragment.md) | Password reset |
| [auth/PendingApprovalFragment.md](auth/PendingApprovalFragment.md) | Institution approval wait screen |
| [auth/EmailVerificationWaitFragment.md](auth/EmailVerificationWaitFragment.md) | Email verification polling |
| [auth/AuthRepository.md](auth/AuthRepository.md) | All auth data operations |
| [auth/AuthViewModel.md](auth/AuthViewModel.md) | Shared auth VM: login, register, Google sign-in, post-login routing |
| [auth/LoginViewModel.md](auth/LoginViewModel.md) | See AuthViewModel |
| [auth/RegisterViewModel.md](auth/RegisterViewModel.md) | See AuthViewModel |
| [auth/EmailVerificationViewModel.md](auth/EmailVerificationViewModel.md) | Email verification polling ViewModel |

### Data Models
| File | Description |
|------|-------------|
| [models/User.md](models/User.md) | User data class |
| [models/ClassModel.md](models/ClassModel.md) | ClassRoom data class |
| [models/Assignment.md](models/Assignment.md) | Assignment + Submission data classes |
| [models/Submission.md](models/Submission.md) | Submission reference |
| [models/Quiz.md](models/Quiz.md) | Quiz + QuizQuestion + QuizAttempt |
| [models/QuizQuestion.md](models/QuizQuestion.md) | QuizQuestion reference |
| [models/QuizAttempt.md](models/QuizAttempt.md) | QuizAttempt reference |
| [models/AttendanceSession.md](models/AttendanceSession.md) | AttendanceRecord data class |
| [models/ChatMessage.md](models/ChatMessage.md) | ChatMessage data class |
| [models/Announcement.md](models/Announcement.md) | Announcement data class |
| [models/Material.md](models/Material.md) | Material data class |
| [models/Institution.md](models/Institution.md) | Institution data class |
| [models/Notification.md](models/Notification.md) | AppNotification data class |
| [models/RoleChangeLog.md](models/RoleChangeLog.md) | Role change audit log |
| [models/NewsArticle.md](models/NewsArticle.md) | NewsAPI response models |
| [models/NetworkResult.md](models/NetworkResult.md) | Sealed class for state management |

### Home Screens
| File | Description |
|------|-------------|
| [home/StudentHomeFragment.md](home/StudentHomeFragment.md) | Student dashboard |
| [home/TeacherHomeFragment.md](home/TeacherHomeFragment.md) | Teacher dashboard |
| [home/AdminDashboardFragment.md](home/AdminDashboardFragment.md) | Admin dashboard |
| [home/HomeViewModel.md](home/HomeViewModel.md) | Shared home ViewModel — today classes, deadlines, announcements, news |
| [home/StudentHomeViewModel.md](home/StudentHomeViewModel.md) | See HomeViewModel |
| [home/TeacherHomeViewModel.md](home/TeacherHomeViewModel.md) | See HomeViewModel |
| [home/AdminDashboardViewModel.md](home/AdminDashboardViewModel.md) | See AdminViewModel |
| [home/TodayClassesAdapter.md](home/TodayClassesAdapter.md) | Horizontal gradient cards for today's timetable |
| [home/UpcomingDeadlinesAdapter.md](home/UpcomingDeadlinesAdapter.md) | Color-coded upcoming deadlines list |
| [home/RecentAnnouncementsAdapter.md](home/RecentAnnouncementsAdapter.md) | Recent announcements on student home |
| [home/NewsAdapter.md](home/NewsAdapter.md) | Education news cards on teacher home |

### Classes
| File | Description |
|------|-------------|
| [classes/ClassListFragment.md](classes/ClassListFragment.md) | Class list screen |
| [classes/ClassDetailActivity.md](classes/ClassDetailActivity.md) | Tabbed class hub |
| [classes/CreateClassBottomSheet.md](classes/CreateClassBottomSheet.md) | Create class form |
| [classes/JoinClassBottomSheet.md](classes/JoinClassBottomSheet.md) | Join class by code |
| [classes/ClassSettingsBottomSheet.md](classes/ClassSettingsBottomSheet.md) | Class settings (edit/delete/leave) |
| [classes/StudentsFragment.md](classes/StudentsFragment.md) | Student list |
| [classes/ClassAdapter.md](classes/ClassAdapter.md) | Gradient class card RecyclerView adapter |
| [classes/ClassTabAdapter.md](classes/ClassTabAdapter.md) | ViewPager2 FragmentStateAdapter for class tabs |
| [classes/StudentsAdapter.md](classes/StudentsAdapter.md) | Enrolled students list adapter |
| [classes/ClassListViewModel.md](classes/ClassListViewModel.md) | ClassViewModel |
| [classes/ClassDetailViewModel.md](classes/ClassDetailViewModel.md) | See ClassViewModel |
| [classes/StudentsViewModel.md](classes/StudentsViewModel.md) | Student list ViewModel |
| [classes/ClassRepository.md](classes/ClassRepository.md) | Class data operations |

### Feed (Class Announcements)
| File | Description |
|------|-------------|
| [classes/feed/FeedFragment.md](classes/feed/FeedFragment.md) | Real-time feed tab inside ClassDetailActivity |
| [classes/feed/FeedViewModel.md](classes/feed/FeedViewModel.md) | Loads, sorts, and manages class announcements |
| [classes/feed/FeedAdapter.md](classes/feed/FeedAdapter.md) | Announcements list with pin toggle |
| [classes/feed/PostAnnouncementDialog.md](classes/feed/PostAnnouncementDialog.md) | Teacher bottom sheet for posting announcements |
| [feed/FeedRepository.md](feed/FeedRepository.md) | Firestore real-time Flow for announcements and materials |

### Assignments
| File | Description |
|------|-------------|
| [assignments/AssignmentsFragment.md](assignments/AssignmentsFragment.md) | Assignment list |
| [assignments/CreateAssignmentFragment.md](assignments/CreateAssignmentFragment.md) | Create assignment |
| [assignments/AssignmentDetailFragment.md](assignments/AssignmentDetailFragment.md) | View/submit |
| [assignments/SubmissionListFragment.md](assignments/SubmissionListFragment.md) | All submissions |
| [assignments/GradeSubmissionFragment.md](assignments/GradeSubmissionFragment.md) | Grade submission |
| [assignments/AssignmentsViewModel.md](assignments/AssignmentsViewModel.md) | Assignment ViewModel |
| [assignments/CreateAssignmentViewModel.md](assignments/CreateAssignmentViewModel.md) | See AssignmentsViewModel |
| [assignments/GradeSubmissionViewModel.md](assignments/GradeSubmissionViewModel.md) | See AssignmentsViewModel |
| [assignments/AssignmentsAdapter.md](assignments/AssignmentsAdapter.md) | Color-coded assignment list adapter with urgency chips |
| [assignments/AssignmentRepository.md](assignments/AssignmentRepository.md) | Assignment data ops |

### Quizzes
| File | Description |
|------|-------------|
| [quiz/QuizListFragment.md](quiz/QuizListFragment.md) | Quiz list |
| [quiz/CreateQuizFragment.md](quiz/CreateQuizFragment.md) | Create quiz |
| [quiz/QuizAttemptActivity.md](quiz/QuizAttemptActivity.md) | Take quiz |
| [quiz/QuizResultFragment.md](quiz/QuizResultFragment.md) | Student results |
| [quiz/QuizResultsTeacherFragment.md](quiz/QuizResultsTeacherFragment.md) | Teacher results |
| [quiz/QuizListViewModel.md](quiz/QuizListViewModel.md) | QuizViewModel |
| [quiz/CreateQuizViewModel.md](quiz/CreateQuizViewModel.md) | See QuizViewModel |
| [quiz/QuizAttemptViewModel.md](quiz/QuizAttemptViewModel.md) | See QuizViewModel |
| [quiz/QuizAdapter.md](quiz/QuizAdapter.md) | Quiz list adapter with Published/Draft chip |
| [quiz/QuizAttemptsAdapter.md](quiz/QuizAttemptsAdapter.md) | Teacher view: per-student attempt results with color-coded % |
| [quiz/AddQuestionDialog.md](quiz/AddQuestionDialog.md) | AlertDialog for adding MCQ questions |
| [quiz/QuizRepository.md](quiz/QuizRepository.md) | Quiz data operations |

### Attendance
| File | Description |
|------|-------------|
| [attendance/AttendanceFragment.md](attendance/AttendanceFragment.md) | QR attendance |
| [attendance/AttendanceHistoryFragment.md](attendance/AttendanceHistoryFragment.md) | History view |
| [attendance/AttendanceViewModel.md](attendance/AttendanceViewModel.md) | Attendance ViewModel |
| [attendance/AttendanceHistoryViewModel.md](attendance/AttendanceHistoryViewModel.md) | See AttendanceViewModel |
| [attendance/AttendanceHistoryAdapter.md](attendance/AttendanceHistoryAdapter.md) | Per-session present/absent count rows |
| [attendance/AttendanceRepository.md](attendance/AttendanceRepository.md) | Attendance data ops |
| [attendance/AttendanceBleService.md](attendance/AttendanceBleService.md) | BLE service |
| [attendance/QrCodeAnalyzer.md](attendance/QrCodeAnalyzer.md) | CameraX QR code analyzer |

### Chat
| File | Description |
|------|-------------|
| [chat/ChatActivity.md](chat/ChatActivity.md) | Not implemented separately (see ChatFragment) |
| [chat/ChatFragment.md](chat/ChatFragment.md) | Real-time chat |
| [chat/ChatViewModel.md](chat/ChatViewModel.md) | Chat ViewModel |
| [chat/ChatRepository.md](chat/ChatRepository.md) | Chat data operations |
| [chat/ChatAdapter.md](chat/ChatAdapter.md) | Message bubble adapter |

### AI Features
| File | Description |
|------|-------------|
| [ai/AIBuddyFragment.md](ai/AIBuddyFragment.md) | AI study assistant |
| [ai/LessonPlannerFragment.md](ai/LessonPlannerFragment.md) | AI lesson plans |
| [ai/AIViewModel.md](ai/AIViewModel.md) | Shared ViewModel: Study Buddy chat, lesson planner, quiz AI generation |
| [ai/AIBuddyViewModel.md](ai/AIBuddyViewModel.md) | See AIViewModel |
| [ai/LessonPlannerViewModel.md](ai/LessonPlannerViewModel.md) | See AIViewModel |
| [ai/AIChatAdapter.md](ai/AIChatAdapter.md) | Dual-view-type chat adapter with Markdown rendering |
| [ai/GeminiApiService.md](ai/GeminiApiService.md) | Gemini API Retrofit interface |
| [ai/GeminiModels.md](ai/GeminiModels.md) | GeminiRequest/Content/Part/Response data classes |
| [ai/GeminiRepository.md](ai/GeminiRepository.md) | Gemini API calls with conversation history and error handling |
| [ai/NewsApiService.md](ai/NewsApiService.md) | NewsAPI Retrofit interface for teacher home news |
| [ai/RetrofitClient.md](ai/RetrofitClient.md) | Retrofit + OkHttp configuration for Gemini and News |

### Notifications
| File | Description |
|------|-------------|
| [notifications/NotificationsFragment.md](notifications/NotificationsFragment.md) | Notification list |
| [notifications/NotificationsViewModel.md](notifications/NotificationsViewModel.md) | Notification ViewModel |
| [notifications/NotificationsAdapter.md](notifications/NotificationsAdapter.md) | Read/unread notification rows with badge and animation |
| [notifications/NotificationRepository.md](notifications/NotificationRepository.md) | Notification data ops |
| [notifications/MyFirebaseMessagingService.md](notifications/MyFirebaseMessagingService.md) | FCM handler |

### Profile
| File | Description |
|------|-------------|
| [profile/ProfileFragment.md](profile/ProfileFragment.md) | User profile |
| [profile/ProfileViewModel.md](profile/ProfileViewModel.md) | Profile ViewModel |
| [profile/SettingsFragment.md](profile/SettingsFragment.md) | Theme, notifications, biometric lock, app version |

### Onboarding
| File | Description |
|------|-------------|
| [onboarding/OnboardingFragment.md](onboarding/OnboardingFragment.md) | 3-page first-launch intro with page transformer and TabLayout dots |
| [onboarding/OnboardingAdapter.md](onboarding/OnboardingAdapter.md) | RecyclerView adapter for onboarding pages |

### Permissions
| File | Description |
|------|-------------|
| [permissions/PermissionsActivity.md](permissions/PermissionsActivity.md) | One-time permission request screen for camera, BLE, location, notifications |

### Admin
| File | Description |
|------|-------------|
| [admin/UserManagementFragment.md](admin/UserManagementFragment.md) | User management with Pending/Teachers/Students tabs |
| [admin/UserDetailFragment.md](admin/UserDetailFragment.md) | User detail — approve/reject/change role |
| [admin/AdminClassesFragment.md](admin/AdminClassesFragment.md) | Institution classes management |
| [admin/AssignTeacherBottomSheet.md](admin/AssignTeacherBottomSheet.md) | Assign a teacher to a class |
| [admin/RoleChangeHistoryFragment.md](admin/RoleChangeHistoryFragment.md) | Audit trail of role changes |
| [admin/UserManagementViewModel.md](admin/UserManagementViewModel.md) | Alias doc for the shared `AdminViewModel` |
| [admin/AdminViewModel.md](admin/AdminViewModel.md) | Shared admin ViewModel: stats, user management, approvals, role changes |
| [admin/UserDetailViewModel.md](admin/UserDetailViewModel.md) | User detail ViewModel |
| [admin/AdminClassesViewModel.md](admin/AdminClassesViewModel.md) | Dedicated class-management ViewModel |
| [admin/RoleChangeHistoryViewModel.md](admin/RoleChangeHistoryViewModel.md) | Fetches institution roleChangeLogs from Firestore |
| [admin/UserManagementAdapter.md](admin/UserManagementAdapter.md) | User rows with conditional approve/reject buttons |
| [admin/RoleChangeLogAdapter.md](admin/RoleChangeLogAdapter.md) | Role change audit log rows |
| [admin/AssignTeacherAdapter.md](admin/AssignTeacherAdapter.md) | Teacher selection rows with Glide avatar |
| [admin/AdminClassAdapter.md](admin/AdminClassAdapter.md) | Class list rows with unassigned highlighting |
| [admin/AdminClassCardAdapter.md](admin/AdminClassCardAdapter.md) | Alternate class card adapter; currently unused by the app |

### WebView, Sensors, Widget
| File | Description |
|------|-------------|
| [webview/WebViewActivity.md](webview/WebViewActivity.md) | In-app browser |
| [webview/ClassConnectJSInterface.md](webview/ClassConnectJSInterface.md) | JS bridge: `saveToNotes()` and `showToast()` callable from JavaScript |
| [sensors/SensorHandler.md](sensors/SensorHandler.md) | Shake detection sensor |
| [sensors/AppLifecycleObserver.md](sensors/AppLifecycleObserver.md) | Foreground/background detection for biometric lock |
| [widget/TimetableWidget.md](widget/TimetableWidget.md) | Home screen timetable widget |

### Dependency Injection
| File | Description |
|------|-------------|
| [di/AppModule.md](di/AppModule.md) | Hilt DI module — Firebase, Retrofit, NewsApi bindings |
| [di/RepositoryModule.md](di/RepositoryModule.md) | Repository interface → implementation bindings |

### Utilities
| File | Description |
|------|-------------|
| [util/Extensions.md](util/Extensions.md) | View extension functions (`show`, `hide`, `loadAvatar`, `addPressEffect`) |
| [util/Constants.md](util/Constants.md) | App-wide constants: Firestore paths, prefs keys, role names, channels |
| [util/ValidationUtils.md](util/ValidationUtils.md) | Email, password, name input validation |
| [util/DateUtils.md](util/DateUtils.md) | Timestamp formatting, `toDisplayDate`, `toRelativeTime`, `daysUntil` |
| [util/NetworkUtils.md](util/NetworkUtils.md) | Connectivity checking via ConnectivityManager |
| [util/BiometricHelper.md](util/BiometricHelper.md) | Biometric hardware detection and authentication prompt |
| [util/StorageRepository.md](util/StorageRepository.md) | Firebase Storage: upload files/photos/submissions, delete by URL |
| [util/FcmHelper.md](util/FcmHelper.md) | Sends FCM push notifications via Legacy HTTP API (⚠️ should move to Cloud Functions) |
| [util/PermissionManager.md](util/PermissionManager.md) | Runtime permissions helper |

### Firebase
| File | Description |
|------|-------------|
| [firebase/FIRESTORE_SCHEMA.md](firebase/FIRESTORE_SCHEMA.md) | Complete database schema with all collections and fields |
| [firebase/SECURITY_RULES.md](firebase/SECURITY_RULES.md) | Firestore security rules explained |
| [firebase/STORAGE_RULES.md](firebase/STORAGE_RULES.md) | Storage security rules |
| [firebase/FCM_SETUP.md](firebase/FCM_SETUP.md) | Push notification setup guide |

---

## Quick Start

1. **Read [setup/SETUP.md](setup/SETUP.md)** for complete setup instructions
2. **Read [setup/ARCHITECTURE.md](setup/ARCHITECTURE.md)** to understand the codebase structure
3. **Read [setup/DEPENDENCIES.md](setup/DEPENDENCIES.md)** for all library explanations
4. **Browse [models/](models/)** to understand data structures
5. **Read [auth/](auth/)** to understand the authentication flow
6. **Explore feature docs** as needed

---

## ✅ Documentation Status

**All 106 source `.kt` files have documentation coverage** as of March 6, 2026.

Documentation was completed in Prompts 7–9. New files documented in Prompt 9 include:
- **App Entry**: `ClassConnectApp.md`, `MainActivity.md`, `AuthActivity.md`
- **AI**: `AIViewModel.md`, `AIChatAdapter.md`, `GeminiModels.md`, `GeminiRepository.md`, `NewsApiService.md`
- **Admin**: `AdminViewModel.md`, `AdminClassAdapter.md`, `AdminClassCardAdapter.md` (unused alternate), `UserManagementAdapter.md`, `AssignTeacherAdapter.md`, `RoleChangeLogAdapter.md`, `RoleChangeHistoryViewModel.md`, `AdminClassesViewModel.md`
- **Assignments**: `AssignmentsAdapter.md`
- **Quiz**: `QuizAdapter.md`, `QuizAttemptsAdapter.md`, `AddQuestionDialog.md`
- **Attendance**: `AttendanceHistoryAdapter.md`
- **Notifications**: `NotificationsAdapter.md`
- **Profile**: `SettingsFragment.md`
- **Onboarding**: `OnboardingFragment.md`, `OnboardingAdapter.md`
- **Permissions**: `PermissionsActivity.md`
- **WebView**: `ClassConnectJSInterface.md`
- **Sensors**: `SensorHandler.md`, `AppLifecycleObserver.md`
- **Util**: `BiometricHelper.md`, `StorageRepository.md`, `FcmHelper.md`

See **[YET_TO_IMPLEMENT.md](YET_TO_IMPLEMENT.md)** for architecture improvement roadmap and missing feature analysis.

---

*Generated for ClassConnect — March 6, 2026*

