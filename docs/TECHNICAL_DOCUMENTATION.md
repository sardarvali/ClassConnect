# ClassConnect - Complete Technical Documentation

> **Date:** April 10, 2026  
> **Author:** Syed Sardar Valli  
> **Format:** Comprehensive Technical Reference  
> **Audience:** Developers, Architects, Technical Leads

---

## Table of Contents

1. [System Architecture](#system-architecture)
2. [Technology Stack](#technology-stack)
3. [Project Structure](#project-structure)
4. [Design Patterns & Principles](#design-patterns--principles)
5. [Core Components](#core-components)
6. [Module Architecture](#module-architecture)
7. [Data Layer & Firebase](#data-layer--firebase)
8. [State Management](#state-management)
9. [Navigation & Routing](#navigation--routing)
10. [Performance Optimization](#performance-optimization)
11. [Security Implementation](#security-implementation)
12. [Testing Strategy](#testing-strategy)
13. [Dependency Injection](#dependency-injection)
14. [Error Handling](#error-handling)
15. [API Integration](#api-integration)

---

## 1. System Architecture

### High-Level Architecture Diagram

```
┌─────────────────────────────────────────────────────────────────┐
│                    PRESENTATION LAYER                           │
│  ┌──────────────┬──────────────┬──────────────┬───────────────┐ │
│  │   Activity   │   Fragment   │  Dialog      │  BottomSheet  │ │
│  │  Container   │  UI Display  │  Modal UI    │  Overlay UI   │ │
│  └──────────────┴──────────────┴──────────────┴───────────────┘ │
│                          │                                       │
│  ┌──────────────┬────────▼────────┬──────────────┬────────────┐ │
│  │  Adapter     │   ViewModel     │  State Flow  │  Lifecycle │ │
│  │  RecyclerView│  Business Logic │  Reactive   │  Aware     │ │
│  └──────────────┴────────┬────────┴──────────────┴────────────┘ │
└─────────────────────────┼──────────────────────────────────────┘
                          │
┌─────────────────────────▼──────────────────────────────────────┐
│                  BUSINESS LOGIC LAYER                          │
│  ┌──────────────┬──────────────┬──────────────────────────┐   │
│  │ Use Cases    │  Repository  │  Repositories Pattern   │   │
│  │ Orchestration│  Abstraction │  Single Source of Truth │   │
│  └──────────────┴──────────────┴──────────────────────────┘   │
└─────────────────────────┬──────────────────────────────────────┘
                          │
┌─────────────────────────▼──────────────────────────────────────┐
│                    DATA LAYER                                  │
│  ┌─────────────────┬──────────────┬───────────────────────┐   │
│  │  Firestore      │  Firebase    │  Local Preferences    │   │
│  │  Real-time DB   │  Storage     │  Encrypted Cache      │   │
│  │  Collections    │  Files       │  SharedPreferences    │   │
│  └─────────────────┴──────────────┴───────────────────────┘   │
└────────────────────────────────────────────────────────────────┘
```

### Architectural Principles

```
MVVM (Model-View-ViewModel)
├── Model: Data classes representing domain entities
├── View: UI components (Fragments, Activities)
├── ViewModel: Manages UI state and business logic
└── Lifecycle-aware to prevent memory leaks

Clean Architecture
├── Presentation Layer: UI and navigation
├── Domain Layer: Business rules and use cases
├── Data Layer: Repository and data sources
└── Dependency direction: Inward (no outward dependencies)

Reactive Programming
├── StateFlow for observable state
├── Coroutines for async operations
├── Flow operators for data transformation
└── Lifecycle-scoped collection (no leaks)
```

---

## 2. Technology Stack

### Framework & Language
```
Language:           Kotlin 1.9+
Min SDK:            API 26 (Android 8.0)
Target SDK:         API 34 (Android 14)
Compile SDK:        API 34
Build System:       Gradle 8.x with Kotlin DSL
```

### Core Android Libraries
```
AndroidX
├── androidx.appcompat:appcompat:1.6.x
├── androidx.lifecycle:lifecycle-runtime:2.6.x
├── androidx.lifecycle:lifecycle-viewmodel-compose:2.6.x
├── androidx.activity:activity-ktx:1.7.x
├── androidx.fragment:fragment-ktx:1.6.x
├── androidx.navigation:navigation-fragment-ktx:2.7.x
├── androidx.navigation:navigation-ui-ktx:2.7.x
└── androidx.viewpager2:viewpager2:1.1.x

Material Design
├── com.google.android.material:material:1.10.x
└── Material Design 3 Components & Theming

RecyclerView
├── androidx.recyclerview:recyclerview:1.3.x
└── androidx.recyclerview:recyclerview-selection:1.2.x

Constraint Layout
└── androidx.constraintlayout:constraintlayout:2.1.x
```

### State Management & Reactivity
```
Kotlin Coroutines
├── kotlinx.coroutines:coroutines-core:1.7.x
├── kotlinx.coroutines:coroutines-android:1.7.x
└── kotlinx.coroutines:coroutines-playservices:1.7.x

Jetpack Compose (Future)
├── androidx.compose.ui:ui:1.6.x
├── androidx.compose.material3:material3:1.x
└── androidx.compose.runtime:runtime-livedata:1.6.x

StateFlow & Flow
├── Part of Kotlin Coroutines
├── Hot flows for state
└── Cold flows for events
```

### Firebase Integration
```
Firebase Cloud Firestore
├── com.google.firebase:firebase-firestore-ktx:24.x
├── Real-time database
├── Document-oriented (NoSQL)
└── Real-time listeners & snapshots

Firebase Authentication
├── com.google.firebase:firebase-auth-ktx:22.x
├── Email/password auth
├── Token management
└── Session handling

Firebase Storage
├── com.google.firebase:firebase-storage-ktx:20.x
├── File uploads
├── Image storage
└── Document storage

Firebase Cloud Messaging
├── com.google.firebase:firebase-messaging-ktx:23.x
├── Push notifications
├── Topic subscriptions
└── Message handling
```

### Dependency Injection
```
Hilt
├── com.google.dagger:hilt-android:2.47
├── Annotation-based DI
├── ViewModel injection
├── Repository injection
└── Module organization
```

### Networking & API
```
Retrofit 2
├── com.squareup.retrofit2:retrofit:2.10.x
├── REST API client
├── Coroutine support
└── Interceptors for auth

OkHttp
├── com.squareup.okhttp3:okhttp:4.11.x
├── HTTP client
├── Request/response logging
└── Interceptor chain

Moshi
├── com.squareup.moshi:moshi-kotlin:1.15.x
├── JSON serialization
└── Type-safe parsing
```

### Image Loading & Caching
```
Glide
├── com.github.bumptech.glide:glide:4.16.x
├── Lazy image loading
├── Memory & disk caching
├── Placeholder & error handling
└── GIF support

Coil (Alternative)
├── io.coil-kt:coil:2.5.x
├── Coroutine-first design
├── Lightweight alternative
└── Modern async handling
```

### Camera & Computer Vision
```
CameraX
├── androidx.camera:camera-core:1.3.x
├── androidx.camera:camera-camera2:1.3.x
├── androidx.camera:camera-lifecycle:1.3.x
└── Modern camera API with lifecycle

ML Kit
├── com.google.mlkit:barcode-scanning:17.x
├── QR code detection
├── Real-time processing
└── No server calls
```

### Testing Libraries
```
JUnit 4
├── junit:junit:4.13.x
└── Unit test framework

Mockk
├── io.mockk:mockk:1.13.x
├── Kotlin-first mocking
└── Coroutine support

Espresso
├── androidx.test.espresso:espresso-core:3.5.x
├── UI testing framework
└── Synchronization support

Robolectric
├── org.robolectric:robolectric:4.11.x
├── Android framework simulation
└── Unit test android code
```

### Logging & Debugging
```
Timber
├── com.jakewharton.timber:timber:5.0.x
├── Structured logging
└── Debug tree for logs

Android Studio Profiler
├── Built-in memory profiler
├── CPU profiler
└── Network profiler
```

### Version Management
```
Gradle Version Catalog
├── libs.versions.toml
├── Centralized version management
├── Shared across modules
└── Semantic versioning
```

---

## 3. Project Structure

### Directory Organization

```
classconnect/
│
├── app/
│   ├── build.gradle.kts                 # App-level build config
│   ├── proguard-rules.pro               # ProGuard obfuscation
│   ├── google-services.json             # Firebase config
│   │
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml      # App manifest
│       │   │
│       │   ├── java/com/syed/classconnect/
│       │   │   ├── ClassConnectApp.kt   # Application class
│       │   │   │
│       │   │   ├── ui/                  # PRESENTATION LAYER
│       │   │   │   ├── admin/           # Admin module
│       │   │   │   │   ├── AdminClassesFragment.kt
│       │   │   │   │   ├── AdminClassesViewModel.kt
│       │   │   │   │   ├── AdminDashboardFragment.kt
│       │   │   │   │   ├── UserManagementFragment.kt
│       │   │   │   │   ├── UserDetailFragment.kt
│       │   │   │   │   ├── RoleChangeHistoryFragment.kt
│       │   │   │   │   └── *Adapter.kt
│       │   │   │   │
│       │   │   │   ├── attendance/      # Attendance module
│       │   │   │   │   ├── AttendanceFragment.kt
│       │   │   │   │   ├── AttendanceViewModel.kt
│       │   │   │   │   ├── QrCodeAnalyzer.kt
│       │   │   │   │   └── AttendanceVerification.kt
│       │   │   │   │
│       │   │   │   ├── auth/            # Authentication
│       │   │   │   │   ├── AuthActivity.kt
│       │   │   │   │   ├── LoginFragment.kt
│       │   │   │   │   ├── RegisterFragment.kt
│       │   │   │   │   ├── ForgotPasswordFragment.kt
│       │   │   │   │   ├── EmailVerificationWaitFragment.kt
│       │   │   │   │   └── PendingApprovalFragment.kt
│       │   │   │   │
│       │   │   │   ├── classes/         # Classes module
│       │   │   │   │   ├── ClassListFragment.kt
│       │   │   │   │   ├── ClassDetailActivity.kt
│       │   │   │   │   ├── StudentsFragment.kt
│       │   │   │   │   ├── FeedFragment.kt
│       │   │   │   │   └── *Adapter.kt
│       │   │   │   │
│       │   │   │   ├── home/            # Home dashboards
│       │   │   │   │   ├── StudentHomeFragment.kt
│       │   │   │   │   ├── TeacherHomeFragment.kt
│       │   │   │   │   ├── AdminDashboardFragment.kt
│       │   │   │   │   ├── HomeViewModel.kt
│       │   │   │   │   └── *Adapter.kt
│       │   │   │   │
│       │   │   │   ├── assignments/     # Assignments
│       │   │   │   │   ├── AssignmentsFragment.kt
│       │   │   │   │   ├── AssignmentDetailFragment.kt
│       │   │   │   │   ├── CreateAssignmentFragment.kt
│       │   │   │   │   ├── AssignmentsViewModel.kt
│       │   │   │   │   └── *Adapter.kt
│       │   │   │   │
│       │   │   │   ├── quiz/            # Quiz system
│       │   │   │   │   ├── QuizListFragment.kt
│       │   │   │   │   ├── CreateQuizFragment.kt
│       │   │   │   │   ├── QuizAttemptActivity.kt
│       │   │   │   │   ├── QuizResultFragment.kt
│       │   │   │   │   ├── QuizViewModel.kt
│       │   │   │   │   └── *Adapter.kt
│       │   │   │   │
│       │   │   │   ├── splash/          # Splash screen
│       │   │   │   │   ├── SplashActivity.kt
│       │   │   │   │   ├── SplashCanvasView.kt
│       │   │   │   │   ├── NeuralBackgroundView.kt
│       │   │   │   │   └── SplashAnimationOrchestrator.kt
│       │   │   │   │
│       │   │   │   ├── settings/        # Settings
│       │   │   │   │   ├── SettingsFragment.kt
│       │   │   │   │   └── SettingsManager.kt
│       │   │   │   │
│       │   │   │   ├── util/            # UI utilities
│       │   │   │   │   ├── UiState.kt
│       │   │   │   │   ├── SkeletonLoadingHelper.kt
│       │   │   │   │   ├── AnimationUtils.kt
│       │   │   │   │   └── I18nHelper.kt
│       │   │   │   │
│       │   │   │   └── main/
│       │   │   │       ├── MainActivity.kt
│       │   │   │       └── NavigationManager.kt
│       │   │   │
│       │   │   ├── data/                # DATA LAYER
│       │   │   │   ├── model/
│       │   │   │   │   ├── User.kt
│       │   │   │   │   ├── ClassModel.kt
│       │   │   │   │   ├── Assignment.kt
│       │   │   │   │   ├── Attendance.kt
│       │   │   │   │   ├── Quiz.kt
│       │   │   │   │   ├── Announcement.kt
│       │   │   │   │   └── Submission.kt
│       │   │   │   │
│       │   │   │   ├── repository/
│       │   │   │   │   ├── AuthRepository.kt
│       │   │   │   │   ├── ClassRepository.kt
│       │   │   │   │   ├── AssignmentRepository.kt
│       │   │   │   │   ├── AttendanceRepository.kt
│       │   │   │   │   ├── QuizRepository.kt
│       │   │   │   │   ├── UserRepository.kt
│       │   │   │   │   └── SettingsRepository.kt
│       │   │   │   │
│       │   │   │   ├── remote/
│       │   │   │   │   ├── FirestoreClient.kt
│       │   │   │   │   ├── FirebaseAuthClient.kt
│       │   │   │   │   └── RetrofitClient.kt
│       │   │   │   │
│       │   │   │   └── local/
│       │   │   │       ├── SharedPreferencesManager.kt
│       │   │   │       ├── SecurePreferences.kt
│       │   │   │       └── CacheManager.kt
│       │   │   │
│       │   │   ├── util/                # UTILITIES
│       │   │   │   ├── Constants.kt
│       │   │   │   ├── Extensions.kt
│       │   │   │   ├── ValidationUtils.kt
│       │   │   │   ├── DateUtils.kt
│       │   │   │   ├── NetworkResult.kt
│       │   │   │   ├── PermissionManager.kt
│       │   │   │   ├── BiometricHelper.kt
│       │   │   │   ├── AccessibilityHelper.kt
│       │   │   │   └── CoroutineExceptionHandling.kt
│       │   │   │
│       │   │   ├── di/                  # DEPENDENCY INJECTION
│       │   │   │   ├── AppModule.kt
│       │   │   │   ├── RepositoryModule.kt
│       │   │   │   ├── DataSourceModule.kt
│       │   │   │   └── NetworkModule.kt
│       │   │   │
│       │   │   └── service/
│       │   │       ├── FirebaseMessagingService.kt
│       │   │       ├── NotificationService.kt
│       │   │       └── SyncService.kt
│       │   │
│       │   └── res/
│       │       ├── layout/              # XML layouts
│       │       ├── drawable/            # Vector drawables
│       │       ├── values/              # Resources
│       │       ├── values-night/        # Dark mode
│       │       └── anim/                # Animations
│       │
│       ├── test/
│       │   └── java/com/syed/classconnect/
│       │       ├── viewmodel/           # ViewModel tests
│       │       ├── repository/          # Repository tests
│       │       ├── util/                # Utility tests
│       │       └── integration/         # Integration tests
│       │
│       └── androidTest/
│           └── java/com/syed/classconnect/
│               ├── ui/                  # UI tests
│               └── features/            # Feature tests
│
├── build.gradle.kts                     # Project-level build
├── settings.gradle.kts                  # Module settings
├── gradle.properties                    # Gradle properties
│
├── gradle/
│   ├── libs.versions.toml               # Version catalog
│   └── wrapper/
│
└── docs/                                # Documentation
    ├── syedexplain.md
    ├── QUICK_REFERENCE.md
    ├── MODULE_OVERVIEW.md
    ├── INDEX.md
    └── ... (other docs)
```

---

## 4. Design Patterns & Principles

### MVVM Pattern Implementation

```kotlin
// MODEL: Data class representing domain entity
data class ClassModel(
    val id: String,
    val name: String,
    val teacherId: String,
    val studentCount: Int,
    val createdAt: Long
)

// VIEWMODEL: Manages UI state and business logic
class ClassesViewModel(
    private val classRepository: ClassRepository
) : ViewModel() {
    
    // Private mutable state
    private val _classesState = MutableStateFlow<UiState<List<ClassModel>>>(Loading)
    
    // Public immutable state for UI
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
                _classesState.value = Error(e.message ?: "Unknown error")
            }
        }
    }
}

// VIEW: Fragment displays data from ViewModel
class ClassesFragment : Fragment() {
    private val viewModel: ClassesViewModel by viewModels()
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.classesState.collect { state ->
                    when (state) {
                        is Loading -> showLoadingState()
                        is Success -> displayClasses(state.data)
                        is Error -> showErrorMessage(state.message)
                    }
                }
            }
        }
    }
}
```

### Repository Pattern

```kotlin
// Repository provides single source of truth
interface ClassRepository {
    suspend fun getAllClasses(): List<ClassModel>
    suspend fun getClassById(id: String): ClassModel
    suspend fun createClass(classModel: ClassModel): Result<ClassModel>
    suspend fun updateClass(classModel: ClassModel): Result<Unit>
    fun observeClasses(): Flow<List<ClassModel>>
}

// Implementation with Firebase
class ClassRepositoryImpl(
    private val firestoreClient: FirestoreClient,
    private val localCache: CacheManager
) : ClassRepository {
    
    override suspend fun getAllClasses(): List<ClassModel> = withContext(Dispatchers.IO) {
        // Try local cache first
        val cached = localCache.getClasses()
        if (cached.isNotEmpty()) {
            return@withContext cached
        }
        
        // Fetch from Firestore
        val classes = firestoreClient.getClasses()
        
        // Cache locally
        localCache.saveClasses(classes)
        
        return@withContext classes
    }
    
    override fun observeClasses(): Flow<List<ClassModel>> = flow {
        firestoreClient.observeClasses().collect { classes ->
            localCache.saveClasses(classes)
            emit(classes)
        }
    }
}
```

### Reactive Programming with Coroutines

```kotlin
// StateFlow for observable state (hot flow)
class HomeViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<HomeUiState>(Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    init {
        viewModelScope.launch {
            // Combine multiple data sources
            combine(
                repository.getTodayClasses(),
                repository.getUpcomingDeadlines(),
                repository.getLatestNews()
            ) { classes, deadlines, news ->
                Success(HomeUiState(classes, deadlines, news))
            }.collect { state ->
                _uiState.value = state
            }
        }
    }
}

// Flow for one-time events (cold flow)
class LoginViewModel : ViewModel() {
    private val _loginEvent = MutableSharedFlow<LoginEvent>()
    val loginEvent: SharedFlow<LoginEvent> = _loginEvent.asSharedFlow()
    
    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                val result = authRepository.login(email, password)
                _loginEvent.emit(LoginEvent.Success(result))
            } catch (e: Exception) {
                _loginEvent.emit(LoginEvent.Error(e.message))
            }
        }
    }
}
```

### Sealed Classes for Type Safety

```kotlin
// UiState for comprehensive state management
sealed class UiState<out T> {
    data class Success<T>(val data: T) : UiState<T>()
    data class Error<T>(val message: String) : UiState<T>()
    class Loading<T> : UiState<T>()
    class Idle<T> : UiState<T>()
}

// Usage with when expression (compiler checks all cases)
when (uiState) {
    is Loading -> showProgressBar()
    is Success -> displayData(uiState.data)
    is Error -> showError(uiState.message)
    is Idle -> {}
}
```

---

## 5. Core Components

### Fragment with Lifecycle Management

```kotlin
// Proper Fragment implementation
class StudentHomeFragment : Fragment() {
    
    // Lazy initialization
    private lateinit var binding: FragmentStudentHomeBinding
    private val viewModel: HomeViewModel by viewModels()
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStudentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView()
        setupObservers()
    }
    
    private fun setupObservers() {
        // Lifecycle-aware collection (no leaks)
        viewLifecycleOwner.lifecycleScope.launch {
            // Only collect when STARTED
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.homeState.collect { state ->
                    when (state) {
                        is Loading -> showLoadingSkeleton()
                        is Success -> updateUI(state.data)
                        is Error -> showError(state.message)
                    }
                }
            }
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        // Binding reference cleared automatically
        _binding = null
    }
}
```

### ViewModel with Coroutine Scopes

```kotlin
class ClassesViewModel(
    private val classRepository: ClassRepository,
    private val errorHandler: ErrorHandler
) : ViewModel() {
    
    private val _classesState = MutableStateFlow<UiState<List<ClassModel>>>(Loading)
    val classesState: StateFlow<UiState<List<ClassModel>>> = _classesState.asStateFlow()
    
    private val _events = MutableSharedFlow<ClassEvent>()
    val events: SharedFlow<ClassEvent> = _events.asSharedFlow()
    
    private var currentPage = 0
    private val pageSize = 25
    
    init {
        loadClasses()
    }
    
    private fun loadClasses() {
        // viewModelScope automatically cancels when ViewModel destroyed
        viewModelScope.launch {
            try {
                _classesState.value = Loading
                val classes = classRepository.getClasses(page = currentPage, pageSize = pageSize)
                _classesState.value = Success(classes)
            } catch (e: Exception) {
                val message = errorHandler.getErrorMessage(e)
                _classesState.value = Error(message)
                _events.emit(ClassEvent.ShowError(message))
            }
        }
    }
    
    fun loadMoreClasses() {
        viewModelScope.launch {
            try {
                val moreClasses = classRepository.getClasses(
                    page = currentPage + 1,
                    pageSize = pageSize
                )
                val currentData = (_classesState.value as? Success)?.data ?: return@launch
                val updated = currentData + moreClasses
                _classesState.value = Success(updated)
                currentPage++
            } catch (e: Exception) {
                val message = errorHandler.getErrorMessage(e)
                _events.emit(ClassEvent.ShowError(message))
            }
        }
    }
}
```

### Adapter with DiffUtil

```kotlin
// Type-safe adapter using ListAdapter
class ClassAdapter : ListAdapter<ClassModel, ClassViewHolder>(ClassDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClassViewHolder {
        val binding = ItemClassBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ClassViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: ClassViewHolder, position: Int) {
        holder.bind(getItem(position))
        
        // Trigger pagination when near end
        if (position >= itemCount - 5) {
            onLoadMore?.invoke()
        }
    }
    
    private var onLoadMore: (() -> Unit)? = null
    
    fun setOnLoadMoreListener(listener: () -> Unit) {
        onLoadMore = listener
    }
}

// ViewHolder with binding
class ClassViewHolder(private val binding: ItemClassBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(classModel: ClassModel) {
        binding.apply {
            className.text = classModel.name
            teacherName.text = classModel.teacherName
            studentCount.text = "${classModel.studentCount} students"
            
            // Lazy load avatar image
            Glide.with(itemView)
                .load(classModel.teacherImageUrl)
                .placeholder(R.drawable.ic_placeholder_avatar)
                .into(teacherAvatar)
        }
    }
}

// DiffUtil callback for efficient updates
class ClassDiffCallback : DiffUtil.ItemCallback<ClassModel>() {
    override fun areItemsTheSame(old: ClassModel, new: ClassModel) = old.id == new.id
    override fun areContentsTheSame(old: ClassModel, new: ClassModel) = old == new
}
```

---

## 6. Module Architecture

### Admin Module

```
RESPONSIBILITY: User management, role assignment, audit logs

FILES:
├── AdminClassesFragment.kt          - Manage classes UI
├── AdminClassesViewModel.kt         - Class management state
├── UserManagementFragment.kt        - User list UI
├── UserManagementViewModel.kt       - User management state
├── UserDetailFragment.kt            - Single user detail/edit
├── UserDetailViewModel.kt           - User detail state
├── RoleChangeHistoryFragment.kt     - Audit log UI
├── RoleChangeHistoryViewModel.kt    - Audit log state
├── AssignTeacherBottomSheet.kt      - Teacher assignment modal
├── AdminClassAdapter.kt             - Class list adapter
├── UserManagementAdapter.kt         - User list adapter
└── RoleChangeLogAdapter.kt          - Audit log adapter

DATA FLOW:
1. AdminClassesFragment calls viewModel.loadClasses()
2. ViewModel fetches from UserRepository
3. Repository queries Firestore: WHERE role = "admin"
4. Data returned and cached locally
5. Adapter renders with DiffUtil
6. User taps class → navigates to ClassDetailActivity

PAGINATION:
- Load 30 classes per page
- Load 30 users per page
- Load 50 audit entries per page

FIRESTORE QUERIES:
- db.collection("classes").orderBy("createdAt", DESC).limit(30)
- db.collection("users").where("role", "==", "teacher").limit(30)
- db.collection("roleChangeLog").orderBy("timestamp", DESC).limit(50)
```

### Attendance Module

```
RESPONSIBILITY: QR code scanning, attendance marking, real-time updates

FILES:
├── AttendanceFragment.kt            - QR scanner UI
├── AttendanceViewModel.kt           - Scanner state
├── QrCodeAnalyzer.kt                - ML Kit QR detection
├── AttendanceVerification.kt        - Verification logic
├── AttendanceHistoryAdapter.kt      - History list adapter
└── TeacherSessionFragment.kt        - Teacher session view

TECHNOLOGY:
- CameraX for camera access
- ML Kit for QR detection
- Firestore listeners for real-time updates

FLOW:
1. User opens AttendanceFragment
2. CameraX initializes camera feed
3. ML Kit scans video frames for QR codes
4. QR detected → Parse session ID from data
5. Call verifyAndMarkAttendance(sessionId, token)
6. Server verifies: Is user in this class? Is session active?
7. If valid → Firestore.collection("attendance").add(record)
8. Firestore listener on teacher side emits update
9. UI updates present count in real-time

FIRESTORE STRUCTURE:
attendance/
├── sessionId
├── userId
├── classId
├── markedAt (timestamp)
└── status ("present", "absent", "late")

REAL-TIME UPDATES:
- Teacher session has Firestore listener
- When student marks → Listener fires
- Present count updates instantly (no polling)
- Saves bandwidth, instant feedback
```

### Auth Module

```
RESPONSIBILITY: Authentication flow, verification, approval

FILES:
├── AuthActivity.kt                  - Auth container activity
├── AuthViewModel.kt                 - Auth state management
├── LoginFragment.kt                 - Login UI
├── RegisterFragment.kt              - Registration UI
├── ForgotPasswordFragment.kt        - Password recovery
├── EmailVerificationWaitFragment.kt - Verification wait UI
└── PendingApprovalFragment.kt       - Approval wait UI

AUTH FLOW:
1. User enters email/password in LoginFragment
2. ValidationUtils.validateLogin() checks format
3. ViewModel calls authRepository.login()
4. Firebase authenticates via signInWithEmailAndPassword()
5. Returns User object with verification status
6. Check isEmailVerified:
   - If false → Show EmailVerificationWaitFragment
   - If true → Check isApproved:
     - If false → Show PendingApprovalFragment
     - If true → Navigate to MainActivity

VERIFICATION PROCESS:
1. User registers → Firebase sends verification email
2. EmailVerificationWaitFragment polls Firebase every 2 seconds
3. Firebase checks if user clicked verification link
4. On verification → isEmailVerified = true
5. Fragment auto-navigates to approval check

APPROVAL PROCESS:
1. New unverified user goes to PendingApprovalFragment
2. Admin reviews in UserManagementFragment
3. Admin approves → Firestore user.isApproved = true
4. Cloud Function triggers → Updates Firebase Auth claim
5. PendingApprovalFragment polls every 3 seconds
6. When approved → Navigates to MainActivity

SECURITY:
- Passwords hashed by Firebase
- Auth token stored in encrypted SharedPreferences
- Token refreshed automatically
- Biometric login supported (optional)

FIRESTORE SCHEMA:
users/{userId}
├── email
├── name
├── role ("student", "teacher", "admin")
├── isApproved (boolean)
├── isEmailVerified (boolean)
├── createdAt (timestamp)
└── profileImage (url)
```

### Classes Module

```
RESPONSIBILITY: Browse classes, manage content, lazy-loaded tabs

FILES:
├── ClassListFragment.kt             - Classes list UI
├── ClassDetailActivity.kt           - Class detail container
├── ClassDetailViewModel.kt          - Class detail state
├── StudentsFragment.kt              - Class students (Tab 1)
├── FeedFragment.kt                  - Announcements (Tab 2)
├── MaterialsFragment.kt             - Course materials (Tab 3)
├── ClassSettingsFragment.kt         - Settings (Tab 4)
├── ClassAdapter.kt                  - Class list adapter
├── StudentAdapter.kt                - Student list adapter
└── AnnouncementAdapter.kt           - Announcements adapter

TAB SYSTEM (Lazy Loading):
ClassDetailActivity uses ViewPager2 with FragmentStateAdapter:
- Tab 0: FeedFragment
- Tab 1: StudentsFragment
- Tab 2: MaterialsFragment
- Tab 3: ClassSettingsFragment

offscreenPageLimit = 1
- Keeps adjacent tab in memory
- Pre-caches as user swipes
- Smooth tab transition

PAGINATION IN EACH TAB:
Feed Tab:
  - Load 20 announcements per page
  - Pagination when user scrolls near bottom

Students Tab:
  - Load 30 students per page
  - DiffUtil updates only changed items

Materials Tab:
  - Load 25 files per page
  - Lazy load file thumbnails

FIRESTORE QUERIES:
classes/{classId}/announcements
  - orderBy("createdAt", DESC)
  - limit(20)

classes/{classId}/students
  - limit(30)
  - offset((page-1) * 30)

classes/{classId}/materials
  - orderBy("uploadedAt", DESC)
  - limit(25)

REAL-TIME UPDATES:
- FeedFragment listens to announcements collection
- When teacher posts → Firestore emits
- Fragment auto-updates with new announcement
- No manual refresh needed
```

### Home Module

```
RESPONSIBILITY: Quick dashboard with today's classes, deadlines, news

FILES:
├── StudentHomeFragment.kt           - Student dashboard
├── TeacherHomeFragment.kt           - Teacher dashboard
├── AdminDashboardFragment.kt        - Admin dashboard
├── HomeViewModel.kt                 - Dashboard state
├── DashboardCardAdapter.kt          - Dashboard cards
├── TodayClassesAdapter.kt           - Classes list adapter
├── UpcomingDeadlinesAdapter.kt      - Deadlines adapter
└── NewsAdapter.kt                   - News cards adapter

STUDENT DASHBOARD:
┌─────────────────────────────────┐
│ Greeting card with time of day  │
├─────────────────────────────────┤
│ TODAY'S CLASSES (Paginated)     │
│ - Load 5 at time                │
│ - Color-coded by status         │
├─────────────────────────────────┤
│ UPCOMING DEADLINES (Paginated)  │
│ - Load 10 at a time             │
│ - Red=Today, Orange=Soon, etc   │
├─────────────────────────────────┤
│ RECENT NEWS (From NewsAPI)      │
│ - Load 3 cards                  │
│ - Lazy load images              │
└─────────────────────────────────┘

DATA LOADING (Parallel):
viewModelScope.launch {
    combine(
        classRepository.getTodayClasses(),
        assignmentRepository.getUpcomingDeadlines(),
        newsRepository.getLatestNews()
    ) { classes, deadlines, news ->
        Success(HomeData(classes, deadlines, news))
    }.collect { _homeState.value = it }
}

PAGINATION IMPLEMENTATION:
User scrolls deadlines → Within 5 items of bottom
Adapter calls onLoadMore callback
ViewModel calls loadMoreDeadlines()
Repository queries with page parameter
New items added to StateFlow
Adapter.submitList() updates UI (only changed items)

DEADLINE COLOR CODING:
- Today: Red (#FF4D6A)
- Tomorrow: Orange (#FFA500)
- This week: Yellow (#FFB020)
- Later: Blue (#1E6FFF)
- Submitted: Green (#00C896)

Real-time Updates:
- Listener on assignments for current user
- When new assignment → auto-update list
- When student submits → deadline color changes
```

### Quiz Module

```
RESPONSIBILITY: Create quizzes, attempt quizzes, view results

FILES:
├── QuizListFragment.kt              - Available quizzes UI
├── CreateQuizFragment.kt            - Quiz creation UI
├── AddQuestionDialog.kt             - Add question dialog
├── QuizAttemptActivity.kt           - Quiz taking UI
├── QuizProgressVisualization.kt     - Progress bar visualization
├── QuizResultFragment.kt            - Results display
├── QuizViewModel.kt                 - Quiz state
├── EditableQuestionsAdapter.kt      - Question editing adapter
├── QuizAdapter.kt                   - Quiz list adapter
└── QuizAttemptsAdapter.kt           - Results list adapter

QUIZ ATTEMPT FLOW:

1. User taps quiz in QuizListFragment
2. QuizAttemptActivity launched
3. QuizViewModel loads all questions (with timer)
4. ViewPager2 displays questions one at a time
5. offscreenPageLimit = 2:
   - Current question in memory
   - Next question pre-cached (no load on swipe)
   - Previous question discarded to save memory
6. User answers and taps next
7. Answer saved in ViewModel (in-memory)
8. ViewPager swipes to next question (instant, pre-cached)
9. Loop until all questions answered
10. Timer expires → Auto-submit
11. Send all answers to Firebase
12. Server calculates score
13. QuizResultFragment displays results

QUESTION DATA STRUCTURE:
Question
├── id: String
├── text: String
├── type: enum (MCQ, SHORT_ANSWER, TRUE_FALSE)
├── imageUrl: String?
├── options: List<String>
├── correctAnswer: String
└── points: Int

TIMER IMPLEMENTATION:
- Global countdown (not per question)
- Background timer continues even if user minimizes
- Auto-submit if time expires
- Shows remaining time prominently

FIRESTORE STRUCTURE:
quizzes/{quizId}
├── title
├── classId
├── timeLimit (minutes)
├── dueDate
├── questions: array of question objects
└── createdBy

attempts/{attemptId}
├── quizId
├── userId
├── answers: map of questionId → answer
├── score
├── submittedAt (timestamp)
└── timeSpent (seconds)

LAZY LOADING:
- Questions pre-cached by ViewPager
- Images lazy loaded on display
- Results paginated (20 per page)
```

### Splash Module

```
RESPONSIBILITY: App startup, authentication check, animation

FILES:
├── SplashActivity.kt                - Splash entry point
├── SplashCanvasView.kt              - Main canvas view
├── NeuralBackgroundView.kt          - Neural network animation
├── ParticleBurstView.kt             - Particle system
└── SplashAnimationOrchestrator.kt   - Animation coordinator

STARTUP FLOW:
1. App launches → SplashActivity.onCreate()
2. Initialize Firebase
3. Load SharedPreferences (theme, language)
4. Start animations:
   - NeuralBackgroundView draws network
   - ParticleBurstView emits particles
   - Logo fades in
5. Parallel: checkAuthentication()
   - Read auth token from encrypted SharedPreferences
   - Call Firebase.auth().currentUser
   - Verify token is still valid
   - Check user's approval status
6. After animations (2s) OR auth check completes:
   - Valid auth token + approved → MainActivity
   - Valid auth token + not approved → AuthActivity (PendingApprovalFragment)
   - No auth token → AuthActivity (LoginFragment)
   - Auth failed → AuthActivity (error message)

ANIMATION DETAILS:
- NeuralBackgroundView: Canvas-based network animation
  - Draws circles representing neural nodes
  - Draws lines connecting nodes
  - Nodes pulse and glow
  - Connections animate with gradient

- ParticleBurstView: Particle physics system
  - Particles emit from center on burst trigger
  - Each particle has velocity and gravity
  - Particles fade out over time
  - Collision detection with screen bounds

- SplashAnimationOrchestrator: Timing coordinator
  - Sequence animations (fade → slide → appear)
  - Synchronize multiple animations
  - Trigger events on completion

PERFORMANCE:
- Minimal blocking (all async)
- Background threads for heavy work
- Animations on UI thread (necessary)
- Typical splash duration: 2 seconds
```

### Settings Module

```
RESPONSIBILITY: User preferences, app configuration

FILES:
└── SettingsManager.kt               - Settings storage/retrieval

IMPLEMENTATION:
- SharedPreferences for simple key-value storage
- Encrypted SharedPreferences for sensitive data
- EncryptedSharedPreferences from AndroidX Security

SETTINGS STORED:

Theme Settings:
├── isDarkMode: Boolean (default: false)
├── accentColor: String (hex color)
└── fontSizeMultiplier: Float (default: 1.0)

Language Settings:
├── currentLanguage: String (default: "en")
└── autoTranslate: Boolean (default: false)

Notification Settings:
├── notificationsEnabled: Boolean (default: true)
├── soundEnabled: Boolean (default: true)
├── vibrationEnabled: Boolean (default: true)
├── quietHoursStart: String (HH:mm)
├── quietHoursEnd: String (HH:mm)
└── muteNotifications: Boolean

Display Settings:
├── highContrast: Boolean (default: false)
└── reduceMotion: Boolean (default: false)

Cache Settings:
├── lastClearCache: Long (timestamp)
├── cacheSize: Long (bytes)
└── offlineMode: Boolean (default: false)

USAGE:
```kotlin
val settingsManager = SettingsManager(context)

// Read settings
val isDarkMode = settingsManager.isDarkMode
val language = settingsManager.currentLanguage

// Write settings
settingsManager.isDarkMode = true
settingsManager.currentLanguage = "es"

// Listen for changes
settingsManager.observeDarkModeChanges().collect { enabled ->
    // Apply theme change
}
```

---

## 7. Data Layer & Firebase

### Firestore Architecture

```
COLLECTIONS:

users/
├── {userId}
│   ├── email: String
│   ├── name: String
│   ├── role: String ("student", "teacher", "admin")
│   ├── profileImageUrl: String?
│   ├── isApproved: Boolean
│   ├── isEmailVerified: Boolean
│   ├── createdAt: Timestamp
│   └── updatedAt: Timestamp

classes/
├── {classId}
│   ├── name: String
│   ├── code: String (join code)
│   ├── description: String
│   ├── teacherId: String (FK to users)
│   ├── studentIds: Array (FK to users)
│   ├── createdAt: Timestamp
│   ├── updatedAt: Timestamp
│   └── isArchived: Boolean

announcements/
├── {announcementId}
│   ├── classId: String (FK)
│   ├── authorId: String (FK)
│   ├── title: String
│   ├── content: String
│   ├── attachments: Array<Url>
│   ├── createdAt: Timestamp
│   ├── updatedAt: Timestamp
│   └── isPinned: Boolean

assignments/
├── {assignmentId}
│   ├── classId: String (FK)
│   ├── title: String
│   ├── description: String
│   ├── rubric: Object (scoring criteria)
│   ├── dueDate: Timestamp
│   ├── createdAt: Timestamp
│   ├── createdBy: String (FK)
│   ├── attachments: Array<Url>
│   ├── maxScore: Int
│   └── allowLateSubmission: Boolean

submissions/
├── {submissionId}
│   ├── assignmentId: String (FK)
│   ├── studentId: String (FK)
│   ├── fileUrl: String
│   ├── submittedAt: Timestamp
│   ├── grade: Int?
│   ├── feedback: String?
│   ├── gradedAt: Timestamp?
│   ├── gradedBy: String? (FK)
│   └── status: String ("pending", "submitted", "graded")

attendance/
├── {attendanceId}
│   ├── sessionId: String
│   ├── classId: String (FK)
│   ├── studentId: String (FK)
│   ├── markedAt: Timestamp
│   ├── status: String ("present", "absent", "late")
│   └── markedBy: String (system or manual)

quizzes/
├── {quizId}
│   ├── classId: String (FK)
│   ├── title: String
│   ├── description: String
│   ├── timeLimit: Int (minutes)
│   ├── dueDate: Timestamp
│   ├── createdBy: String (FK)
│   ├── questions: Array<Question>
│   ├── createdAt: Timestamp
│   ├── passScore: Int
│   └── showResultsAfterSubmit: Boolean

attempts/
├── {attemptId}
│   ├── quizId: String (FK)
│   ├── userId: String (FK)
│   ├── answers: Map<QuestionId, Answer>
│   ├── score: Int
│   ├── percentage: Float
│   ├── status: String ("in_progress", "submitted")
│   ├── startedAt: Timestamp
│   ├── submittedAt: Timestamp
│   └── timeSpent: Int (seconds)
```

### Security Rules

```firestore-rules
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    
    // Helper functions
    function isAuthenticated() {
      return request.auth != null;
    }
    
    function isAdmin(userId) {
      return get(/databases/$(database)/documents/users/$(userId)).data.role == "admin";
    }
    
    function isTeacher(userId) {
      return get(/databases/$(database)/documents/users/$(userId)).data.role == "teacher";
    }
    
    function isStudent(userId) {
      return get(/databases/$(database)/documents/users/$(userId)).data.role == "student";
    }
    
    // Users can only read/write their own data
    match /users/{userId} {
      allow read: if isAuthenticated() && request.auth.uid == userId;
      allow write: if isAuthenticated() && request.auth.uid == userId;
      allow read: if isAdmin(request.auth.uid); // Admins see all users
      allow write: if isAdmin(request.auth.uid);
    }
    
    // Classes
    match /classes/{classId} {
      allow read: if isAuthenticated(); // Any authenticated user
      allow create: if isAuthenticated() && isTeacher(request.auth.uid);
      allow update: if isAuthenticated() && 
        (resource.data.teacherId == request.auth.uid || isAdmin(request.auth.uid));
      allow delete: if isAdmin(request.auth.uid);
    }
    
    // Announcements in classes
    match /classes/{classId}/announcements/{docId} {
      allow read: if isAuthenticated();
      allow create: if isAuthenticated() && isTeacher(request.auth.uid);
      allow update, delete: if isTeacher(request.auth.uid) || isAdmin(request.auth.uid);
    }
    
    // Assignments
    match /assignments/{assignmentId} {
      allow read: if isAuthenticated();
      allow create: if isTeacher(request.auth.uid);
      allow update: if isTeacher(request.auth.uid) || isAdmin(request.auth.uid);
    }
    
    // Submissions (sensitive - students only see their own)
    match /submissions/{submissionId} {
      allow read: if isAuthenticated() && 
        (resource.data.studentId == request.auth.uid || isTeacher(request.auth.uid));
      allow create: if isStudent(request.auth.uid) && 
        request.auth.uid == request.resource.data.studentId;
      allow update: if isTeacher(request.auth.uid);
    }
    
    // Attendance
    match /attendance/{attendanceId} {
      allow read: if isAuthenticated();
      allow create: if isAuthenticated(); // Students mark their own
      allow update: if isTeacher(request.auth.uid);
    }
  }
}
```

---

## 8. State Management

### StateFlow Pattern

```kotlin
// Complete StateFlow setup with best practices
class UserViewModel(
    private val userRepository: UserRepository
) : ViewModel() {
    
    // Private mutable state
    private val _userState = MutableStateFlow<UiState<User>>(Loading)
    
    // Public immutable state (UI observes this)
    val userState: StateFlow<UiState<User>> = _userState.asStateFlow()
    
    // Optional: Combine multiple states
    private val _homeState = combine(
        _userState,
        _classesState,
        _assignmentsState
    ) { user, classes, assignments ->
        HomeState(user, classes, assignments)
    }.stateIn(viewModelScope, SharingStarted.Lazily, initialValue = null)
    
    val homeState: StateFlow<HomeState?> = _homeState
    
    init {
        loadUser()
    }
    
    private fun loadUser() {
        viewModelScope.launch {
            try {
                _userState.value = Loading
                val user = userRepository.getCurrentUser()
                _userState.value = Success(user)
            } catch (e: Exception) {
                _userState.value = Error(e.message ?: "Unknown error")
            }
        }
    }
    
    fun refreshUser() {
        loadUser()
    }
}

// Fragment collection
class UserProfileFragment : Fragment() {
    private val viewModel: UserViewModel by viewModels()
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        viewLifecycleOwner.lifecycleScope.launch {
            // Only collect when STARTED (not CREATED)
            // Avoids unnecessary recomposition when paused
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.userState.collect { state ->
                    updateUI(state)
                }
            }
        }
    }
    
    private fun updateUI(state: UiState<User>) {
        when (state) {
            is Loading -> showProgressBar()
            is Success -> displayUser(state.data)
            is Error -> showError(state.message)
        }
    }
}
```

### Complex State Management with Combine

```kotlin
class HomeCombinedViewModel(
    private val classRepository: ClassRepository,
    private val assignmentRepository: AssignmentRepository,
    private val newsRepository: NewsRepository
) : ViewModel() {
    
    // Individual states
    private val _classesState = MutableStateFlow<UiState<List<Class>>>(Loading)
    private val _assignmentsState = MutableStateFlow<UiState<List<Assignment>>>(Loading)
    private val _newsState = MutableStateFlow<UiState<List<News>>>(Loading)
    
    // Combined state
    val homeState: StateFlow<HomeUiState> = combine(
        _classesState,
        _assignmentsState,
        _newsState
    ) { classes, assignments, news ->
        HomeUiState(classes, assignments, news)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = HomeUiState(Loading, Loading, Loading)
    )
    
    init {
        loadAll()
    }
    
    private fun loadAll() {
        viewModelScope.launch {
            launch {
                try {
                    _classesState.value = Loading
                    val classes = classRepository.getTodayClasses()
                    _classesState.value = Success(classes)
                } catch (e: Exception) {
                    _classesState.value = Error(e.message)
                }
            }
            
            launch {
                try {
                    _assignmentsState.value = Loading
                    val assignments = assignmentRepository.getUpcomingAssignments()
                    _assignmentsState.value = Success(assignments)
                } catch (e: Exception) {
                    _assignmentsState.value = Error(e.message)
                }
            }
            
            launch {
                try {
                    _newsState.value = Loading
                    val news = newsRepository.getLatestNews()
                    _newsState.value = Success(news)
                } catch (e: Exception) {
                    _newsState.value = Error(e.message)
                }
            }
        }
    }
}

// UI State
data class HomeUiState(
    val classesState: UiState<List<Class>>,
    val assignmentsState: UiState<List<Assignment>>,
    val newsState: UiState<List<News>>
) {
    fun isLoading() = classesState is Loading || assignmentsState is Loading || newsState is Loading
}
```

---

## 9. Navigation & Routing

### Navigation Graph Structure

```xml
<!-- nav_main.xml - Main app navigation -->
<?xml version="1.0" encoding="utf-8"?>
<navigation xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:id="@+id/nav_main"
    app:startDestination="@id/nav_home">
    
    <!-- Home destination -->
    <fragment
        android:id="@+id/nav_home"
        android:name="com.syed.classconnect.ui.home.StudentHomeFragment"
        android:label="@string/home">
        <action
            android:id="@+id/action_home_to_classDetail"
            app:destination="@id/classDetailActivity"
            app:enterAnim="@anim/slide_in_right"
            app:exitAnim="@anim/slide_out_left" />
        <action
            android:id="@+id/action_home_to_assignment"
            app:destination="@id/nav_assignments" />
    </fragment>
    
    <!-- Classes destination -->
    <fragment
        android:id="@+id/nav_classes"
        android:name="com.syed.classconnect.ui.classes.ClassListFragment"
        android:label="@string/classes">
        <action
            android:id="@+id/action_classes_to_detail"
            app:destination="@id/classDetailActivity" />
        <action
            android:id="@+id/action_classes_to_create"
            app:destination="@id/createClassDialog" />
    </fragment>
    
    <!-- Class detail activity -->
    <activity
        android:id="@+id/classDetailActivity"
        android:name="com.syed.classconnect.ui.classes.ClassDetailActivity"
        android:label="@string/class_detail">
        <argument
            android:name="classId"
            app:argType="string" />
    </activity>
    
    <!-- ... other destinations ... -->
    
</navigation>

<!-- nav_auth.xml - Auth flow navigation -->
<?xml version="1.0" encoding="utf-8"?>
<navigation xmlns:android="http://schemas.android.com/apk/res/android"
    android:id="@+id/nav_auth"
    app:startDestination="@id/loginFragment">
    
    <fragment
        android:id="@+id/loginFragment"
        android:name="com.syed.classconnect.ui.auth.LoginFragment"
        android:label="@string/sign_in">
        <action
            android:id="@+id/action_login_to_register"
            app:destination="@id/registerFragment" />
        <action
            android:id="@+id/action_login_to_forgotPassword"
            app:destination="@id/forgotPasswordFragment" />
        <action
            android:id="@+id/action_login_to_verify"
            app:destination="@id/emailVerificationWaitFragment" />
    </fragment>
    
    <!-- ... other auth fragments ... -->
    
</navigation>
```

### Programmatic Navigation

```kotlin
// Fragment to Fragment with arguments
val args = ClassDetailFragmentArgs(classId = "class_123")
findNavController().navigate(
    ClassListFragmentDirections.actionClassesToDetail(classId)
)

// With custom transitions
findNavController().navigate(
    R.id.action_home_to_classDetail,
    Bundle().apply { putString("classId", "class_123") },
    NavOptions.Builder()
        .setEnterAnim(R.anim.slide_in_right)
        .setExitAnim(R.anim.slide_out_left)
        .setPopEnterAnim(R.anim.slide_in_left)
        .setPopExitAnim(R.anim.slide_out_right)
        .build()
)

// Back navigation
findNavController().popBackStack()

// Navigation with deep link
val deepLink = Uri.parse("classconnect://class/$classId")
findNavController().navigate(deepLink)
```

---

## 10. Performance Optimization

### Pagination Implementation

```kotlin
// ViewModel with pagination support
class AssignmentsViewModel(
    private val assignmentRepository: AssignmentRepository
) : ViewModel() {
    
    private val _assignments = MutableStateFlow<List<Assignment>>(emptyList())
    val assignments: StateFlow<List<Assignment>> = _assignments.asStateFlow()
    
    private val _isLoadingMore = MutableStateFlow(false)
    val isLoadingMore: StateFlow<Boolean> = _isLoadingMore.asStateFlow()
    
    private var currentPage = 0
    private val pageSize = 20
    private var hasMorePages = true
    
    init {
        loadNextPage()
    }
    
    fun loadNextPage() {
        if (_isLoadingMore.value || !hasMorePages) return
        
        viewModelScope.launch {
            _isLoadingMore.value = true
            try {
                val newAssignments = assignmentRepository.getAssignments(
                    page = currentPage,
                    pageSize = pageSize
                )
                
                if (newAssignments.size < pageSize) {
                    hasMorePages = false
                }
                
                _assignments.value = _assignments.value + newAssignments
                currentPage++
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoadingMore.value = false
            }
        }
    }
}

// Adapter with pagination trigger
class AssignmentsAdapter : ListAdapter<Assignment, AssignmentViewHolder>(AssignmentDiffCallback()) {
    
    private var onLoadMore: (() -> Unit)? = null
    
    override fun onBindViewHolder(holder: AssignmentViewHolder, position: Int) {
        holder.bind(getItem(position))
        
        // Trigger load more when user within 5 items of end
        if (position >= itemCount - 5) {
            onLoadMore?.invoke()
        }
    }
    
    fun setOnLoadMoreListener(listener: () -> Unit) {
        onLoadMore = listener
    }
}

// Fragment setup
class AssignmentsFragment : Fragment() {
    private val viewModel: AssignmentsViewModel by viewModels()
    private val adapter = AssignmentsAdapter()
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        adapter.setOnLoadMoreListener {
            viewModel.loadNextPage()
        }
        
        recyclerView.adapter = adapter
        
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.assignments.collect { assignments ->
                    adapter.submitList(assignments)
                }
            }
        }
    }
}
```

### Image Lazy Loading

```kotlin
// Efficient image loading with Glide
Glide.with(context)
    .load(imageUrl)
    .placeholder(R.drawable.ic_placeholder)  // Show while loading
    .thumbnail(0.1f)                          // Show low-res first
    .error(R.drawable.ic_error)              // Show on failure
    .circleCrop()                            // Transform
    .into(imageView)

// Preload images for nearby items
Glide.with(context)
    .load(nextImageUrl)
    .preload()

// Memory optimization
GlideApp.get(context).clearMemory()          // Clear memory cache
GlideApp.get(context).clearDiskCache()       // Clear disk cache (async)
```

### Memory Management

```kotlin
// ViewPager2 configuration for tabs
viewPager.offscreenPageLimit = 1  // Keep only adjacent tabs

// Proper bitmap management
private fun loadBitmapEfficiently(path: String, width: Int, height: Int): Bitmap {
    val options = BitmapFactory.Options().apply {
        inJustDecodeBounds = true
    }
    BitmapFactory.decodeFile(path, options)
    
    options.inSampleSize = calculateInSampleSize(options, width, height)
    options.inJustDecodeBounds = false
    
    return BitmapFactory.decodeFile(path, options)
}

private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
    val height = options.outHeight
    val width = options.outWidth
    var inSampleSize = 1
    
    if (height > reqHeight || width > reqWidth) {
        val halfHeight = height / 2
        val halfWidth = width / 2
        
        while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
            inSampleSize *= 2
        }
    }
    
    return inSampleSize
}

// Lifecycle-aware collection (prevents memory leaks)
viewLifecycleOwner.lifecycleScope.launch {
    repeatOnLifecycle(Lifecycle.State.STARTED) {
        viewModel.data.collect { data ->
            updateUI(data)
        }
    }
}
```

---

## 11. Security Implementation

### Authentication & Token Management

```kotlin
// Secure token storage
class SecureAuthTokenManager(context: Context) {
    private val encryptedSharedPreferences = EncryptedSharedPreferences.create(
        context,
        "auth_prefs",
        MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build(),
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )
    
    fun saveAuthToken(token: String) {
        encryptedSharedPreferences.edit {
            putString("auth_token", token)
            putLong("token_timestamp", System.currentTimeMillis())
        }
    }
    
    fun getAuthToken(): String? {
        return encryptedSharedPreferences.getString("auth_token", null)
    }
    
    fun clearAuthToken() {
        encryptedSharedPreferences.edit {
            remove("auth_token")
            remove("token_timestamp")
        }
    }
}

// Firebase authentication
class AuthRepository(
    private val firebaseAuth: FirebaseAuth,
    private val tokenManager: SecureAuthTokenManager
) {
    
    suspend fun login(email: String, password: String): Result<User> = try {
        val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
        val user = authResult.user ?: throw Exception("User is null")
        
        val token = user.getIdToken(false).await().token ?: throw Exception("Token is null")
        tokenManager.saveAuthToken(token)
        
        Result.success(mapToUser(user))
    } catch (e: Exception) {
        Result.failure(e)
    }
    
    suspend fun logout() {
        firebaseAuth.signOut()
        tokenManager.clearAuthToken()
    }
}
```

### Input Validation

```kotlin
object ValidationUtils {
    
    fun validateEmail(email: String): String? = when {
        email.isEmpty() -> "Email is required"
        !email.contains("@") -> "Invalid email format"
        !email.contains(".") -> "Invalid email format"
        email.length > 254 -> "Email is too long"
        else -> null
    }
    
    fun validatePassword(password: String): String? = when {
        password.isEmpty() -> "Password is required"
        password.length < 8 -> "Password must be at least 8 characters"
        !password.any { it.isUpperCase() } -> "Password must contain uppercase letter"
        !password.any { it.isLowerCase() } -> "Password must contain lowercase letter"
        !password.any { it.isDigit() } -> "Password must contain digit"
        !password.any { "!@#$%^&*()_+-=[]{}|;:',.<>?".contains(it) } -> "Password must contain special character"
        else -> null
    }
    
    fun validateUsername(username: String): String? = when {
        username.isEmpty() -> "Username is required"
        username.length < 3 -> "Username must be at least 3 characters"
        username.length > 20 -> "Username is too long"
        !username.matches(Regex("^[a-zA-Z0-9_]*$")) -> "Username can only contain letters, numbers, and underscores"
        else -> null
    }
}
```

### Data Encryption

```kotlin
// Biometric authentication
class BiometricAuthManager(context: Context) {
    private val biometricPrompt = BiometricPrompt(
        context as AppCompatActivity,
        Executors.newSingleThreadExecutor(),
        object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                // Handle successful authentication
            }
            
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                // Handle error
            }
        }
    )
    
    fun authenticate() {
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric Authentication")
            .setDescription("Authenticate to access ClassConnect")
            .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG)
            .setNegativeButtonText("Cancel")
            .build()
        
        biometricPrompt.authenticate(promptInfo)
    }
}
```

---

## 12. Testing Strategy

### Unit Testing

```kotlin
@RunWith(AndroidJUnit4::class)
class LoginViewModelTest {
    
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()
    
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    
    private val mockAuthRepository = mockk<AuthRepository>(relaxed = true)
    private lateinit var viewModel: LoginViewModel
    
    @Before
    fun setUp() {
        viewModel = LoginViewModel(mockAuthRepository)
    }
    
    @Test
    fun login_withValidCredentials_emitsSuccess() = runTest {
        // Arrange
        val user = User("1", "test@test.com")
        coEvery { mockAuthRepository.login("test@test.com", "password") } returns Result.success(user)
        
        // Act
        viewModel.login("test@test.com", "password")
        advanceUntilIdle()
        
        // Assert
        assert(viewModel.authState.value is Success)
        assertEquals(user, (viewModel.authState.value as Success).data)
    }
    
    @Test
    fun login_withInvalidEmail_emitsError() = runTest {
        // Act
        viewModel.login("invalid-email", "password")
        
        // Assert
        val state = viewModel.authState.value
        assert(state is Error)
        assertTrue((state as Error).message.contains("Invalid email"))
    }
}
```

### Integration Testing

```kotlin
@RunWith(AndroidJUnit4::class)
class ClassesFragmentIntegrationTest {
    
    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)
    
    @Test
    fun classesFragment_displaysClassList() {
        // Setup test data
        val testClasses = listOf(
            ClassModel("1", "Math 101", "teacher1", 30),
            ClassModel("2", "Physics 102", "teacher2", 25)
        )
        
        // Launch fragment
        activityRule.scenario.onActivity { activity ->
            val fragment = ClassListFragment()
            activity.supportFragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commitNow()
        }
        
        // Verify UI
        onView(withText("Math 101"))
            .check(matches(isDisplayed()))
        onView(withText("30 students"))
            .check(matches(isDisplayed()))
    }
}
```

---

## 13. Dependency Injection

### Hilt Configuration

```kotlin
// Application module
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    @Singleton
    @Provides
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()
    
    @Singleton
    @Provides
    fun provideFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()
    
    @Singleton
    @Provides
    fun provideSecurePreferences(context: Context): SecurePreferences {
        return SecurePreferences(context)
    }
}

