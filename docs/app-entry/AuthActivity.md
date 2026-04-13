# AuthActivity.kt — Authentication nav host that routes unauthenticated users through login, register, pending, and email verification screens

---

## 📁 File Location
`app/src/main/java/com/syed/classconnect/ui/auth/AuthActivity.kt`

---

## 🎯 What This File Does
`AuthActivity` is a thin container Activity whose only job is to host the authentication navigation graph (`nav_auth`). It holds the `auth_nav_host` NavHostFragment, which contains all auth-related screens: Login, Register, ForgotPassword, PendingApproval, and EmailVerificationWait. When `SplashActivity` determines that a user is not logged in, or when login routing produces a `ToPending` or `ToEmailVerification` result, the app starts `AuthActivity` with an optional `"destination"` string extra to navigate the user directly to the correct screen. Without this Activity, the authentication flow would have no container and users could never log in.

---

## 📦 Every Import — Explained

| Import | From Library | What It Is | Why Used Here |
|--------|-------------|-----------|--------------|
| `android.os.Bundle` | Android SDK | Key-value map for Activity creation data | Receives `savedInstanceState` and intent extras |
| `androidx.appcompat.app.AppCompatActivity` | AndroidX AppCompat | Base Activity with Material design and backcompat support | `AuthActivity` extends it |
| `androidx.navigation.fragment.NavHostFragment` | Navigation Component | Fragment that hosts and manages a navigation graph | Finding the auth nav host |
| `com.syed.classconnect.R` | Project | Resource IDs | `R.id.auth_nav_host`, `R.navigation.nav_auth` |
| `com.syed.classconnect.databinding.ActivityAuthBinding` | ViewBinding | Type-safe reference to `activity_auth.xml` views | `binding.root`, `auth_nav_host` |
| `dagger.hilt.android.AndroidEntryPoint` | Hilt | Enables DI in this Activity | Required for the hosted Fragments to use Hilt |

---

## 🔑 Every Keyword, Annotation & Concept Used

### `@AndroidEntryPoint`
Even though `AuthActivity` itself doesn't `@Inject` any fields, the annotation is REQUIRED because the Fragments it hosts (`LoginFragment`, `RegisterFragment`, etc.) use `@AndroidEntryPoint`. The parent Activity must also have the annotation for Hilt to inject into child fragments.

```kotlin
@AndroidEntryPoint
class AuthActivity : AppCompatActivity()
// Without this, LoginFragment's @Inject fields would not be set.
```

### `NavHostFragment`
A special Fragment that acts as a navigation controller container. `auth_nav_host` in `activity_auth.xml` inflates `nav_auth.xml` — the navigation graph containing all auth screens.

### `intent.getStringExtra("destination")`
Reads a string value from the Intent that started this Activity. Used to navigate directly to a specific screen:
- `"pending"` → navigate to `PendingApprovalFragment`
- `"email_verification"` → navigate to `EmailVerificationWaitFragment`
- (no extra / null) → default start destination (LoginFragment)

### `intent.getBooleanExtra("pending", false)`
A second (legacy) way to signal that the user should be sent to PendingApproval. The `"destination"` approach is preferred but both are handled.

```kotlin
when (intent.getStringExtra("destination")) {
    "pending"            -> navController.navigate(R.id.pendingApprovalFragment)
    "email_verification" -> navController.navigate(R.id.emailVerificationWaitFragment)
}
```

---

## 🏗️ Class Structure
`AuthActivity : AppCompatActivity()` — extends AppCompatActivity. Very minimal: no ViewModel, no DI fields. Pure navigation container.

---

## 📋 Properties

| Property | Type | Modifier | What It Holds | Why It Exists |
|----------|------|----------|--------------|--------------|
| `binding` | `ActivityAuthBinding` | `private lateinit var` | ViewBinding for `activity_auth.xml` | Access the auth_nav_host container |

---

## ⚙️ Functions

