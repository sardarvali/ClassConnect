# SettingsFragment.kt — Fragment for app preferences: theme, notifications, biometric lock, and about links

---

## 📁 File Location
`app/src/main/java/com/syed/classconnect/ui/profile/SettingsFragment.kt`

---

## 🎯 What This File Does
`SettingsFragment` provides the user-configurable app settings screen. It manages three preference categories: (1) Theme — light, dark, or follow system, persisted in SharedPreferences and applied immediately via `AppCompatDelegate`; (2) Notifications toggle — persisted in SharedPreferences; (3) Biometric lock — checks hardware availability, authenticates before enabling, shows appropriate messages for missing hardware or unenrolled biometrics. It also shows Privacy Policy and Terms of Service links (opening WebViewActivity) and the app version. Without this fragment, users could not change the app theme or configure biometric lock.

---

## 📦 Every Import — Explained

| Import | From Library | What It Is | Why Used Here |
|--------|-------------|-----------|--------------|
| `android.os.Bundle` | Android SDK | State map | Fragment lifecycle |
| `android.view.LayoutInflater` | Android SDK | XML → View | `fragment_settings.xml` |
| `android.view.View` | Android SDK | Base view | `onViewCreated` param |
| `android.view.ViewGroup` | Android SDK | Container | `onCreateView` param |
| `android.widget.Toast` | Android SDK | Short text popup | "Biometric auth failed" message |
| `androidx.appcompat.app.AppCompatDelegate` | AndroidX | Theme/night mode control | `setDefaultNightMode()`, mode constants |
| `androidx.fragment.app.Fragment` | AndroidX | Base Fragment | SettingsFragment extends it |
| `android.content.Context` | Android SDK | App context | `getSharedPreferences()` |
| `com.google.android.material.snackbar.Snackbar` | Material | Snackbar messages | Biometric status feedback |
| `com.syed.classconnect.R` | Project | Resource IDs | String resources |
| `com.syed.classconnect.databinding.FragmentSettingsBinding` | ViewBinding | `fragment_settings.xml` typed access | All preference views |
| `com.syed.classconnect.util.BiometricHelper` | Project | Biometric status/auth | `canAuthenticate()`, `authenticate()` |
| `com.syed.classconnect.util.Constants` | Project | `PREF_BIOMETRIC_ENABLED` key | SharedPreferences key |
| `dagger.hilt.android.AndroidEntryPoint` | Hilt | Enables DI | Required for DI in this fragment |

---

## 🔑 Every Keyword, Annotation & Concept Used

### `AppCompatDelegate.setDefaultNightMode(mode)`
Changes the app-wide theme immediately without restarting:
- `MODE_NIGHT_NO` = always light
- `MODE_NIGHT_YES` = always dark
- `MODE_NIGHT_FOLLOW_SYSTEM` = follows device system setting
The change is applied to all Activities instantly. SharedPreferences stores the chosen mode so it persists across app launches.

### `prefs.edit().putInt("theme_mode", mode).apply()`
`SharedPreferences.edit()` returns an `Editor`. `.apply()` writes asynchronously (preferred over `.commit()` which is synchronous and blocks the main thread).

### `BiometricHelper.canAuthenticate(context): BiometricStatus`
Returns one of:
- `AVAILABLE`: hardware present and biometrics enrolled → enable the switch
- `NO_HARDWARE`: no biometric sensor → disable switch
- `UNAVAILABLE`: hardware exists but currently unavailable (e.g., too many failed attempts)
- `NONE_ENROLLED`: hardware exists but no fingerprint/face set up → show enrollment message

### `BiometricHelper.authenticate()` — authenticate before enabling
When the user turns ON the biometric switch, we immediately prompt for authentication. If auth succeeds, we save `PREF_BIOMETRIC_ENABLED = true`. If the user cancels (`onError` with empty string), we revert the switch to off. This prevents someone from enabling biometric lock without proving they have the correct biometric.

### `requireContext().packageManager.getPackageInfo(requireContext().packageName, 0).versionName`
Gets the app's version name (e.g., "1.0.0") from the installed package information. Wrapped in try/catch since `PackageManager.NameNotFoundException` can theoretically be thrown.

---

## 🏗️ Class Structure
`@AndroidEntryPoint class SettingsFragment : Fragment()` — no ViewModel (all state from SharedPreferences).

---

## 📋 Properties

| Property | Type | Modifier | What It Holds | Why It Exists |
|----------|------|----------|--------------|--------------|
| `_binding` | `FragmentSettingsBinding?` | `private var` | Nullable ViewBinding | Null-ed in onDestroyView |
| `binding` | `FragmentSettingsBinding` | `private val get()` | Non-null accessor | Safe view access |

---

## ⚙️ Functions

### `onViewCreated(view, savedInstanceState)`
**Purpose:** Read current preferences, configure all controls.
**Step by step:**
1. Gets SharedPreferences `"classconnect_prefs"`.
2. **Theme**: reads `"theme_mode"`, checks the appropriate radio button; radio group listener → saves mode + calls `AppCompatDelegate.setDefaultNightMode(mode)`.
3. **Notifications**: reads `"notifications_enabled"`, sets switch; listener → saves to prefs.
4. **Biometric**: calls `setupBiometric(prefs)`.
5. **Privacy Policy** click → starts `WebViewActivity` with `url` and `title` extras.
6. **Terms** click → same.
7. Gets app version from PackageManager, sets `tvVersion`.

### `setupBiometric(prefs: SharedPreferences)`
**Purpose:** Configure biometric switch based on hardware status.
**Step by step:**
1. Calls `BiometricHelper.canAuthenticate()`.
2. Reads current `PREF_BIOMETRIC_ENABLED` from prefs.
3. `NO_HARDWARE` / `UNAVAILABLE`: disable switch, show "not available" text.
4. `NONE_ENROLLED`: enable switch, but turning it ON shows a Snackbar telling the user to enroll biometrics in Settings.
5. `AVAILABLE`: enable switch, show current state. Turning ON → `BiometricHelper.authenticate()` → on success save `true` + show snackbar. Turning OFF → save `false`.

---

## ⚠️ Important Notes & Gotchas
- Theme change is applied **immediately** via `AppCompatDelegate.setDefaultNightMode()` — the Activity recreates (screen flashes). This is correct behavior.
- Biometric must be verified before enabling to prevent someone enabling it without their own biometric.
- The `"notifications_enabled"` preference is read by `MyFirebaseMessagingService` (or other notification logic) to determine whether to show notifications. It doesn't actually grant/revoke OS notification permission — only the OS permission does that.

