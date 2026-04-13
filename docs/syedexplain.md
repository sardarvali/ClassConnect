# ClassConnect - Complete UI Architecture & Component Explanation

> **Author:** Syed Sardar Valli  
> **Date:** April 10, 2026  
> **Project:** ClassConnect Android Classroom Management Platform  
> **Version:** 2.0 - Complete UI Deep Dive

---

## 📋 Table of Contents

1. [App Overview](#app-overview)
2. [UI Architecture & Patterns](#ui-architecture--patterns)
3. [Lazy Loading & Performance](#lazy-loading--performance)
4. [Module-by-Module Breakdown](#module-by-module-breakdown)
5. [Navigation Flow](#navigation-flow)
6. [Utilities & Support Systems](#utilities--support-systems)
7. [Implementation Best Practices](#implementation-best-practices)
8. [Testing & Quality](#testing--quality)

---

## App Overview

### What is ClassConnect?

**ClassConnect** is a comprehensive Android classroom management platform enabling:
- **Teachers**: Create classes, assignments, quizzes, track attendance, manage students
- **Students**: Join classes, complete assignments, attempt quizzes, view grades
- **Admins**: Manage users, institutions, assign teachers to classes

### Tech Stack

```
┌─────────────────────────────────────────────┐
│        PRESENTATION LAYER (UI)              │
│  • Fragments (Screen Components)            │
│  • ViewModels (State Management)            │
│  • Adapters (List/Recycler views)           │
└────────────┬────────────────────────────────┘
             │
┌────────────▼────────────────────────────────┐
│      BUSINESS LOGIC LAYER (VIEWMODELS)      │
│  • Kotlin Coroutines (async operations)     │
│  • StateFlow (reactive state)               │
│  • Use Cases (business rules)               │
└────────────┬────────────────────────────────┘
             │
┌────────────▼────────────────────────────────┐
│      DATA LAYER (REPOSITORIES)              │
│  • Firebase Firestore (real-time DB)        │
│  • Firebase Storage (files)                 │
│  • Remote Config (feature flags)            │
└─────────────────────────────────────────────┘
```

### Key Characteristics

| Aspect | Implementation |
|--------|-----------------|
| **Design Pattern** | MVVM (Model-View-ViewModel) with Clean Architecture |
| **State Management** | StateFlow (replacing old LiveData) |
| **Navigation** | Navigation Component (Fragment-based) |
| **Async Operations** | Kotlin Coroutines with viewModelScope |
| **Database** | Firebase Firestore (NoSQL) |
| **Authentication** | Firebase Auth |
| **Real-time Updates** | Firestore Listeners |
| **DI Framework** | Hilt |
| **UI Framework** | Material Design 3 |

---

## UI Architecture & Patterns

### MVVM Pattern (Model-View-ViewModel)

```
┌──────────────────────────────────────────────────────────┐
│                    FRAGMENT (UI Layer)                   │
│  • Displays data from ViewModel                          │
│  • Handles user interactions                             │
│  • Passes events to ViewModel                            │
│  File: *Fragment.kt (e.g., StudentHomeFragment.kt)       │
└──────────────────┬───────────────────────────────────────┘
                   │ Observes StateFlow
                   │ Calls methods
                   ▼
┌──────────────────────────────────────────────────────────┐
│                  VIEWMODEL (Logic Layer)                 │
│  • Manages UI state (StateFlow)                          │
│  • Handles business logic                                │
│  • Makes repository calls                                │
│  • Survives configuration changes                        │
│  File: *ViewModel.kt (e.g., HomeViewModel.kt)            │
└──────────────────┬───────────────────────────────────────┘
                   │ Calls methods
                   ▼
┌──────────────────────────────────────────────────────────┐
│              REPOSITORY (Data Layer)                     │
│  • Abstracts data sources (Firestore, Remote Config)    │
│  • Handles API calls                                     │
│  • Provides single source of truth                       │
│  File: *Repository.kt (e.g., ClassRepository.kt)         │
└──────────────────────────────────────────────────────────┘
                   │
                   ▼
        ┌──────────────────────┐
        │  Firestore / APIs    │
        │  Remote Config       │
        │  Local Cache         │
        └──────────────────────┘
```

### Example: Login Flow

```kotlin
// User taps "Sign In" button
LoginFragment {
    button.setOnClickListener {
        viewModel.login(email, password)
    }
    
    // Observe state from ViewModel
    viewModel.loginState.collect { state ->
        when (state) {
            is Loading -> showProgressBar()
            is Success -> navigateToHome()
            is Error -> showErrorMessage(state.message)
        }
    }
}

// ViewModel handles the logic
LoginViewModel {
    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = Loading
            val result = repository.login(email, password)
            _loginState.value = when {
                result.isSuccess -> Success(result.data)
                else -> Error(result.exception.message)
            }
        }
    }
}

// Repository abstracts the data source
AuthRepository {
    suspend fun login(email: String, password: String) {
        return firebaseAuth.signInWithEmailAndPassword(email, password)
    }
}
```

### State Management with StateFlow (Reactive)

```kotlin
// Instead of old LiveData, we use StateFlow for better performance
class HomeViewModel : ViewModel() {
    
    // Private mutable state (only ViewModel can change)
    private val _uiState = MutableStateFlow<HomeUiState>(Loading)
    
    // Public immutable state (UI observes this)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    init {
        loadHomeData()
    }
    
    private fun loadHomeData() {
        viewModelScope.launch {
            try {
                _uiState.value = Loading
                val todayClasses = repository.getTodayClasses()
                val upcomingDeadlines = repository.getUpcomingDeadlines()
                val news = repository.getNews()
                
                _uiState.value = Success(
                    HomeData(todayClasses, upcomingDeadlines, news)
                )
            } catch (e: Exception) {
                _uiState.value = Error(e.message ?: "Unknown error")
            }
        }
    }
}

// Fragment collects from StateFlow
class StudentHomeFragment : Fragment() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        lifecycleScope.launch {
            // Collect only when STARTED (won't update when paused)
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is Loading -> showLoadingSkeleton()
                        is Success -> showData(state.data)
                        is Error -> showError(state.message)
                    }
                }
            }
        }
    }
}
```

---

## Lazy Loading & Performance

### What is Lazy Loading?

**Lazy loading** = Only load data when user needs it (scroll into view), not everything upfront.

This improves:
- ⚡ **App Speed**: Load 10 items, not 1000
- 💾 **Memory**: Keep only visible items in memory
- 🔋 **Battery**: Fewer network requests
- 📱 **Responsiveness**: UI stays smooth

### Implementation Approaches

#### 1. **Pagination (Most Common)**

```kotlin
// ViewModel with pagination
class AssignmentsViewModel(private val repo: AssignmentRepository) : ViewModel() {
    
    private val _assignments = MutableStateFlow<List<Assignment>>(emptyList())
    val assignments: StateFlow<List<Assignment>> = _assignments.asStateFlow()
    
    private val _isLoadingMore = MutableStateFlow(false)
    val isLoadingMore: StateFlow<Boolean> = _isLoadingMore.asStateFlow()
    
    private var currentPage = 0
    private val pageSize = 20  // Load 20 items per page
    
    fun loadNextPage() {
        if (_isLoadingMore.value) return  // Already loading
        
        viewModelScope.launch {
            _isLoadingMore.value = true
            try {
                val newAssignments = repo.getAssignments(
                    page = currentPage,
                    pageSize = pageSize
                )
                _assignments.value += newAssignments
                currentPage++
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoadingMore.value = false
            }
        }
    }
}

// Adapter with scroll listener
class AssignmentsAdapter : RecyclerView.Adapter<AssignmentViewHolder>() {
    private var onLoadMore: (() -> Unit)? = null
    
    fun setOnLoadMoreListener(listener: () -> Unit) {
        onLoadMore = listener
    }
    
    override fun onBindViewHolder(holder: AssignmentViewHolder, position: Int) {
        // When user scrolls near the end, load more
        if (position >= itemCount - 5) {
            onLoadMore?.invoke()
        }
    }
}

// Fragment connects adapter listener
class AssignmentsFragment : Fragment() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        adapter.setOnLoadMoreListener {
            viewModel.loadNextPage()
        }
        
        recyclerView.adapter = adapter
    }
}
```

#### 2. **Firestore Query Pagination**

```kotlin
// Repository using Firestore pagination
class AssignmentRepository {
    
    suspend fun getAssignments(
        classId: String,
        page: Int,
        pageSize: Int = 20
    ): List<Assignment> = withContext(Dispatchers.IO) {
        var query: Query = firestore.collection("assignments")
            .whereEqualTo("classId", classId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(pageSize.toLong())
        
        // Skip to the page we want
        if (page > 0) {
            // This is simplified - real pagination uses lastDocumentSnapshot
            query = query.offset(page * pageSize)
        }
        
        val documents = query.get().await()
        documents.toObjects(Assignment::class.java)
    }
}
```

#### 3. **Virtual Scrolling with DiffUtil**

```kotlin
// Smart adapter that only updates changed items
class AssignmentsAdapter : ListAdapter<Assignment, AssignmentViewHolder>(
    AssignmentDiffCallback()
) {
    
    override fun onBindViewHolder(holder: AssignmentViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

// Callback to detect what actually changed
class AssignmentDiffCallback : DiffUtil.ItemCallback<Assignment>() {
    override fun areItemsTheSame(old: Assignment, new: Assignment) 
        = old.id == new.id  // Same item?
    
    override fun areContentsTheSame(old: Assignment, new: Assignment) 
        = old == new  // Same data?
}

// Update adapter efficiently
adapter.submitList(newAssignments)  // Only updates changed items!
```

#### 4. **Lazy Loading Images**

```kotlin
// Use Glide/Coil for lazy image loading
class StudentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(student: Student) {
        Glide.with(itemView.context)
            .load(student.profileImageUrl)
            .placeholder(R.drawable.ic_placeholder_avatar)
            .error(R.drawable.ic_error_avatar)
            .into(studentImageView)
    }
}
```

#### 5. **Skeleton Loading (Loading Placeholder)**

```kotlin
// Show loading skeleton while fetching data
class SkeletonLoadingHelper {
    fun showSkeletonLoader(container: ViewGroup) {
        repeat(5) {
            val skeleton = SkeletonView(container.context)
            container.addView(skeleton)
        }
    }
    
    fun hideSkeletonLoader(container: ViewGroup) {
        container.removeAllViews()
    }
}

// Usage in Fragment
class AssignmentsFragment : Fragment() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is Loading -> {
                            skeletonHelper.showSkeletonLoader(assignmentsContainer)
                        }
                        is Success -> {
                            skeletonHelper.hideSkeletonLoader(assignmentsContainer)
                            adapter.submitList(state.data.assignments)
                        }
                    }
                }
            }
        }
    }
}
```

### Performance Metrics

| Metric | Target | Benefit |
|--------|--------|---------|
| **Time to First Paint** | < 1s | User sees something immediately |
| **Initial Data Load** | 20 items | Fast first load |
| **Scroll Smoothness** | 60 FPS | Smooth scrolling experience |
| **Memory Usage** | < 200 MB | Won't crash old devices |
| **Network Requests** | Batched | Reduce server load |

---

## Module-by-Module Breakdown

### 1. 🔐 **ADMIN Module**

**Path**: `app/src/main/java/com/syed/classconnect/ui/admin/`

**Purpose**: Administrative functions for managing users, classes, and roles.

#### Components

| Component | File | Purpose |
|-----------|------|---------|
| **AdminClassesFragment** | `AdminClassesFragment.kt` | Display all classes (for admin) |
| **AdminViewModel** | `AdminViewModel.kt` | Manages admin data state |
| **AdminClassesViewModel** | `AdminClassesViewModel.kt` | Manages class list state |
| **UserManagementFragment** | `UserManagementFragment.kt` | Manage users (create, edit, delete) |
| **UserManagementViewModel** | `UserManagementViewModel.kt` | User management state |
| **UserDetailFragment** | `UserDetailFragment.kt` | View single user details |
| **UserDetailViewModel** | `UserDetailViewModel.kt` | Single user state |
| **RoleChangeHistoryFragment** | `RoleChangeHistoryFragment.kt` | View role change audit logs |
| **RoleChangeHistoryViewModel** | `RoleChangeHistoryViewModel.kt` | History state |
| **AssignTeacherBottomSheet** | `AssignTeacherBottomSheet.kt` | Modal to assign teachers |

#### User Flow

```
Admin Login
    ↓
Admin Dashboard (AdminClassesFragment)
    ├── View All Classes → ClassList
    ├── Manage Users → UserManagementFragment
    │   ├── Create User
    │   ├── Edit User Details (UserDetailFragment)
    │   └── Change Role (AssignTeacherBottomSheet)
    └── View Audit Logs → RoleChangeHistoryFragment
```

#### Code Example: Admin Dashboard

```kotlin
class AdminClassesFragment : Fragment() {
    private val viewModel: AdminClassesViewModel by viewModels()
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.classesState.collect { state ->
                    when (state) {
                        is Loading -> showLoadingPlaceholder()
                        is Success -> updateClassesList(state.classes)
                        is Error -> showErrorDialog(state.message)
                    }
                }
            }
        }
    }
}

class AdminClassesViewModel(
    private val classRepository: ClassRepository
) : ViewModel() {
    
    private val _classesState = MutableStateFlow<UiState<List<ClassModel>>>(Loading)
    val classesState: StateFlow<UiState<List<ClassModel>>> = _classesState.asStateFlow()
    
    init {
        loadClasses()
    }
    
    private fun loadClasses() {
        viewModelScope.launch {
            try {
                _classesState.value = Loading
                val classes = classRepository.getAllClasses()
                _classesState.value = Success(classes)
            } catch (e: Exception) {
                _classesState.value = Error(e.message ?: "Failed to load classes")
            }
        }
    }
}
```

#### Lazy Loading

- ✅ **Pagination**: Load 25 classes at a time
- ✅ **DiffUtil**: Only update changed classes in RecyclerView
- ✅ **Images**: Lazy load teacher/student avatars with Glide

---

### 2. 📍 **ATTENDANCE Module**

**Path**: `app/src/main/java/com/syed/classconnect/ui/attendance/`

**Purpose**: Track attendance using QR codes and Bluetooth scanning.

#### Components

| Component | File | Purpose |
|-----------|------|---------|
| **AttendanceFragment** | `AttendanceFragment.kt` | Scan QR code to mark attendance |
| **AttendanceViewModel** | `AttendanceViewModel.kt` | Attendance state management |
| **QrCodeAnalyzer** | `QrCodeAnalyzer.kt` | QR code detection & parsing |
| **AttendanceVerification** | `AttendanceVerification.kt` | Verify attendance marking |
| **AttendanceHistoryAdapter** | `AttendanceHistoryAdapter.kt` | Display attendance records |

#### Features

```
STUDENT View:
┌──────────────────────┐
│ Camera Feed (QR)     │ ← Points at QR code
├──────────────────────┤
│ "Scan to Mark..."    │
└──────────────────────┘
        ↓ (QR detected)
┌──────────────────────┐
│ ✓ Attendance Marked! │
│ 10:05 AM Today       │
└──────────────────────┘

TEACHER View:
┌──────────────────────┐
│ Today's Session      │
├──────────────────────┤
│ Class: Math 101      │
│ Time: 10:00-11:00    │
│ Present: 28/30       │
│                      │
│ [Start/Stop Session] │
└──────────────────────┘
```

#### QR Code Scanning (CameraX)

```kotlin
class QrCodeAnalyzer : ImageAnalysis.Analyzer {
    private val mlKitScanner = BarcodeScanning.getClient()
    
    override fun analyze(imageProxy: ImageProxy) {
        val image = imageProxy.image ?: return
        
        val inputImage = InputImage.fromMediaImage(
            image,
            imageProxy.imageInfo.rotationDegrees
        )
        
        mlKitScanner.process(inputImage)
            .addOnSuccessListener { barcodes ->
                for (barcode in barcodes) {
                    val qrValue = barcode.rawValue
                    onQrDetected(qrValue)  // Parse and mark attendance
                }
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    }
}

class AttendanceViewModel : ViewModel() {
    fun markAttendance(qrData: String) {
        viewModelScope.launch {
            try {
                // Parse QR: "SESSION_123_45|TOKEN_xyz"
                val sessionId = extractSessionId(qrData)
                val token = extractToken(qrData)
                
                // Verify attendance
                val result = repository.verifyAndMarkAttendance(sessionId, token)
                
                when {
                    result.isSuccess -> {
                        _attendanceState.value = 
                            Success("Attendance marked successfully")
                    }
                    else -> _attendanceState.value = 
                        Error("Failed to mark attendance")
                }
            } catch (e: Exception) {
                _attendanceState.value = Error(e.message)
            }
        }
    }
}
```

#### Lazy Loading

- ✅ **History Pagination**: Load 50 attendance records per page
- ✅ **Real-time Updates**: Firestore listener for live attendance count
- ✅ **Image Caching**: Store session start image locally

---

### 3. 🔑 **AUTH Module**

**Path**: `app/src/main/java/com/syed/classconnect/ui/auth/`

**Purpose**: User authentication (login, registration, password recovery).

#### Components

| Component | File | Purpose |
|-----------|------|---------|
| **AuthActivity** | `AuthActivity.kt` | Host activity for auth fragments |
| **AuthViewModel** | `AuthViewModel.kt` | Auth state management |
| **LoginFragment** | `LoginFragment.kt` | Email/password login UI |
| **RegisterFragment** | `RegisterFragment.kt` | User registration UI |
| **ForgotPasswordFragment** | `ForgotPasswordFragment.kt` | Password recovery UI |
| **EmailVerificationWaitFragment** | `EmailVerificationWaitFragment.kt` | Wait for email verification |
| **PendingApprovalFragment** | `PendingApprovalFragment.kt` | Wait for admin approval |

#### Authentication Flow

```
┌──────────────────┐
│  SplashActivity  │
│  (Check Auth)    │
└────┬─────────────┘
     │
     ├─ User logged in?
     │  YES → MainActivity
     │  NO  ↓
     │
     ▼
┌──────────────────┐
│  AuthActivity    │
│  LoginFragment   │
└────┬─────────────┘
     │
     ├─ "Sign In" → Check credentials
     │  ├─ Invalid → Show error
     │  ├─ Valid but not verified → EmailVerificationWaitFragment
     │  └─ Valid & verified → Check approval status
     │      ├─ Not approved → PendingApprovalFragment
     │      └─ Approved → MainActivity
     │
     ├─ "Sign Up" → RegisterFragment
     │  └─ Complete registration → EmailVerificationWaitFragment
     │
     └─ "Forgot Password" → ForgotPasswordFragment
        └─ Reset link sent → LoginFragment
```

#### Code Example: Login with Validation

```kotlin
class LoginFragment : Fragment() {
    private val viewModel: AuthViewModel by viewModels()
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        signInButton.setOnClickListener {
            val email = emailInput.text.toString()
            val password = passwordInput.text.toString()
            
            // Input validation
            val errors = ValidationUtils.validateLogin(email, password)
            if (errors.isNotEmpty()) {
                showErrors(errors)
                return@setOnClickListener
            }
            
            viewModel.login(email, password)
        }
        
        // Observe auth state
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.authState.collect { state ->
                    when (state) {
                        is Loading -> {
                            progressBar.visibility = View.VISIBLE
                            signInButton.isEnabled = false
                        }
                        is Success -> {
                            progressBar.visibility = View.GONE
                            // Navigate based on approval status
                            if (state.user.isApproved) {
                                navigateToMain()
                            } else {
                                navigateToPendingApproval()
                            }
                        }
                        is Error -> {
                            progressBar.visibility = View.GONE
                            signInButton.isEnabled = true
                            showErrorSnackbar(state.message)
                        }
                    }
                }
            }
        }
    }
}

class AuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {
    
    private val _authState = MutableStateFlow<UiState<User>>(Idle)
    val authState: StateFlow<UiState<User>> = _authState.asStateFlow()
    
    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = Loading
            
            val result = authRepository.login(email, password)
            
            _authState.value = when {
                result.isSuccess && result.data != null -> {
                    // Store user session
                    storageRepository.saveUser(result.data)
                    Success(result.data)
                }
                else -> Error(result.exceptionOrNull()?.message ?: "Login failed")
            }
        }
    }
}
```

#### Lazy Loading

- ❌ **Not applicable** (Auth screens load instantly)
- ✅ **Form validation**: Show errors progressively

---

### 4. 🏫 **CLASS Module**

**Path**: `app/src/main/java/com/syed/classconnect/ui/classes/`

**Purpose**: Browse, create, and manage classes.

#### Components

| Component | File | Purpose |
|-----------|------|---------|
| **ClassListFragment** | `ClassListFragment.kt` | List of enrolled classes |
| **ClassDetailActivity** | `ClassDetailActivity.kt` | Single class details & tabs |
| **ClassDetailViewModel** | `ClassDetailViewModel.kt` | Class details state |
| **StudentsFragment** | `StudentsFragment.kt` | View class students |
| **FeedFragment** (inside class) | `FeedFragment.kt` | Announcements & posts |
| **CreateClassBottomSheet** | `CreateClassBottomSheet.kt` | Create new class dialog |
| **JoinClassBottomSheet** | `JoinClassBottomSheet.kt` | Join existing class dialog |

#### Class Detail Tabs (Lazy Loading Tab Content)

```
┌─────────────────────────────────────────┐
│  Class: Mathematics 101                 │
├─────────────────────────────────────────┤
│ [Feed] [Students] [Materials] [Settings]│  ← Tab headers
├─────────────────────────────────────────┤
│                                         │
│  Recent Announcements:                  │ ← Content loads only when tab visible
│  • "Quiz tomorrow at 10 AM"             │
│  • "Assignment due Friday"              │
│                                         │
└─────────────────────────────────────────┘
```

#### Lazy Loading Tabs

```kotlin
// Fragment with tab container
class ClassDetailActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_class_detail)
        
        // Setup ViewPager2 with LazyFragmentStateAdapter
        val adapter = ClassTabAdapter(supportFragmentManager, lifecycle)
        viewPager.adapter = adapter
        
        // Only loads tab fragment when user swipes to it
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Feed"
                1 -> "Students"
                2 -> "Materials"
                3 -> "Settings"
                else -> ""
            }
        }.attach()
    }
}

// Adapter for lazy tab loading
class ClassTabAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle
) : FragmentStateAdapter(fragmentManager, lifecycle) {
    
    override fun getItemCount() = 4
    
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> FeedFragment.newInstance(classId)
            1 -> StudentsFragment.newInstance(classId)
            2 -> MaterialsFragment.newInstance(classId)
            3 -> ClassSettingsFragment.newInstance(classId)
            else -> Fragment()
        }
    }
}

// Each tab loads data only when visible
class FeedFragment : Fragment() {
    private val viewModel: FeedViewModel by viewModels()
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Only load when fragment becomes visible
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Load announcements using pagination
                viewModel.loadAnnouncements()
                
                viewModel.announcements.collect { announcements ->
                    adapter.submitList(announcements)
                }
            }
        }
    }
}
```

---

### 5. 🏠 **HOME Module**

**Path**: `app/src/main/java/com/syed/classconnect/ui/home/`

**Purpose**: Dashboard showing quick overview (today's classes, upcoming deadlines, news).

#### Components

| Component | File | Purpose |
|-----------|------|---------|
| **StudentHomeFragment** | `StudentHomeFragment.kt` | Student dashboard |
| **TeacherHomeFragment** | `TeacherHomeFragment.kt` | Teacher dashboard |
| **AdminDashboardFragment** | `AdminDashboardFragment.kt` | Admin dashboard |
| **HomeViewModel** | `HomeViewModel.kt` | Dashboard state |
| **TodayClassesAdapter** | `TodayClassesAdapter.kt` | Today's classes list |
| **UpcomingDeadlinesAdapter** | `UpcomingDeadlinesAdapter.kt` | Assignment deadlines |
| **NewsAdapter** | `NewsAdapter.kt` | Latest news cards |

#### Home Dashboard Layout

```
┌─────────────────────────────────────┐
│  ┌─────────────────────────────────┐│ Student Home
│  │  Syed Sardar Valli              ││
│  │  Welcome Back! 👋               ││
│  └─────────────────────────────────┘│
│                                     │
│  TODAY'S CLASSES  [3 items]         │
│  ┌─────────────────────────────────┐│
│  │ Math 101          10:00-11:00 AM││ ← Lazy load
│  │ Physics 102       12:00-01:00 PM││
│  │ Chemistry 103     2:00-3:00 PM  ││
│  └─────────────────────────────────┘│
│                                     │
│  UPCOMING DEADLINES  [Paginated]    │
│  ┌─────────────────────────────────┐│
│  │ 📌 Math Assignment  Due Tomorrow ││ ← Deadline colored by urgency
│  │ 📌 Physics Quiz     Due Friday   ││    (Red=Today, Orange=Soon, etc)
│  │ 📌 Chemistry Lab    Due Next Week││
│  │          [Load More]              │
│  └─────────────────────────────────┘│
│                                     │
│  RECENT NEWS  [From API]            │
│  ┌─────────────────────────────────┐│
│  │ [Image] Education Latest...     ││ ← Lazy load images
│  │ Tap to read full article        ││
│  └─────────────────────────────────┘│
│                                     │
└─────────────────────────────────────┘
```

#### Code Example: Home with Lazy Loading

```kotlin
class StudentHomeFragment : Fragment() {
    private val viewModel: HomeViewModel by viewModels()
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Setup RecyclerView with pagination
        deadlinesAdapter.setOnLoadMoreListener {
            viewModel.loadMoreDeadlines()
        }
        
        // Collect UI state
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.homeState.collect { state ->
                    when (state) {
                        is Loading -> showLoadingSkeletons()
                        is Success -> {
                            showTodayClasses(state.data.todayClasses)
                            showDeadlines(state.data.deadlines)
                            showNews(state.data.news)
                        }
                        is Error -> showErrorBanner(state.message)
                    }
                }
            }
        }
    }
}

class HomeViewModel(
    private val classRepository: ClassRepository,
    private val assignmentRepository: AssignmentRepository,
    private val newsRepository: NewsRepository
) : ViewModel() {
    
    private val _homeState = MutableStateFlow<UiState<HomeData>>(Loading)
    val homeState: StateFlow<UiState<HomeData>> = _homeState.asStateFlow()
    
    private val _deadlinesPage = MutableStateFlow(0)
    
    init {
        loadHomeData()
    }
    
    private fun loadHomeData() {
        viewModelScope.launch {
            try {
                _homeState.value = Loading
                
                // Load all data in parallel
                val todayClasses = async { classRepository.getTodayClasses() }
                val deadlines = async { assignmentRepository.getUpcomingDeadlines(page = 0) }
                val news = async { newsRepository.getLatestNews() }
                
                val data = HomeData(
                    todayClasses = todayClasses.await(),
                    deadlines = deadlines.await(),
                    news = news.await()
                )
                
                _homeState.value = Success(data)
            } catch (e: Exception) {
                _homeState.value = Error(e.message ?: "Failed to load dashboard")
            }
        }
    }
    
    fun loadMoreDeadlines() {
        viewModelScope.launch {
            val nextPage = _deadlinesPage.value + 1
            val moreDeadlines = assignmentRepository.getUpcomingDeadlines(page = nextPage)
            
            val currentData = (_homeState.value as? Success)?.data ?: return@launch
            val updated = currentData.copy(
                deadlines = currentData.deadlines + moreDeadlines
            )
            
            _homeState.value = Success(updated)
            _deadlinesPage.value = nextPage
        }
    }
}
```

---

### 6. 📝 **QUIZ Module**

**Path**: `app/src/main/java/com/syed/classconnect/ui/quiz/`

**Purpose**: Create, take, and review quizzes.

#### Components

| Component | File | Purpose |
|-----------|------|---------|
| **QuizListFragment** | `QuizListFragment.kt` | List available quizzes |
| **CreateQuizFragment** | `CreateQuizFragment.kt` | Create new quiz |
| **AddQuestionDialog** | `AddQuestionDialog.kt` | Add questions to quiz |
| **QuizAttemptActivity** | `QuizAttemptActivity.kt` | Take quiz with timer |
| **QuizProgressVisualization** | `QuizProgressVisualization.kt` | Visual progress bar |
| **QuizResultFragment** | `QuizResultFragment.kt` | Show quiz results |

#### Quiz Taking Experience

```
┌──────────────────────────────────────────┐
│  Math Quiz - Question 2/20               │
│  ⏱️ Time: 8:42 remaining                 │
├──────────────────────────────────────────┤
│                                          │
│  What is 2 + 2 × 3?                      │
│                                          │
│  ○ 8  (selected)  ← Visual feedback      │
│  ○ 12                                    │
│  ○ 10                                    │
│  ○ 6                                     │
│                                          │
│          [< Previous] [Next >]           │
│                                          │
│  Progress: ████████░░░░░░░░░░ 40%        │
└──────────────────────────────────────────┘
```

#### Lazy Loading & Performance

```kotlin
class QuizAttemptActivity : AppCompatActivity() {
    
    private val viewModel: QuizAttemptViewModel by viewModels()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_attempt)
        
        // Setup ViewPager for questions (lazy load each question)
        val adapter = QuizQuestionsAdapter(this)
        viewPager.adapter = adapter
        
        // Enable pre-caching of adjacent questions
        viewPager.offscreenPageLimit = 2  // Cache prev + next question
        
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.quizState.collect { state ->
                    when (state) {
                        is Success -> {
                            adapter.submitList(state.questions)
                            // Disable pre-caching after all loaded
                            viewPager.offscreenPageLimit = 1
                        }
                    }
                }
            }
        }
    }
}

// Smart question adapter with caching
class QuizQuestionsAdapter(
    activity: AppCompatActivity
) : FragmentStateAdapter(activity) {
    
    private var questions: List<Question> = emptyList()
    
    override fun getItemCount() = questions.size
    
    override fun createFragment(position: Int): Fragment {
        // Only creates fragment when needed
        return QuestionFragment.newInstance(questions[position])
    }
    
    fun submitList(newQuestions: List<Question>) {
        questions = newQuestions
        notifyDataSetChanged()
    }
}

// Individual question displays with efficient image loading
class QuestionFragment : Fragment() {
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val question = arguments?.getParcelable<Question>("question") ?: return
        
        questionText.text = question.text
        
        // Load image with Glide (automatically cached)
        if (question.imageUrl != null) {
            Glide.with(this)
                .load(question.imageUrl)
                .thumbnail(0.1f)  // Show thumbnail first
                .into(questionImage)
        }
        
        // Display options
        optionsAdapter.submitList(question.options)
    }
}
```

---

### 7. 🎨 **SPLASH & SETTINGS Modules**

**Path**: `app/src/main/java/com/syed/classconnect/ui/splash/` and `app/src/main/java/com/syed/classconnect/ui/settings/`

#### Splash Screen Components

| Component | File | Purpose |
|-----------|------|---------|
| **SplashActivity** | `SplashActivity.kt` | Initial app launch screen |
| **SplashCanvasView** | `SplashCanvasView.kt` | Custom animated canvas |
| **NeuralBackgroundView** | `NeuralBackgroundView.kt` | Neural network animation |
| **ParticleBurstView** | `ParticleBurstView.kt` | Particle effects |
| **SplashAnimationOrchestrator** | `SplashAnimationOrchestrator.kt` | Coordinate animations |

#### Splash Screen Flow

```
App Launch
    ↓
SplashActivity starts
    ├── Show animated background (particles/neural net)
    ├── Check Firebase initialization
    ├── Check stored authentication token
    │   ├── Token valid? → Verify with Firebase
    │   │   ├── Still valid? → MainActivity
    │   │   └── Expired? → Clear & show AuthActivity
    │   │
    │   └── No token? → AuthActivity
    │
    └── Timeout (5s) → AuthActivity (fallback)
```

#### Settings Module

| Component | File | Purpose |
|-----------|------|---------|
| **SettingsManager** | `SettingsManager.kt` | Settings storage & retrieval |

```kotlin
// Settings stored locally
class SettingsManager(context: Context) {
    private val prefs = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
    
    // Theme
    var isDarkMode: Boolean
        get() = prefs.getBoolean("dark_mode", false)
        set(value) = prefs.edit().putBoolean("dark_mode", value).apply()
    
    // Language
    var currentLanguage: String
        get() = prefs.getString("language", "en") ?: "en"
        set(value) = prefs.edit().putString("language", value).apply()
    
    // Notifications
    var notificationsEnabled: Boolean
        get() = prefs.getBoolean("notifications_enabled", true)
        set(value) = prefs.edit().putBoolean("notifications_enabled", value).apply()
    
    // Cache clearing
    fun clearCache() {
        // Implementation details
    }
}
```

---

## Navigation Flow

### Complete App Navigation Graph

```
┌────────────────────────────────────────────────────────┐
│                    APP NAVIGATION                      │
└────────────────────────────────────────────────────────┘

ENTRY POINT:
  SplashActivity
     ↓
     └─ Auth Token Valid?
        ├─ YES → MainActivity
        │         (contains bottom nav)
        │
        └─ NO → AuthActivity (nav_auth.xml)
                 ├─ LoginFragment
                 ├─ RegisterFragment
                 ├─ ForgotPasswordFragment
                 └─ EmailVerificationWaitFragment

MAIN APP (MainActivity with BottomNavigation):
  ┌─ nav_main.xml
  └─ Bottom Nav destinations:
     │
     ├─ HOME (HomeFragment)
     │   ├─ StudentHomeFragment
     │   ├─ TeacherHomeFragment (if teacher)
     │   └─ AdminDashboardFragment (if admin)
     │
     ├─ CLASSES (ClassListFragment)
     │   ├─ ClassDetailActivity (each class)
     │   │   ├─ FeedFragment
     │   │   ├─ StudentsFragment
     │   │   ├─ MaterialsFragment
     │   │   └─ ClassSettingsFragment
     │   ├─ CreateClassBottomSheet
     │   └─ JoinClassBottomSheet
     │
     ├─ ASSIGNMENTS (AssignmentsFragment)
     │   ├─ AssignmentDetailFragment
     │   ├─ CreateAssignmentFragment (teacher only)
     │   ├─ GradeSubmissionFragment (teacher)
     │   └─ SubmissionListFragment (teacher)
     │
     ├─ ATTENDANCE (AttendanceFragment)
     │   ├─ QR scan interface
     │   ├─ AttendanceHistoryFragment
     │   └─ TeacherSessionFragment
     │
     └─ PROFILE (ProfileFragment)
         ├─ UserDetailViewFragment
         └─ SettingsFragment

ADMIN-ONLY (if user is admin):
  ├─ AdminClassesFragment
  ├─ UserManagementFragment
  └─ RoleChangeHistoryFragment
```

### Navigation Code

```kotlin
// MainActivity with bottom navigation
class MainActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        // Setup bottom navigation
        val navView = findViewById<BottomNavigationView>(R.id.bottom_nav)
        val navController = findNavController(R.id.nav_host_fragment)
        
        // Connect bottom nav to nav graph
        navView.setupWithNavController(navController)
        
        // Show/hide admin options based on role
        val userRole = getCurrentUserRole()
        if (userRole == UserRole.ADMIN) {
            navView.menu.add(0, R.id.nav_admin, 4, "Admin")
        }
    }
}

// Navigate between fragments
navController.navigate(R.id.action_homeFragment_to_classDetailActivity, 
    Bundle().apply { putString("classId", classId) })
```

---

## Utilities & Support Systems

### UI Utilities

**Path**: `app/src/main/java/com/syed/classconnect/ui/util/`

| Utility | File | Purpose |
|---------|------|---------|
| **UiState** | `UiState.kt` | Sealed class for Loading/Success/Error states |
| **SkeletonLoadingHelper** | `SkeletonLoadingHelper.kt` | Generate loading placeholders |
| **AnimationUtils** | `AnimationUtils.kt` | Smooth transitions & animations |
| **I18nHelper** | `I18nHelper.kt` | Internationalization (multi-language) |
| **AccessibilityHelper** | `AccessibilityHelper.kt` | TalkBack support |
| **OfflineIndicatorManager** | `OfflineIndicatorManager.kt` | Show offline status |
| **GlobalSearchManager** | `GlobalSearchManager.kt` | Cross-app search |

#### UiState Pattern

```kotlin
// Sealed class for all possible UI states
sealed class UiState<out T> {
    data class Success<T>(val data: T) : UiState<T>()
    data class Error<T>(val message: String) : UiState<T>()
    class Loading<T> : UiState<T>()
    class Idle<T> : UiState<T>()
}

// Usage
when (uiState) {
    is Loading -> showProgressBar()
    is Success -> showData(uiState.data)
    is Error -> showError(uiState.message)
    is Idle -> {}
}
```

#### Skeleton Loading

```kotlin
// Generate loading placeholders
class SkeletonLoadingHelper(context: Context) {
    fun createSkeletonCard(): View {
        return CardView(context).apply {
            layoutParams = RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.MATCH_PARENT,
                dpToPx(100)
            )
            setCardBackgroundColor(Color.parseColor("#E0E0E0"))
            radius = dpToPx(8).toFloat()
        }
    }
}

// Usage
recyclerView.adapter = SkeletonAdapter(count = 5)
// After loading completes:
recyclerView.adapter = RealAdapter(data)
```

### Core Utilities

**Path**: `app/src/main/java/com/syed/classconnect/util/`

| Utility | File | Purpose |
|---------|------|---------|
| **NetworkResult** | `NetworkResult.kt` | Sealed class for network operations |
| **ValidationUtils** | `ValidationUtils.kt` | Email, password, input validation |
| **DateUtils** | `DateUtils.kt` | Date/time formatting & calculations |
| **Extensions** | `Extensions.kt` | Kotlin extension functions |
| **Constants** | `Constants.kt` | App-wide constants |
| **BiometricHelper** | `BiometricHelper.kt` | Fingerprint/Face recognition |
| **PermissionManager** | `PermissionManager.kt` | Runtime permissions |
| **ErrorMessageProvider** | `ErrorMessageProvider.kt` | User-friendly error messages |
| **CoroutineExceptionHandling** | `CoroutineExceptionHandling.kt` | Global exception handler |

#### Network Result

```kotlin
// Sealed class for network operations
sealed class NetworkResult<out T> {
    data class Success<T>(val data: T) : NetworkResult<T>()
    data class Error(val exception: Exception) : NetworkResult<Nothing>()
    class Loading<T> : NetworkResult<T>()
}

// Extension functions
fun <T> NetworkResult<T>.getOrNull(): T? = 
    if (this is NetworkResult.Success) data else null

fun <T> NetworkResult<T>.isSuccess(): Boolean = 
    this is NetworkResult.Success
```

#### Validation Utils

```kotlin
object ValidationUtils {
    
    fun validateEmail(email: String): String? = when {
        email.isEmpty() -> "Email is required"
        !email.contains("@") -> "Invalid email format"
        else -> null
    }
    
    fun validatePassword(password: String): String? = when {
        password.isEmpty() -> "Password is required"
        password.length < 8 -> "Password must be at least 8 characters"
        !password.any { it.isUpperCase() } -> "Password must contain uppercase letter"
        !password.any { it.isDigit() } -> "Password must contain a digit"
        else -> null
    }
    
    fun validateLogin(email: String, password: String): List<String> {
        val errors = mutableListOf<String>()
        validateEmail(email)?.let { errors.add(it) }
        validatePassword(password)?.let { errors.add(it) }
        return errors
    }
}
```

---

## Implementation Best Practices

### 1. **Fragment Best Practices**

```kotlin
// ✅ DO
class MyFragment : Fragment() {
    
    // Use viewModels() delegation
    private val viewModel: MyViewModel by viewModels()
    
    // Use viewLifecycleOwner for lifecycle-aware operations
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Collect with lifecycle awareness
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.data.collect { data ->
                    updateUI(data)
                }
            }
        }
    }
    
    // Properly handle arguments
    companion object {
        fun newInstance(arg: String) = MyFragment().apply {
            arguments = Bundle().apply { putString("arg", arg) }
        }
    }
}

// ❌ DON'T
class BadFragment : Fragment() {
    
    // Don't hold activity references
    private lateinit var activity: Activity  // Memory leak!
    
    // Don't use GlobalScope
    GlobalScope.launch {  // Bad! Can outlive fragment
        loadData()
    }
    
    // Don't set listeners without cleanup
    button.setOnClickListener { ... }  // No unsubscribe
}
```

### 2. **ViewModel Best Practices**

```kotlin
// ✅ DO
class GoodViewModel(
    private val repository: DataRepository
) : ViewModel() {
    
    private val _state = MutableStateFlow<UiState<Data>>(Loading)
    val state: StateFlow<UiState<Data>> = _state.asStateFlow()
    
    init {
        loadData()
    }
    
    private fun loadData() {
        viewModelScope.launch {
            try {
                _state.value = Loading
                val data = repository.getData()
                _state.value = Success(data)
            } catch (e: Exception) {
                _state.value = Error(e.message ?: "Unknown error")
            }
        }
    }
    
    // Public methods for user actions
    fun refresh() {
        loadData()
    }
}

// ❌ DON'T
class BadViewModel : ViewModel() {
    
    // Don't use mutable public variables
    public var data: MutableLiveData<String> = MutableLiveData()  // Expose internals!
    
    // Don't leak views
    var button: Button? = null  // Memory leak!
    
    // Don't use Long-lived listeners
    lifecycleScope.launch {  // Bad scope!
        continuouslyFetchData()
    }
}
```

### 3. **RecyclerView & Adapter Best Practices**

```kotlin
// ✅ DO - Use ListAdapter with DiffUtil
class GoodAdapter : ListAdapter<Item, ItemViewHolder>(ItemDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_layout, parent, false)
        )
    }
    
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(getItem(position))  // Only binds visible items
    }
}

class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(item: Item) {
        // Bind only necessary data
        itemView.title.text = item.title
        itemView.description.text = item.description
    }
}

class ItemDiffCallback : DiffUtil.ItemCallback<Item>() {
    override fun areItemsTheSame(old: Item, new: Item) = old.id == new.id
    override fun areContentsTheSame(old: Item, new: Item) = old == new
}

// ❌ DON'T - Full data swap on every update
class BadAdapter : RecyclerView.Adapter<ItemViewHolder>() {
    
    var items: List<Item> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()  // Rebinds ALL items!
        }
    
    // This causes all items to re-render, even unchanged ones
}
```

### 4. **Memory Management**

```kotlin
// ✅ DO - Use lifecycleScope for cleanup
viewLifecycleOwner.lifecycleScope.launch {
    repeatOnLifecycle(Lifecycle.State.STARTED) {
        // Automatically stops when paused, resumes when started
        viewModel.data.collect { updateUI(it) }
    }
}

// ✅ DO - Use Hilt for dependency injection
@Singleton
class AppRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val prefs: SharedPreferences
) { ... }

// ❌ DON'T - Manual lifecycle management
var listener: ValueEventListener? = null
override fun onStart() {
    listener = // create listener
}
override fun onStop() {
    listener?.let { remove() }  // Easy to forget!
}
```

---

## Testing & Quality

### Unit Testing Structure

```kotlin
// Test ViewModel
class HomeViewModelTest {
    
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()
    
    private val testDispatcher = StandardTestDispatcher()
    
    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }
    
    @Test
    fun loadHomeData_success() = runTest {
        // Arrange
        val mockClasses = listOf(ClassModel("1", "Math"))
        val mockRepo = mockk<ClassRepository>()
        coEvery { mockRepo.getTodayClasses() } returns mockClasses
        
        val viewModel = HomeViewModel(mockRepo)
        
        // Act
        advanceUntilIdle()
        
        // Assert
        assert(viewModel.homeState.value is Success)
    }
}
```

### Integration Testing

```kotlin
// Test Fragment interactions
@RunWith(AndroidJUnit4::class)
class StudentHomeFragmentTest {
    
    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)
    
    @Test
    fun displaysTodayClasses() {
        // Setup test data
        val testData = listOf(
            ClassModel("1", "Math", "10:00-11:00")
        )
        
        // Launch fragment
        activityRule.scenario.onActivity { activity ->
            val fragment = StudentHomeFragment()
            activity.supportFragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commitNow()
        }
        
        // Verify UI
        onView(withText("Math 101"))
            .check(matches(isDisplayed()))
    }
}
```

---

## Summary & Key Takeaways

### ✅ What Makes ClassConnect Robust

| Aspect | Implementation |
|--------|-----------------|
| **Performance** | Lazy loading, pagination, efficient RecyclerView adapters |
| **State Management** | StateFlow for reactive updates, ViewModel survives config changes |
| **Navigation** | Navigation Component with clear flow graphs |
| **Memory Safety** | Lifecycle-aware coroutines, proper Fragment lifecycle |
| **UI Polish** | Design tokens, animations, dark mode support |
| **Accessibility** | Content descriptions, TalkBack support, high contrast |

### 🚀 Performance Optimizations Implemented

```
1. Pagination (Load 20 items at a time, not 1000)
2. Lazy Loading Images (Glide with caching)
3. DiffUtil for RecyclerView (Only update changed items)
4. ViewPager Caching (Pre-load adjacent tabs)
5. Skeleton Loading (Show placeholder while fetching)
6. Coroutine Scope (Automatic cleanup with lifecycle)
7. StateFlow vs LiveData (More efficient)
8. Design Tokens (Reusable, cached resources)
```

### 📱 Modules at a Glance

| Module | Purpose | Key Features |
|--------|---------|--------------|
| **Admin** | User & class management | Role changes, audit logs |
| **Attendance** | QR code scanning | Real-time verification |
| **Auth** | Login/registration | Email verification, approval flow |
| **Classes** | Browse & join | Lazy-loaded tabs, feed system |
| **Home** | Quick dashboard | Paginated lists, news cards |
| **Quiz** | Test taking | Timed questions, progress visualization |
| **Splash** | App startup | Animated introduction, auth check |
| **Settings** | User preferences | Theme, language, notifications |

### 🔧 Next Steps for Production

1. **Complete Testing**: Aim for 70% unit test coverage
2. **Performance Monitoring**: Track frame drops, memory usage
3. **Error Handling**: Global exception handler, retry logic
4. **Analytics**: Track user flows and crashes
5. **Security**: API key management, data encryption
6. **Offline Support**: Cache critical data locally

---

## Document History

| Date | Author | Changes |
|------|--------|---------|
| 2026-04-10 | Syed Sardar Valli | Complete rewrite with lazy loading deep dive |
| 2026-04-06 | (Previous) | Initial component documentation |

---

*This document is a living guide. Updates and clarifications are welcomed based on team feedback and evolving architecture decisions.*

