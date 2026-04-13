# ClassConnect - Complete Module Overview & Screen Map

> **Date:** April 10, 2026  
> **Purpose:** Visual guide to all screens and their relationships

---

## 📱 App Navigation Structure

### Entry Point: SplashActivity

```
SplashActivity
│
├─ Checks: Is user logged in?
│  │
│  ├─ YES → Check approval status
│  │  │
│  │  ├─ APPROVED → MainActivity
│  │  │
│  │  └─ PENDING APPROVAL → AuthActivity (PendingApprovalFragment)
│  │
│  └─ NO → AuthActivity (LoginFragment)
│
└─ Fallback timeout (5s) → AuthActivity
```

---

## 🔐 AUTH MODULE - Complete Flow

**File Path**: `app/src/main/java/com/syed/classconnect/ui/auth/`

### Screens (Components)

#### 1. LoginFragment
- **Purpose**: Email & password login
- **Fields**: Email, Password
- **Actions**: Sign In, Forgot Password, Register
- **Next Screens**: Home (if approved) OR PendingApprovalFragment

#### 2. RegisterFragment
- **Purpose**: Create new account
- **Fields**: Name, Email, Password, Confirm Password, Role
- **Actions**: Create Account
- **Next Screens**: EmailVerificationWaitFragment

#### 3. ForgotPasswordFragment
- **Purpose**: Password recovery
- **Fields**: Email
- **Actions**: Send Reset Link
- **Next Screens**: LoginFragment (after link sent)

#### 4. EmailVerificationWaitFragment
- **Purpose**: Wait for email verification
- **Display**: Status, resend option
- **Auto**: Polls Firebase for verification
- **Next Screens**: PendingApprovalFragment or Home

#### 5. PendingApprovalFragment
- **Purpose**: Wait for admin approval
- **Display**: Approval status, estimated time
- **Actions**: Logout
- **Next Screens**: MainActivity (when approved) OR Login (on logout)

### Authentication Flow Diagram

```
┌─────────────────────────────────────────────────────────┐
│                  AUTH DECISION TREE                     │
└─────────────────────────────────────────────────────────┘

                    User Launches App
                           │
                           ▼
                   Check Auth Token
                      /         \
                    YES          NO
                    /              \
                   ▼                ▼
            Verify with Firebase   LoginFragment
              /        |      \
             ✓         ✗       timeout
            /           \        \
           ▼             ▼        ▼
        Check        Clear Token  Show Error
        Approval      & Return     & Retry
          /    \      to Login
        YES    NO
        /       \
       ▼         ▼
    Home      PendingApprovalFragment
    Screen    ├─ Approved? → Home
             └─ Rejected? → LoginFragment
```

### State Management Example

```kotlin
// AuthViewModel manages the entire flow
class AuthViewModel(private val authRepo: AuthRepository) : ViewModel() {
    
    // Observable state
    private val _authState = MutableStateFlow<AuthState>(Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()
    
    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = Loading
            val result = authRepo.login(email, password)
            
            _authState.value = when {
                result.isSuccess && result.data.isEmailVerified -> {
                    if (result.data.isApproved) {
                        Success(AuthScreen.HOME)
                    } else {
                        Success(AuthScreen.PENDING_APPROVAL)
                    }
                }
                result.isSuccess -> {
                    Success(AuthScreen.VERIFY_EMAIL)
                }
                else -> Error(result.exception.message ?: "Login failed")
            }
        }
    }
}

// Fragment observes state
class LoginFragment : Fragment() {
    override fun onViewCreated(...) {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.authState.collect { state ->
                    when (state) {
                        is Success -> navigateToScreen(state.screen)
                        is Error -> showError(state.message)
                        is Loading -> showProgressBar()
                    }
                }
            }
        }
    }
}
```

---

## 🏫 CLASSES MODULE - Complete Flow

**File Path**: `app/src/main/java/com/syed/classconnect/ui/classes/`

### Main Screens

#### 1. ClassListFragment
- **Purpose**: Show all enrolled classes
- **Display**: Class cards with:
  - Class name, teacher name
  - Student count
  - Current announcements preview
- **Actions**: 
  - Tap to open details
  - Create class (teacher/admin)
  - Join class
- **Lazy Loading**: 
  - Load 25 classes per page
  - Paginate when scrolling near end

