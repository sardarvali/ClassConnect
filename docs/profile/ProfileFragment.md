# ProfileFragment.kt — User profile view with photo upload, name/bio editing, account badge, and logout

---

## 📁 File Location
`app/src/main/java/com/syed/classconnect/ui/profile/ProfileFragment.kt`

---

## 🎯 What This File Does
`ProfileFragment` is the personal profile screen accessible from the bottom navigation bar. It displays the user's avatar, name, email, role, bio, and account type badge (institution vs independent). Users can change their profile photo (launches system image picker), edit their name and bio, and save changes. The logout button shows a confirmation dialog then signs out and redirects to `AuthActivity`. A "Settings" button navigates to `SettingsFragment`. Without this fragment, users cannot view or update their own profile information.

---

## 📦 Every Import — Explained

| Import | From Library | What It Is | Why Used Here |
|--------|-------------|-----------|--------------|
| `android.content.Intent` | Android SDK | App navigation | Launch `AuthActivity` on logout |
| `android.os.Bundle` | Android SDK | State map | Lifecycle |
| `android.view.LayoutInflater/View/ViewGroup` | Android SDK | View inflation | `onCreateView` |
| `androidx.activity.result.contract.ActivityResultContracts.GetContent` | AndroidX | File picker contract | Photo selection |
| `androidx.appcompat.app.AlertDialog` | AndroidX | Confirmation dialog | Logout confirm |
| `androidx.fragment.app.Fragment` | AndroidX | Base Fragment | This extends it |
| `androidx.fragment.app.viewModels` | AndroidX | ViewModel delegate | `by viewModels()` |
| `androidx.navigation.fragment.findNavController` | AndroidX Navigation | Navigate to settings | `action_profile_to_settings` |
| `com.syed.classconnect.R` | Project | Resource IDs | String resources for dialog |
| `com.google.firebase.auth.FirebaseAuth` | Firebase | Auth service | `auth.signOut()`, current UID |
| `com.syed.classconnect.databinding.FragmentProfileBinding` | ViewBinding | `fragment_profile.xml` | All views |
| `com.syed.classconnect.ui.auth.AuthActivity` | Project | Auth screens | Redirect after logout |
| `com.syed.classconnect.util.loadAvatar` | Project extension | Glide avatar | `ivAvatar.loadAvatar(url)` |
| `com.syed.classconnect.util.animateEntrance/animateScaleIn` | Project extension | Enter animations | Smooth UI entrance |
| `com.syed.classconnect.util.showSnackbar` | Project extension | Feedback message | "Profile saved!" |
| `dagger.hilt.android.AndroidEntryPoint` | Hilt | Enables injection | `@Inject FirebaseAuth` |

---

## 🔑 Every Keyword, Annotation & Concept Used

### `registerForActivityResult(GetContent())` — Modern file picker
```kotlin
private val photoLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
    uri ?: return@registerForActivityResult
    viewModel.uploadProfilePhoto(uid, uri)
}
```
`GetContent()` launches the system file picker to select an image (`image/*`). The result (a `Uri`) is delivered to the lambda. Returns `null` if the user cancels — `?: return@registerForActivityResult` handles this. Registered at Fragment creation time (before `onViewCreated`) — required by the API.

### `animateScaleIn(startDelay = 100L)` and `animateEntrance(startDelay, stepDelay)`
Custom extension functions from `Extensions.kt`. `animateScaleIn` applies a scale + fade-in animation to the avatar. `animateEntrance` staggers animations across a list of views (name, email, role) with `stepDelay` between each. Creates a polished entrance effect.

### Account type badge
```kotlin
if (user.accountType == "independent") {
    binding.chipAccountType.text = getString(R.string.badge_independent)
    binding.chipAccountType.setChipBackgroundColorResource(R.color.success)
} else {
    binding.chipAccountType.text = getString(R.string.badge_institution, user.institutionId)
    binding.chipAccountType.setChipBackgroundColorResource(R.color.info)
}
```
A Material `Chip` shows whether the account is institution-linked (blue) or independent (green).

### `flags = FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK` on logout
Clears the entire back stack after logout. Without these flags, pressing Back after logout would return to the main app screen without authentication.

---

## 📋 Properties

| Property | Type | Modifier | What It Holds | Why It Exists |
|----------|------|----------|--------------|--------------|
| `_binding` | `FragmentProfileBinding?` | `private var` | ViewBinding | Null in onDestroyView |
| `viewModel` | `ProfileViewModel` | `private val` | Profile state | Load, update, upload |
| `auth` | `FirebaseAuth` | `@Inject lateinit var` | Firebase auth service | UID, signOut |
| `photoLauncher` | `ActivityResultLauncher` | `private val` | Image picker launcher | Profile photo selection |

---

## ⚙️ Functions

### `onViewCreated(view, savedInstanceState)`
1. Runs entrance animations on avatar and text fields.
2. Calls `viewModel.loadProfile(uid)` to load user data.
3. `viewModel.user.observe` → binds all fields to the user object.
4. Avatar click → `photoLauncher.launch("image/*")`.
5. Save button → `viewModel.updateProfile(uid, name, bio)` → Snackbar "Profile saved!".
6. Logout button → confirmation dialog → `auth.signOut()` → `AuthActivity`.
7. Settings button → `findNavController().navigate(R.id.action_profile_to_settings)`.
8. `viewModel.photoUrl.observe` → updates avatar after upload.

---

## 🔄 Data Flow Diagram
```
Fragment opens → viewModel.loadProfile(uid)
        ↓
ProfileViewModel → AuthRepository.getUserById(uid)
        ↓
_user = User → Fragment binds all fields

User taps avatar → photoLauncher → system image picker → uri
        ↓
viewModel.uploadProfilePhoto(uid, uri) → StorageRepository.uploadProfilePhoto()
        ↓
Firebase Storage upload → download URL returned
        ↓
AuthRepository.updateUser(uid, photoUrl=url) → Firestore update
        ↓
viewModel._photoUrl = url → ivAvatar.loadAvatar(url)

Save button → viewModel.updateProfile(uid, name, bio)
        ↓
Firestore: users/{uid} name + bio updated
```

---

## ⚠️ Important Notes & Gotchas
- The profile photo is uploaded to `profile_photos/{uid}.jpg` — any new upload overwrites the old one.
- `viewModel.photoUrl` is a separate `LiveData<String>` updated only after photo upload. `viewModel.user` holds the full user object loaded at start. Both are needed because photo upload updates a different LiveData to immediately refresh the avatar without reloading the entire user.