// Repository module
@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    
    @Singleton
    @Provides
    fun provideAuthRepository(
        firebaseAuth: FirebaseAuth,
        securePreferences: SecurePreferences
    ): AuthRepository = AuthRepositoryImpl(firebaseAuth, securePreferences)
    
    @Singleton
    @Provides
    fun provideClassRepository(
        firestore: FirebaseFirestore,
        cacheManager: CacheManager
    ): ClassRepository = ClassRepositoryImpl(firestore, cacheManager)
}

// ViewModel injection
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val classRepository: ClassRepository,
    private val assignmentRepository: AssignmentRepository
) : ViewModel() {
    // Implementation
}

// Fragment injection
@AndroidEntryPoint
class StudentHomeFragment : Fragment() {
    private val viewModel: HomeViewModel by viewModels()
}
```

---

## 14. Error Handling

### Global Exception Handling

```kotlin
// Exception hierarchy
sealed class ClassConnectException(message: String) : Exception(message) {
    class NetworkException(message: String) : ClassConnectException(message)
    class AuthenticationException(message: String) : ClassConnectException(message)
    class AuthorizationException(message: String) : ClassConnectException(message)
    class ValidationException(message: String) : ClassConnectException(message)
    class ServerException(val code: Int, message: String) : ClassConnectException(message)
    class DataException(message: String) : ClassConnectException(message)
}