#### 2. ClassDetailActivity (Main hub)
- **Purpose**: Central hub for class content
- **Tabs**:
  - **Feed** (Announcements)
  - **Students** (Class roster)
  - **Materials** (Course content)
  - **Settings** (Class config)

#### Tab: Feed
- **Component**: FeedFragment
- **Purpose**: View/post announcements
- **Display**: List of announcements with:
  - Post date
  - Author name & avatar
  - Content preview
- **Lazy Loading**: Pagination (20 per page)
- **Actions**:
  - Teacher: Create new post
  - Student: React/comment

#### Tab: Students
- **Component**: StudentsFragment
- **Purpose**: View class roster
- **Display**: 
  - Student cards with avatars
  - Role badges (Student/Assistant)
- **Lazy Loading**: Paginate students (30 per page)
- **Actions**:
  - Teacher: Assign grades, view submissions
  - Admin: Change roles

#### Tab: Materials
- **Component**: MaterialsFragment
- **Purpose**: Download course materials
- **Display**: File list with:
  - File name
  - Upload date
  - File size
  - Download count
- **Lazy Loading**: Lazy load file icons/previews
- **Actions**: Download, share, open

#### Tab: Settings
- **Component**: ClassSettingsFragment
- **Purpose**: Configure class settings
- **Display** (Teacher only):
  - Class name, code
  - Archive settings
  - Notification preferences
- **Actions**: Edit, archive, delete

### Class Navigation Diagram

```
ClassListFragment
    │
    ├─ Tap Class Card
    │  │
    │  └─→ ClassDetailActivity
    │      ├─ Feed Tab [Default]
    │      │   └─ List announcements (paginated)
    │      │       └─ Tap to expand
    │      │
    │      ├─ Students Tab
    │      │   └─ List students (paginated)
    │      │       ├─ Tap student → StudentDetailDialog
    │      │       └─ Teacher: Change grade
    │      │
    │      ├─ Materials Tab
    │      │   └─ List files (paginated)
    │      │       └─ Tap to download/open
    │      │
    │      └─ Settings Tab [Teacher only]
    │          └─ Class configuration
    │
    ├─ FAB: Create Class → CreateClassBottomSheet
    │   └─ Enter class details
    │       └─ Returns to ClassListFragment
    │
    └─ FAB: Join Class → JoinClassBottomSheet
        └─ Enter class code
            └─ Returns to ClassListFragment with new class
```

### Lazy Loading in Classes

```kotlin
// Pagination for student list
class StudentsFragment : Fragment() {
    
    private val viewModel: StudentsViewModel by viewModels()
    
    override fun onViewCreated(...) {
        adapter.setOnLoadMoreListener {
            viewModel.loadMoreStudents()
        }
        
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.students.collect { students ->
                    adapter.submitList(students)  // Only updates changed items
                }
            }
        }
    }
}

// ViewModel with pagination
class StudentsViewModel(private val classRepo: ClassRepository) : ViewModel() {
    
    private val _students = MutableStateFlow<List<Student>>(emptyList())
    val students: StateFlow<List<Student>> = _students.asStateFlow()
    
    private var studentPage = 0
    private val pageSize = 30
    
    init {
        loadMoreStudents()
    }
    
    fun loadMoreStudents() {
        viewModelScope.launch {
            val newStudents = classRepo.getClassStudents(
                classId = classId,
                page = studentPage,
                pageSize = pageSize
            )
            _students.value += newStudents
            studentPage++
        }
    }
}
```

---

## 🏠 HOME MODULE - Complete Flow

**File Path**: `app/src/main/java/com/syed/classconnect/ui/home/`

### Dashboard Screens (Role-based)

#### StudentHomeFragment
- **Purpose**: Student dashboard
- **Sections**:
  1. **Greeting** (Dynamic greeting with time of day)
  2. **Today's Classes** (Paginated, 5 per screen)
  3. **Upcoming Deadlines** (Paginated, deadline-colored)
  4. **Recent News** (From API, 3 cards)
  5. **Quick Actions** (Attendance, assignments)

#### TeacherHomeFragment
- **Purpose**: Teacher dashboard
- **Sections**:
  1. **Today's Schedule** (Classes with times)
  2. **Pending Submissions** (Assignments needing grading)
  3. **Class Statistics** (Charts)
  4. **Recent Announcements** (Posted by teacher)

