# RegisterFragment — Dual-path registration with password strength validation

---

## 📁 Location
`app/src/main/java/com/syed/classconnect/ui/auth/RegisterFragment.kt`

---

## 🎯 What This File Does
RegisterFragment handles new user registration with a dual-path system. If a user provides an institution code, they follow **Path A** (institution approval flow → PendingApprovalFragment). If no code is provided, they follow **Path B** (independent email verification flow → EmailVerificationWaitFragment). Features real-time password strength validation with visual indicators.

---

## 📦 Imports — Every Line Explained

| Import | Library / Package | What It Is | Why This File Needs It |
|--------|------------------|-----------|----------------------|
| `android.os.Bundle` | Android SDK | Key-value data | Saved state |
| `android.text.Editable` / `TextWatcher` | Android SDK | Text change listener | Password strength real-time updates |
| `android.view.LayoutInflater` / `View` / `ViewGroup` | Android SDK | View inflation | Fragment view creation |
| `android.widget.TextView` | Android SDK | Text display widget | Password requirement indicators |
| `androidx.core.content.ContextCompat` | AndroidX | Resource access | Gets colors for strength indicators |
| `androidx.core.view.isVisible` | AndroidX Core KTX | View visibility extension | Toggle password requirements visibility |
| `androidx.fragment.app.Fragment` | AndroidX | Base Fragment | This class extends Fragment |
| `androidx.fragment.app.viewModels` | AndroidX Fragment KTX | ViewModel delegation | `by viewModels()` |
| `androidx.lifecycle.lifecycleScope` | AndroidX Lifecycle | Coroutine scope | Collecting StateFlow |
| `androidx.navigation.fragment.findNavController` | AndroidX Navigation | Navigation | Navigate to Pending/EmailVerification |
| `com.syed.classconnect.R` | App (Generated) | Resources | String, color, drawable references |
| `com.syed.classconnect.databinding.FragmentRegisterBinding` | ViewBinding | Type-safe views | Layout binding |
| `com.syed.classconnect.util.ValidationUtils` | App | Validation | Email, password, code validation |
| `com.syed.classconnect.util.hide` / `show` / `showSnackbar` | App | Extensions | UI helpers |
| `dagger.hilt.android.AndroidEntryPoint` | Hilt | DI | Enables injection |
| `kotlinx.coroutines.flow.collectLatest` | Coroutines | Flow collection | Observes registration result StateFlow |
| `kotlinx.coroutines.launch` | Coroutines | Starts coroutine | For lifecycleScope.launch |

---

## 🏗️ Class Structure
`RegisterFragment` extends `Fragment` with `@AndroidEntryPoint`. Uses `AuthViewModel` (shared with LoginFragment) for registration logic.

---

## ⚙️ Key Functions

### `onViewCreated(view, savedInstanceState)`
1. Sets up password strength TextWatcher with real-time validation
2. Sets up institution code TextWatcher to update path info card
3. Sets click listeners for Register button and Login link
4. Observes `viewModel.registrationResult` StateFlow for dual-path routing

### `attemptRegister()`
Validates all fields: name, email, password strength, password match, optional institution code format. Calls `viewModel.register(name, email, password, role, institutionCode)`.

### `updatePathCard(code: String)`
Updates the registration path info card: blue with institution icon when code is entered, green with email icon when empty.

### `updateReq(textView, met, text)`
Updates individual password requirement indicators with ✓ (green) or ✗ (red).

---

## 🔄 Data Flow
```
User fills form → attemptRegister() validates
    → viewModel.register(name, email, password, role, code)
    ├── code not empty → registerWithInstitutionCode() → RegistrationResult.InstitutionPath
    │   → navigate to PendingApprovalFragment
    └── code empty → registerIndependent() → RegistrationResult.IndependentPath
        → navigate to EmailVerificationWaitFragment
```

---

## ⚠️ Important Notes
- Institution code is OPTIONAL — validates format only if non-empty
- Password requirements: 8+ chars, 1 uppercase, 1 digit, 1 special char
- Role selection: radio buttons for "teacher" or "student" (admin created manually)
- Registration result uses `StateFlow` (not LiveData) because it's a one-shot event from dual-path logic

