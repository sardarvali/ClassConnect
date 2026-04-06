# UserDetailFragment.kt — Admin screen for viewing user details and changing their role with warnings and audit logging

---

## 📁 File Location
`app/src/main/java/com/syed/classconnect/ui/admin/UserDetailFragment.kt`

---

## 🎯 What This File Does
`UserDetailFragment` is the admin-only screen that displays a specific user's full profile (avatar, name, email, role, account type badge) and allows the admin to change their role. Role selection uses a `ChipGroup` — when the admin selects a different chip, the "Apply" button becomes enabled, a contextual warning appears (e.g., "Demoting to Student will remove them from all classes"), and a reason text field appears. Confirming opens a final `MaterialAlertDialog` with consequences explained. The role change is logged as a `RoleChangeLog` entry for audit purposes. Without this fragment, admins cannot manage individual users.

---

## 📦 Every Import — Explained

| Import | From Library | What It Is | Why Used Here |
|--------|-------------|-----------|--------------|
| `android.os.Bundle` | Android SDK | Arguments | Fragment arguments for `userId` |
| `android.view.LayoutInflater/View/ViewGroup` | Android SDK | View inflation | `onCreateView` |
| `androidx.core.view.isVisible` | AndroidX | Kotlin property for visibility | Set visibility from Boolean |
| `androidx.fragment.app.Fragment` | AndroidX | Base Fragment | This extends it |
| `androidx.fragment.app.viewModels` | AndroidX | ViewModel delegate | `by viewModels()` |
| `androidx.lifecycle.lifecycleScope` | AndroidX | Coroutine scope | `collectLatest` on StateFlow |
| `com.bumptech.glide.Glide` | Glide | Image loading | Load avatar with circleCrop |
| `com.google.android.material.dialog.MaterialAlertDialogBuilder` | Material | Styled dialog | Role change confirmation |
| `com.google.firebase.auth.FirebaseAuth` | Firebase | Current user | Admin's UID |
| `com.syed.classconnect.R` | Project | Resource IDs | Strings for dialog messages |
| `com.syed.classconnect.data.model.User` | Project | User data class | Target user |
| `com.syed.classconnect.databinding.FragmentUserDetailBinding` | ViewBinding | `fragment_user_detail.xml` | All views |
| `com.syed.classconnect.util.NetworkResult` | Project | State wrapper | Role change result |
| `com.syed.classconnect.util.hide/show/showSnackbar` | Project extensions | UI helpers | Loading and feedback |
| `dagger.hilt.android.AndroidEntryPoint` | Hilt | DI enabled | `@Inject FirebaseAuth` |
| `kotlinx.coroutines.flow.collectLatest` | Coroutines | Flow collection | Collect `roleChangeState` |

---

## 🔑 Every Keyword, Annotation & Concept Used

### `chipGroup.setOnCheckedStateChangeListener { _, checkedIds -> }`
Fires when any chip selection changes. `checkedIds` is the list of currently-selected chip IDs. Maps the chip ID back to a role string. Enables the Apply button only if the selected role differs from the current role.

### `buildWarning(from, to, name): String`
Returns a user-readable consequence string based on the transition:
- Teacher → Student: "Demoting will unassign from all classes"
- Student → Teacher: "Will be removed from all enrolled classes"
- Any → Admin: "Gives full institution control"
Returns empty string for unremarkable transitions (e.g., Student → Student).

### `buildConfirmMessage(from, to, name): String`
More detailed version for the confirmation dialog. Explains irreversible consequences.

### `chipAdmin.isVisible = user.accountType == "institution" && user.uid != adminUid`
Admin chip is only shown for institution members (independent users can't be admins). It's also hidden if the admin is looking at themselves (can't self-demote).

### `viewModel.roleChangeState.collectLatest { result -> }`
Uses `collectLatest` inside `lifecycleScope.launch`. `collectLatest` cancels the previous collection if a new value arrives before the previous completes — appropriate for one-shot results like role change outcomes.

---

## 📋 Properties

| Property | Type | Modifier | What It Holds | Why It Exists |
|----------|------|----------|--------------|--------------|
| `_binding` | `FragmentUserDetailBinding?` | `private var` | ViewBinding | Null on destroy |
| `viewModel` | `UserDetailViewModel` | `private val` | Role change logic | `changeUserRole()`, `loadUser()` |
| `auth` | `FirebaseAuth` | `@Inject lateinit var` | Admin's UID | Self-change guard |
| `targetUser` | `User?` | `private var` | The user being viewed | Used in role change calls |
| `selectedNewRole` | `String?` | `private var` | Chip-selected role | Compared against current |

---

## ⚙️ Functions

### `onViewCreated(view, savedInstanceState)`
1. Reads `userId` from arguments, gets `adminUid` from `auth`.
2. Calls `viewModel.loadUser(targetUid)`.
3. Observes `viewModel.user` (StateFlow) → `bindUserData()`.
4. Observes `viewModel.roleChangeState` → shows success/error Snackbar.
5. Calls `setupRoleChips()`.

### `bindUserData(user, adminUid)` *(private)*
Populates all views with user data. Pre-selects correct chip. Hides admin chip if independent/self.

### `setupRoleChips()` *(private)*
Attaches chip change listener. On change: determines new role, shows/hides warning and reason field, enables/disables Apply button.

### `showConfirmDialog(user, newRole)` *(private)*
Shows `MaterialAlertDialog` with full consequence message. On confirm: calls `viewModel.changeUserRole(...)`.

### `buildWarning` / `buildConfirmMessage` *(private)*
Generate context-appropriate warning strings for different role transitions.

---

## 🔄 Data Flow Diagram
```
Admin navigates to UserDetailFragment(userId=...)
        ↓
viewModel.loadUser(targetUid) → Firestore get user
        ↓
_user = Success(user) → bindUserData() — UI shows user info

Admin taps a different chip (e.g., Student → Teacher)
        ↓
setupRoleChips() detects change → warning shown, Apply button enabled
        ↓
Admin types reason → taps Apply → showConfirmDialog()
        ↓
Admin confirms → viewModel.changeUserRole(uid, "teacher", reason, ...)
        ↓
UserDetailViewModel → AuthRepository.changeRole() + logRoleChange()
        ↓
roleChangeState = Success → Snackbar "Role changed to Teacher"
```