#### AdminDashboardFragment
- **Purpose**: Admin overview
- **Sections**:
  1. **System Status** (User count, storage)
  2. **Pending Approvals** (New registrations)
  3. **Recent Activity** (Audit log)
  4. **Quick Stats** (Classes, users, activity)

### Home Screen Data Flow

```
┌────────────────────────────────────────────────┐
│  StudentHomeFragment onViewCreated()           │
├────────────────────────────────────────────────┤
│                                                │
│ 1. Show skeleton loaders                       │
│ 2. viewModel.loadHomeData() called             │
│ 3. Collect homeState:                          │
│    • Loading → Keep skeletons                  │
│    • Success → Replace with real data          │
│    • Error → Show error banner                 │
│                                                │
└────────────────────────────────────────────────┘
        │
        ▼
┌────────────────────────────────────────────────┐
│  HomeViewModel.loadHomeData()                  │
├────────────────────────────────────────────────┤
│                                                │
│ Launch concurrent tasks:                       │
│  • getTodayClasses()                           │
│  • getUpcomingDeadlines(page=0)               │
│  • getLatestNews()                             │
│  • getUnreadNotifications()                    │
│                                                │
│ All tasks run in parallel (async/await)        │
│ Combine results into HomeData                  │
│ Emit Success(HomeData)                         │
│                                                │
└────────────────────────────────────────────────┘
        │
        ▼
┌────────────────────────────────────────────────┐
│  UI Update (Fragment collects and rebuilds)    │
├────────────────────────────────────────────────┤
│                                                │
│ ✓ TodayClassesAdapter.submitList(classes)     │
│ ✓ DeadlinesAdapter.submitList(deadlines)      │
│ ✓ NewsAdapter.submitList(news)                │
│ ✓ Hide loading skeleton                        │
│                                                │
│ User sees smooth transition from skeleton      │
│ to real data                                   │
│                                                │
└────────────────────────────────────────────────┘
```

### Pagination: Load More Deadlines

```
Screen shows:
┌──────────────────────┐
│ Deadline 1           │  ← Items 1-20 (page 0)
│ Deadline 2           │
│ Deadline 3           │
│ Deadline 4           │
│ Deadline 5           │
│ ...                  │
│ [Load More]          │
└──────────────────────┘

User scrolls down and taps [Load More]:
  1. viewModel.loadMoreDeadlines()
  2. Repository queries page=1
  3. Items 21-40 loaded
  4. Adapter.submitList(all 40 items)
  5. UI scrolls to show new items

Result:
┌──────────────────────┐
│ Deadline 1           │
│ ...                  │
│ Deadline 20          │  ← Can scroll up to see
│ Deadline 21          │  ← New items added here
│ Deadline 22          │
│ ...                  │
│ [Load More]          │
└──────────────────────┘
```

---

## 📝 ASSIGNMENTS MODULE - Complete Flow

**File Path**: `app/src/main/java/com/syed/classconnect/ui/assignments/`

### Student View

#### AssignmentsFragment
- **Purpose**: View assigned assignments
- **Display**: 
  - Assignment cards with:
    - Title, due date
    - Status (Pending, Submitted, Graded)
    - Deadline color coding
- **Lazy Loading**: Paginated (20 per page)
- **Actions**: Tap to view details

#### AssignmentDetailFragment
- **Purpose**: View assignment details & submit
- **Display**:
  - Description, rubric
  - Attached files
  - Submission form
- **Actions**: Upload file, submit

### Teacher View

#### CreateAssignmentFragment
- **Purpose**: Create new assignment
- **Fields**: Title, description, due date, rubric
- **Actions**: Create, save draft

#### GradeSubmissionFragment
- **Purpose**: Grade student submissions
- **Display**: 
  - Student name
  - Submitted file
  - Grade form
- **Actions**: Add grade & feedback

#### SubmissionListFragment
- **Purpose**: View all student submissions
- **Display**: 
  - Student list with status
  - Submission count
- **Lazy Loading**: Paginated
- **Actions**: View submission, grade

---

## 📍 ATTENDANCE MODULE - Complete Flow

**File Path**: `app/src/main/java/com/syed/classconnect/ui/attendance/`

### Attendance Taking (Student)