// Error handler utility
class ErrorHandler {
    fun getErrorMessage(exception: Exception): String = when (exception) {
        is ClassConnectException.NetworkException -> "No internet connection"
        is ClassConnectException.AuthenticationException -> "Authentication failed"
        is ClassConnectException.AuthorizationException -> "You don't have permission"
        is ClassConnectException.ValidationException -> exception.message ?: "Invalid input"
        is ClassConnectException.ServerException -> "Server error (${exception.code})"
        is ClassConnectException.DataException -> "Data error"
        else -> "Unknown error occurred"
    }
}

// Coroutine exception handler
class CoroutineExceptionHandler {
    fun getExceptionHandler(onError: (String) -> Unit) = CoroutineExceptionHandler { _, exception ->
        val message = ErrorHandler().getErrorMessage(exception as Exception)
        onError(message)
    }
}
```

---

## 15. API Integration

### REST API with Retrofit

```kotlin
interface NewsApiService {
    @GET("top-headlines")
    suspend fun getTopHeadlines(
        @Query("category") category: String,
        @Query("language") language: String = "en",
        @Query("apiKey") apiKey: String
    ): NewsResponse
}

// Retrofit client setup
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    
    @Singleton
    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
            })
            .addInterceptor { chain ->
                val originalRequest = chain.request()
                val newRequest = originalRequest.newBuilder()
                    .header("Content-Type", "application/json")
                    .build()
                chain.proceed(newRequest)
            }
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }
    
    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://newsapi.org/v2/")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
    }
    
    @Singleton
    @Provides
    fun provideNewsApiService(retrofit: Retrofit): NewsApiService {
        return retrofit.create(NewsApiService::class.java)
    }
}

// Repository using API
class NewsRepository(private val apiService: NewsApiService) {
    suspend fun getLatestNews(): List<News> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getTopHeadlines(
                category = "education",
                apiKey = BuildConfig.NEWS_API_KEY
            )
            response.articles.map { it.toNews() }
        } catch (e: Exception) {
            throw ClassConnectException.NetworkException(e.message ?: "Network error")
        }
    }
}
```

---

## Summary

ClassConnect is built on a **modern, production-ready Android architecture** featuring:

✅ **MVVM + Clean Architecture** - Clear separation of concerns  
✅ **StateFlow Reactive Programming** - Efficient state management  
✅ **Firebase Integration** - Real-time database and authentication  
✅ **Coroutines** - Efficient async operations  
✅ **Dependency Injection (Hilt)** - Loose coupling  
✅ **Pagination & Lazy Loading** - Performance optimized  
✅ **Security** - Encrypted storage, validation, auth tokens  
✅ **Testing** - Unit and integration tests  
✅ **Error Handling** - Comprehensive exception management  
✅ **Navigation Component** - Type-safe navigation  

**All components work together seamlessly** to create a robust, maintainable, and performant classroom management application.

---

*Complete Technical Documentation - April 10, 2026*

