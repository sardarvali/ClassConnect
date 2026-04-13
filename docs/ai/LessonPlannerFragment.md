# LessonPlannerFragment.kt — Teacher-only AI lesson plan generator with copy-to-clipboard

---

## 📁 File Location
`app/src/main/java/com/syed/classconnect/ui/ai/LessonPlannerFragment.kt`

---

## 🎯 What This File Does
`LessonPlannerFragment` provides a form where teachers enter subject, topic, grade/level, duration, and learning objectives, then tap "Generate" to receive an AI-written lesson plan from Gemini. The result is displayed as plain text and can be copied to the clipboard with a single tap. A "Regenerate" button re-runs the same form for a fresh result. The loading spinner disables the Generate button while the API call is in progress. Without this fragment, the Lesson Planner menu item shows nothing.

---

## 📦 Every Import — Explained

| Import | From Library | What It Is | Why Used Here |
|--------|-------------|-----------|--------------|
| `android.content.ClipData` | Android SDK | Clipboard data holder | Set clipboard text |
| `android.content.ClipboardManager` | Android SDK | System clipboard service | Copy plan text |
| `android.content.Context` | Android SDK | App context | `getSystemService(CLIPBOARD_SERVICE)` |
| `android.os.Bundle` | Android SDK | State map | Lifecycle |
| `android.view.LayoutInflater/View/ViewGroup` | Android SDK | View inflation | `onCreateView` |
| `androidx.fragment.app.Fragment` | AndroidX | Base Fragment | This extends it |
| `androidx.fragment.app.viewModels` | AndroidX | ViewModel delegate | Shared `AIViewModel` |
| `com.syed.classconnect.R` | Project | Resource IDs | `R.string.copied` |
| `com.syed.classconnect.databinding.FragmentLessonPlannerBinding` | ViewBinding | `fragment_lesson_planner.xml` | All views |
| `com.syed.classconnect.util.hide/show` | Project extensions | Toggle view visibility | Hide result until generated |
| `com.syed.classconnect.util.showSnackbar` | Project extension | Inline feedback | Error + "Copied!" messages |
| `dagger.hilt.android.AndroidEntryPoint` | Hilt | Enables injection | `by viewModels()` |

---

## 🔑 Every Keyword, Annotation & Concept Used

### `ClipboardManager` / `ClipData.newPlainText()`
```kotlin
val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
clipboard.setPrimaryClip(ClipData.newPlainText("Lesson Plan", lastGeneratedPlan))
```
`ClipData.newPlainText(label, text)` creates a clipboard entry. `setPrimaryClip` replaces whatever was previously on the clipboard. `label` is shown in some clipboard managers but not by the app.

### `lastGeneratedPlan: String` — private var
Caches the last generated plan text so the Copy button always copies the most-recently-generated plan, even if `tvPlan.text` were somehow cleared.

### `viewModel.generateLessonPlan(...) { plan -> }` — callback pattern
The result arrives via a lambda callback (not LiveData). The callback updates:
- `lastGeneratedPlan = plan`
- `binding.tvPlan.text = plan` (plain text — no Markdown rendering here unlike AIBuddyFragment)
- Shows `layoutResult`, `btnRegenerate`, `btnCopy`

### Guard: `if (subject.isEmpty() || topic.isEmpty()) return`
Subject and topic are required. Grade, duration, and objectives are optional (Gemini can infer defaults). Shows Snackbar if missing.

---

## 📋 Properties

| Property | Type | Modifier | What It Holds | Why It Exists |
|----------|------|----------|--------------|--------------|
| `_binding` | `FragmentLessonPlannerBinding?` | `private var` | ViewBinding | Null on destroy |
| `viewModel` | `AIViewModel` | `private val` | Shared AI ViewModel | `generateLessonPlan()` |
| `lastGeneratedPlan` | `String` | `private var` | Last result text | Copy button content |

---

## ⚙️ Functions

### `onViewCreated(view, savedInstanceState)`
1. "Generate" button → `generatePlan()`.
2. "Regenerate" button → `generatePlan()` (same function).
3. "Copy" button → copies `lastGeneratedPlan` to clipboard → "Copied!" Snackbar.
4. Observes `viewModel.isLoading` → toggle `progressBar` visibility and button state.
5. Observes `viewModel.error` → shows Snackbar on error.

### `generatePlan()` *(private)*
1. Reads all 5 input fields.
2. Guards: subject and topic must not be empty.
3. Hides `layoutResult` (clear previous result).
4. Calls `viewModel.generateLessonPlan(subject, topic, grade, duration, objectives) { plan -> ... }`.
5. In callback: saves plan text, shows result card, shows Regenerate and Copy buttons.

---

## 🔄 Data Flow Diagram
```
Teacher fills form + taps Generate
        ↓
generatePlan() validates inputs
        ↓
viewModel.generateLessonPlan(subject, topic, grade, duration, objectives) { plan -> }
        ↓
AIViewModel → GeminiRepository.generateContent(emptyList(), prompt)
        ↓
Retrofit POST → Gemini API → response text (Markdown)
        ↓
NetworkResult.Success(text) → onResult(text) callback
        ↓
lastGeneratedPlan = text
binding.tvPlan.text = text
layoutResult.show()
```