### `onCreate(savedInstanceState: Bundle?)`
**Purpose:** Inflate the layout, set up the nav controller, and route to the correct auth screen based on intent extras.
**Called when:** `SplashActivity` starts this Activity (not logged in), or `AuthViewModel` triggers routing after login.
**Step by step:**
1. Inflates `activity_auth.xml` via ViewBinding, sets as content view.
2. Finds the `auth_nav_host` NavHostFragment.
3. Gets the NavController from that fragment.
4. Reads `intent.getStringExtra("destination")`:
   - `"pending"` → `navController.navigate(R.id.pendingApprovalFragment)`
   - `"email_verification"` → `navController.navigate(R.id.emailVerificationWaitFragment)`
   - null/other → no action; NavController uses the default start destination (LoginFragment) from `nav_auth.xml`.
5. Reads `intent.getBooleanExtra("pending", false)` as a secondary check and navigates to PendingApproval if true.

---

## 🔄 Data Flow Diagram
```
SplashActivity: user not logged in
        ↓
startActivity(Intent(this, AuthActivity::class.java))
        ↓
AuthActivity.onCreate()
        ↓
No "destination" extra → default start = LoginFragment
        ↓
User logs in → AuthViewModel.resolvePostLoginRoute()
        ↓
        ├── ToPending → startActivity(AuthActivity, destination="pending")
        ├── ToEmailVerification → startActivity(AuthActivity, destination="email_verification")
        └── ToMain → startActivity(MainActivity)
```

---

## 🧩 Dependencies

| Depends On | Why |
|-----------|-----|
| `nav_auth` navigation graph | Defines all auth fragments and their navigation actions |
| `SplashActivity` | Starts AuthActivity when user is not logged in |
| `AuthViewModel` | Signals routing result after login/register |

---

## ⚠️ Important Notes & Gotchas
- `@AndroidEntryPoint` on AuthActivity is mandatory even though the Activity itself has no `@Inject` fields — it enables Hilt injection in all child Fragments.
- Navigation to `pendingApprovalFragment` or `emailVerificationWaitFragment` is done AFTER `onCreate` returns to the nav graph's start destination (LoginFragment). This means there is a brief moment where LoginFragment is the root, and the navigated-to fragment is pushed on top. This is correct NavComponent behavior.
- If `"pending"` extra is passed via BOTH `getStringExtra("destination")` AND `getBooleanExtra("pending", true)`, `navController.navigate(R.id.pendingApprovalFragment)` will be called twice — which would push PendingApproval onto the back stack twice. This is a minor bug: back-pressing would show PendingApproval again before Login.

---

## 📝 Full Source Code with Line-by-Line Comments

```kotlin
// ═══════════════════════════════════════
// AuthActivity.kt
// ═══════════════════════════════════════

package com.syed.classconnect.ui.auth

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.syed.classconnect.R
import com.syed.classconnect.databinding.ActivityAuthBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
// Required so Hilt can inject into child Fragments (LoginFragment, etc.).
class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding
    // ViewBinding for activity_auth.xml.

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        // Inflate activity_auth.xml → creates a typed binding with the auth_nav_host view.
        setContentView(binding.root)

        val navHost = supportFragmentManager.findFragmentById(R.id.auth_nav_host) as NavHostFragment
        // Find the NavHostFragment declared in activity_auth.xml.
        val navController = navHost.navController
        // The NavController manages which auth Fragment is shown.

        when (intent.getStringExtra("destination")) {
            "pending"            -> navController.navigate(R.id.pendingApprovalFragment)
            // Navigate to the "your account is awaiting approval" screen.
            "email_verification" -> navController.navigate(R.id.emailVerificationWaitFragment)
            // Navigate to the "check your email" screen.
        }

        if (intent.getBooleanExtra("pending", false)) {
            navController.navigate(R.id.pendingApprovalFragment)
            // Legacy path: boolean extra also routes to pending screen.
        }
    }
}
```