#### AttendanceFragment
```
┌──────────────────────────────┐
│  📱 Point camera at QR code  │
├──────────────────────────────┤
│  ████████████████████████    │  ← Camera feed
│  ████████████████████████    │
│  ████████████████████████    │
│  ████████████████████████    │
│  ████████████████████████    │
├──────────────────────────────┤
│  Scan to mark attendance     │
│  Status: Ready               │
└──────────────────────────────┘

QR Detected:
   ↓
Parse QR data → Get sessionId
   ↓
Call API → Mark attendance
   ↓
Show success → Play sound ✓
```

### Attendance Taking (Teacher)

#### TeacherSessionFragment
```
┌──────────────────────────────┐
│  Class: Math 101             │
│  Time: 10:00-11:00 AM        │
│  Session: ACTIVE             │
├──────────────────────────────┤
│  Present:     28/30          │  ← Real-time count
│  Absent:      2/30           │
│  Not marked:  0/30           │
├──────────────────────────────┤
│  [View Attendance List]      │
│  [End Session]               │
└──────────────────────────────┘

Real-time updates:
  Firestore Listener → observes attendance collection
  On each mark attendance → count updates live
```

### Attendance History

#### AttendanceHistoryFragment
- **Purpose**: View attendance records
- **Display**: Attendance list (paginated)
- **Lazy Loading**: Load 50 records per page
- **Actions**: Filter by date, export

---

## 📝 QUIZ MODULE - Complete Flow

**File Path**: `app/src/main/java/com/syed/classconnect/ui/quiz/`

### Student: Take Quiz

#### QuizListFragment
- **Purpose**: List available quizzes
- **Display**: Quiz cards with:
  - Quiz name, class
  - Due date
  - Status (Available, In Progress, Completed)
- **Lazy Loading**: Paginated
- **Actions**: Tap to attempt

#### QuizAttemptActivity
```
┌──────────────────────────────────┐
│  Math Quiz - Question 3/20       │
│  ⏱️ Time: 14:32 remaining        │
├──────────────────────────────────┤
│                                  │
│  What is the square root of 16?  │
│                                  │
│  A) 2     B) 4  ← Selected      │
│  C) 8     D) 16                 │
│                                  │
│  [< Previous] [Next >]          │
│                                  │
│  Progress: ███░░░░░░░░░░░░░░░░░│
│            3/20 (15%)            │
│                                  │
└──────────────────────────────────┘

Navigation:
  Previous/Next buttons scroll between questions
  ViewPager pre-caches adjacent questions
  Questions load lazily as needed
  Selections saved in ViewModel
  Timer counts down globally
```

#### QuizResultFragment
- **Purpose**: Show quiz results
- **Display**:
  - Score, percentage
  - Question review (if allowed)
  - Feedback from teacher

### Teacher: Create Quiz

#### CreateQuizFragment
- **Purpose**: Create new quiz
- **Sections**:
  1. Quiz details (Name, due date, time limit)
  2. Add questions button
  3. Question list (drag to reorder)

#### AddQuestionDialog
- **Purpose**: Add individual questions
- **Fields**: Question text, answers, correct answer
- **Types**: MCQ, True/False, Short Answer

### Teacher: View Submissions

#### QuizResultsFragment
- **Purpose**: View all student submissions
- **Display**: 
  - Student names with scores
  - Sort by score/time
- **Lazy Loading**: Paginated
- **Actions**: View individual result

---

## 🎨 SPLASH SCREEN - Animation Flow

**File Path**: `app/src/main/java/com/syed/classconnect/ui/splash/`

### SplashActivity Lifecycle

```
App Start
    ↓
SplashActivity.onCreate()
    ├─ Initialize Firebase
    ├─ Load theme
    │
    └─ startAnimations()
       ├─ SplashCanvasView.startNeuralAnimation()
       │  └─ Neural network particles animate
       │
       ├─ ParticleBurstView.startAnimation()
       │  └─ Particles burst outward
       │
       └─ SplashAnimationOrchestrator.playSequence()
          ├─ Fade in logo (500ms)
          ├─ Slide in text (600ms)
          └─ Show tagline (500ms)

Parallel: checkAuthentication()
    ├─ Get stored auth token
    ├─ Verify with Firebase
    └─ Determine next screen

After animations complete (2s) OR authentication resolved:
    └─→ Navigate to appropriate screen
```

### Component Breakdown

- **NeuralBackgroundView**: Canvas-based neural network animation
- **ParticleBurstView**: Particle system with physics
- **SplashCanvasView**: Main animated background
- **SplashAnimationOrchestrator**: Coordinates all animations

---

## ⚙️ SETTINGS MODULE

