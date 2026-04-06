# Extensions — Kotlin extension functions for views and fragments

---

## 📁 Location
`app/src/main/java/com/syed/classconnect/util/Extensions.kt`

---

## 🎯 What This File Does
Extensions.kt provides Kotlin extension functions that simplify common UI operations. These are used throughout all Fragments and Activities to reduce boilerplate code for showing/hiding views, displaying messages, and adding animations.

---

## 📦 Imports

| Import | Library / Package | What It Is | Why This File Needs It |
|--------|------------------|-----------|----------------------|
| `android.view.View` | Android SDK | Base view class | Extension functions on View |
| `android.view.animation.*` | Android SDK | Animation classes | Wiggle animation |
| `android.widget.Toast` | Android SDK | Short popup message | showToast extension |
| `androidx.fragment.app.Fragment` | AndroidX | Fragment base | Extensions on Fragment |
| `com.google.android.material.snackbar.Snackbar` | Material | Bottom notification | showSnackbar extension |

---

## ⚙️ Extension Functions

### `View.show()`
```kotlin
fun View.show() { visibility = View.VISIBLE }
```
Sets view visibility to VISIBLE. Shorthand for `view.visibility = View.VISIBLE`.

### `View.hide()`
```kotlin
fun View.hide() { visibility = View.GONE }
```
Sets view visibility to GONE (doesn't occupy space). Shorthand for `view.visibility = View.GONE`.

### `View.invisible()`
```kotlin
fun View.invisible() { visibility = View.INVISIBLE }
```
Sets view visibility to INVISIBLE (still occupies space but not drawn).

### `View.wiggle()`
```kotlin
fun View.wiggle() {
    val anim = TranslateAnimation(-10f, 10f, 0f, 0f).apply {
        duration = 50
        repeatCount = 5
        repeatMode = Animation.REVERSE
    }
    startAnimation(anim)
}
```
Shakes the view horizontally — used on input fields when validation fails.

### `Fragment.showSnackbar(message: String)`
```kotlin
fun Fragment.showSnackbar(message: String) {
    Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show()
}
```
Shows a Material Snackbar at the bottom of the Fragment's view.

### `Fragment.showToast(message: String)`
```kotlin
fun Fragment.showToast(message: String) {
    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
}
```
Shows a Toast message.

---

## ⚠️ Important Notes
- Extension functions add methods to existing classes without modifying them
- `View.hide()` uses `GONE` (not `INVISIBLE`) — the view doesn't take up space
- `wiggle()` animation repeats 5 times with reverse for a shake effect
- These extensions must be imported at the call site: `import com.syed.classconnect.util.show`