**File Path**: `app/src/main/java/com/syed/classconnect/ui/settings/`

### SettingsManager

```kotlin
Settings stored in SharedPreferences:

┌─────────────────────────────────────┐
│  Theme Settings                     │
│  • Dark mode (boolean)              │
│  • Accent color (string)            │
├─────────────────────────────────────┤
│  Language Settings                  │
│  • Current language (string: en)   │
│  • Auto-translate (boolean)         │
├─────────────────────────────────────┤
│  Notification Settings              │
│  • Enabled (boolean)                │
│  • Sound (boolean)                  │
│  • Vibration (boolean)              │
│  • Quiet hours (time range)         │
├─────────────────────────────────────┤
│  Display Settings                   │
│  • Font size (int: 1-5)            │
│  • High contrast (boolean)          │
├─────────────────────────────────────┤
│  Cache Settings                     │
│  • Clear cache button               │
│  • Offline mode (boolean)           │
└─────────────────────────────────────┘

Access from Fragment:
  val isDarkMode = settingsManager.isDarkMode
  settingsManager.isDarkMode = true  // Notifies Observer
```

---

## 👤 ADMIN MODULE - Complete Flow

**File Path**: `app/src/main/java/com/syed/classconnect/ui/admin/`

### Admin Dashboard

#### AdminDashboardFragment
- **Purpose**: Overview of system
- **Display**: Quick stats, pending actions

#### AdminClassesFragment
- **Purpose**: Manage all classes
- **Display**: Class list (paginated, 25 per page)
- **Actions**: View, edit, archive

#### UserManagementFragment
- **Purpose**: Manage users
- **Display**: User list with roles
- **Lazy Loading**: Paginated (30 per page)
- **Actions**: Create, edit, delete user

#### UserDetailFragment
- **Purpose**: Edit single user
- **Fields**: Name, email, role, status
- **Actions**: Change role (if applicable)

#### AssignTeacherBottomSheet
- **Purpose**: Assign teacher to class
- **Display**: Teacher selection
- **Actions**: Confirm assignment

#### RoleChangeHistoryFragment
- **Purpose**: Audit log of role changes
- **Display**: Role change records (paginated)
- **Columns**: User, old role, new role, date, changed by
- **Lazy Loading**: Paginated (50 per page)

### Admin Flow Diagram

```
AdminDashboardFragment
    │
    ├─ Classes Stats → AdminClassesFragment
    │    └─ Tap class → Edit/Archive
    │
    ├─ Pending Approvals → UserManagementFragment
    │    │
    │    └─ Tap user → UserDetailFragment
    │         ├─ View details
    │         ├─ [Approve] button
    │         └─ Approve → Success message
    │
    ├─ Users → UserManagementFragment
    │    ├─ Search/filter users (paginated)
    │    └─ Tap user → UserDetailFragment
    │         ├─ Change role → AssignTeacherBottomSheet
    │         └─ Role changed → Audit log updated
    │
    └─ Audit Log → RoleChangeHistoryFragment
         └─ View all role changes (paginated)
            Filter by date/user
```

---

## 🎯 COMPLETE NAVIGATION GRAPH

```
SplashActivity
    │
    ├─── No auth token ────→ AuthActivity (nav_auth.xml)
    │                            ├─ LoginFragment
    │                            ├─ RegisterFragment
    │                            ├─ ForgotPasswordFragment
    │                            ├─ EmailVerificationWaitFragment
    │                            └─ PendingApprovalFragment
    │
    └─── Valid token ────→ MainActivity (nav_main.xml)
                              │
                              ├─ BottomNav
                              │  ├─ [HOME] → StudentHomeFragment
                              │  │           (or TeacherHomeFragment, AdminDashboardFragment)
                              │  │           ├─ [Load More] → TodayClassesAdapter
                              │  │           ├─ [Load More] → DeadlinesAdapter
                              │  │           └─ [News card] → WebViewActivity
                              │  │
                              │  ├─ [CLASSES] → ClassListFragment
                              │  │             ├─ [Class card] → ClassDetailActivity
                              │  │             │                 ├─ Feed Tab
                              │  │             │                 │  └─ FeedFragment
                              │  │             │                 ├─ Students Tab
                              │  │             │                 │  └─ StudentsFragment
                              │  │             │                 ├─ Materials Tab
                              │  │             │                 │  └─ MaterialsFragment
                              │  │             │                 └─ Settings Tab
                              │  │             │                    └─ ClassSettingsFragment
                              │  │             ├─ [FAB +] → CreateClassBottomSheet
                              │  │             └─ [FAB Join] → JoinClassBottomSheet
                              │  │
                              │  ├─ [ASSIGNMENTS] → AssignmentsFragment
                              │  │                  ├─ [Assignment] → AssignmentDetailFragment
                              │  │                  ├─ [Create] → CreateAssignmentFragment (teacher)
                              │  │                  ├─ [Submissions] → SubmissionListFragment (teacher)
                              │  │                  └─ [Grade] → GradeSubmissionFragment (teacher)
                              │  │
                              │  ├─ [ATTENDANCE] → AttendanceFragment
                              │  │                 ├─ QR scan
                              │  │                 ├─ [History] → AttendanceHistoryFragment
                              │  │                 └─ [Session] → TeacherSessionFragment (teacher)
                              │  │
                              │  └─ [PROFILE] → ProfileFragment
                              │                  ├─ [Settings] → SettingsFragment
                              │                  ├─ [Account] → UserDetailViewFragment
                              │                  └─ [Logout] → AuthActivity
                              │
                              └─ [ADMIN] (if admin role)
                                 ├─ AdminDashboardFragment
                                 ├─ AdminClassesFragment
                                 ├─ UserManagementFragment
                                 │  └─ [User] → UserDetailFragment
                                 │             └─ [Assign] → AssignTeacherBottomSheet
                                 └─ RoleChangeHistoryFragment
```

---

## 📊 Data Models & Collections

### Firestore Collections

```
firestore/
├── users/
│   └── {userId}/
│       ├── email: string
│       ├── name: string
│       ├── role: "student" | "teacher" | "admin"
│       ├── profileImageUrl: string
│       └── isApproved: boolean
│
├── classes/
│   └── {classId}/
│       ├── name: string
│       ├── teacherId: string
│       ├── code: string
│       ├── createdAt: timestamp
│       └── studentIds: array
│
├── assignments/
│   └── {assignmentId}/
│       ├── classId: string
│       ├── title: string
│       ├── description: string
│       ├── dueDate: timestamp
│       ├── createdBy: string
│       └── rubric: object
│
├── submissions/
│   └── {submissionId}/
│       ├── assignmentId: string
│       ├── studentId: string
│       ├── fileUrl: string
│       ├── submittedAt: timestamp
│       ├── grade: number
│       └── feedback: string
│
├── attendance/
│   └── {attendanceId}/
│       ├── sessionId: string
│       ├── studentId: string
│       ├── markedAt: timestamp
│       └── status: "present" | "absent"
│
├── announcements/
│   └── {announcementId}/
│       ├── classId: string
│       ├── authorId: string
│       ├── title: string
│       ├── content: string
│       ├── attachments: array
│       └── createdAt: timestamp
│
└── quizzes/
    └── {quizId}/
        ├── classId: string
        ├── title: string
        ├── timeLimit: number
        ├── dueDate: timestamp
        ├── questions: array
        └── createdBy: string
```

---

## 🎯 Summary

### All Modules at a Glance

| Module | Screens | Key Feature | Lazy Load |
|--------|---------|------------|-----------|
| **Auth** | Login, Register, Verify | Email verification | No |
| **Home** | Dashboard | Quick overview | Yes (pagination) |
| **Classes** | List, Detail (4 tabs) | Multi-tab interface | Yes (tabs + pagination) |
| **Assignments** | List, Detail, Create | File upload | Yes (pagination) |
| **Attendance** | QR scan, History | Real-time marking | Yes (history pagination) |
| **Quiz** | List, Attempt, Results | Timed questions | Yes (question caching) |
| **Admin** | Classes, Users, History | User management | Yes (pagination) |
| **Splash** | Animated intro | Auth check | No |
| **Settings** | Theme, Language | User preferences | No |

### Performance Optimizations by Module

```
Module              Technique
───────────────────────────────────────
Admin               Pagination (30 users/page)
Attendance          History pagination (50/page)
Classes             Tab lazy load + paginated lists
Home                Concurrent data load + pagination
Quiz                Question pre-caching (ViewPager)
Assignments         Image lazy load + pagination
Auth                Form validation only
Splash              Minimal blocking operations
```

---

*Complete documentation for ClassConnect UI Architecture - April 10, 2026*

