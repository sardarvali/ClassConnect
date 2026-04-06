# PostAnnouncementDialog.kt — Bottom sheet dialog for teachers to post a new class announcement

---

## 📁 File Location
`app/src/main/java/com/syed/classconnect/ui/classes/feed/PostAnnouncementDialog.kt`

---

## 🎯 What This File Does
`PostAnnouncementDialog` is a `BottomSheetDialogFragment` that slides up from the bottom of the screen when the teacher taps the FAB in `FeedFragment`. It contains a title field and body field. When the teacher submits, it calls `FeedViewModel.postAnnouncement()` which writes the announcement to Firestore. The dialog then dismisses itself. Because it uses `by viewModels()` (NOT `by activityViewModels()`), it shares the same `FeedViewModel` instance as the parent `FeedFragment` when both are in the same `parentFragmentManager`. Without this dialog, teachers would have no way to create new announcements.

---

## 📦 Every Import — Explained

| Import | From Library | What It Is | Why Used Here |
|--------|-------------|-----------|--------------|
| `android.os.Bundle` | Android SDK | Key-value map | Arguments and savedInstanceState |
| `android.view.LayoutInflater` | Android SDK | XML → View | `onCreateView` inflation |
| `android.view.View` | Android SDK | Base view | `onViewCreated` param |
| `android.view.ViewGroup` | Android SDK | Layout container | `onCreateView` param |
| `androidx.fragment.app.viewModels` | AndroidX | ViewModel delegation | `by viewModels()` |
| `com.google.android.material.bottomsheet.BottomSheetDialogFragment` | Material | Bottom sheet dialog | Base class |
| `com.google.firebase.auth.FirebaseAuth` | Firebase | Current user | Gets UID for author |
| `com.syed.classconnect.databinding.BottomSheetPostAnnouncementBinding` | ViewBinding | `bottom_sheet_post_announcement.xml` | `etTitle`, `etBody`, `btnPost` |
| `com.syed.classconnect.util.Constants` | Project | `EXTRA_CLASS_ID` | Bundle argument key |
| `dagger.hilt.android.AndroidEntryPoint` | Hilt | Enables DI | `@Inject` in this fragment |
| `javax.inject.Inject` | Javax / Hilt | Field injection | Injects `FirebaseAuth` |

---

## 🔑 Every Keyword, Annotation & Concept Used

### `BottomSheetDialogFragment`
A special `DialogFragment` that renders as a Material bottom sheet — slides up from the bottom of the screen with a drag handle and rounded corners. Shows system keyboard automatically when text fields are focused.

### `by viewModels()` in a Dialog Fragment
When `PostAnnouncementDialog` is shown via `parentFragmentManager`, it is attached to the **same** fragment stack as `FeedFragment`. `by viewModels()` in this context returns the SAME `FeedViewModel` instance as `FeedFragment`'s `by viewModels()` — because they share the same `parentFragmentManager` scope.
> Important: If it were shown via `childFragmentManager`, the ViewModel scope would be different.

### `if (title.isEmpty() || body.isEmpty()) return@setOnClickListener`
Guard clause inside a lambda. `return@setOnClickListener` exits the click listener lambda without doing anything — effectively ignores the tap if fields are empty.

### `viewModel.currentUserName`
The dialog reads the author name from the ViewModel's `currentUserName` property (set when `loadUserRole()` ran in `FeedFragment`). This avoids a second Firestore lookup.

### `dismiss()`
Closes the bottom sheet with a slide-down animation.

---

## 🏗️ Class Structure
`@AndroidEntryPoint class PostAnnouncementDialog : BottomSheetDialogFragment()`

---

## 📋 Properties

| Property | Type | Modifier | What It Holds | Why It Exists |
|----------|------|----------|--------------|--------------|
| `_binding` | `BottomSheetPostAnnouncementBinding?` | `private var` | Nullable ViewBinding | Null-ed in onDestroyView |
| `binding` | `BottomSheetPostAnnouncementBinding` | `private val get()` | Non-null accessor | Safe view access |
| `viewModel` | `FeedViewModel` | `private val` | Shared ViewModel | Post announcement, get user name |
| `auth` | `FirebaseAuth` | `@Inject lateinit var` | Current user auth | Gets UID for authorId |
| `classId` | `String` | `private lateinit var` | Class to post to | Firestore path key |

---

## ⚙️ Functions

### `newInstance(classId: String): PostAnnouncementDialog` *(companion)*
Factory method that creates the dialog with `classId` bundled as an argument.

### `onViewCreated(view, savedInstanceState)`
**Step by step:**
1. Reads `classId` from arguments.
2. Gets UID from `auth.currentUser?.uid`.
3. Calls `viewModel.loadUserRole(uid)` — ensures `currentUserName` is populated.
4. Sets `btnPost` click listener:
   - Reads `title` and `body` from text fields.
   - If either is empty → returns (no-op).
   - Calls `viewModel.postAnnouncement(classId, title, body, uid, viewModel.currentUserName)`.
   - Calls `dismiss()`.

---

## ⚠️ Important Notes & Gotchas
- `viewModel.currentUserName` may be "Teacher" (the default) if `loadUserRole()` hasn't completed yet. This is a minor race condition — in practice, the role loads fast enough that it resolves before the user finishes typing.
- The dialog does NOT validate field lengths or content. An empty-looking title (e.g., just spaces) that's not empty after trimming would still post. Consider adding `.trim()` validation.

---

## 📝 Full Source Code with Line-by-Line Comments

```kotlin
package com.syed.classconnect.ui.classes.feed

// (imports as listed above)

@AndroidEntryPoint
class PostAnnouncementDialog : BottomSheetDialogFragment() {

    private var _binding: BottomSheetPostAnnouncementBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FeedViewModel by viewModels()
    // Shares ViewModel with FeedFragment when shown via parentFragmentManager.
    @Inject lateinit var auth: FirebaseAuth

    private lateinit var classId: String

    companion object {
        fun newInstance(classId: String) = PostAnnouncementDialog().apply {
            arguments = Bundle().apply { putString(Constants.EXTRA_CLASS_ID, classId) }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = BottomSheetPostAnnouncementBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        classId = arguments?.getString(Constants.EXTRA_CLASS_ID) ?: return
        val uid = auth.currentUser?.uid ?: return

        viewModel.loadUserRole(uid)
        // Ensures currentUserName is populated before the user submits.

        binding.btnPost.setOnClickListener {
            val title = binding.etTitle.text.toString().trim()
            val body = binding.etBody.text.toString().trim()
            if (title.isEmpty() || body.isEmpty()) return@setOnClickListener
            // Basic validation — both fields required.

            val currentUser = viewModel.currentUserName
            // Gets the already-loaded user display name.

            viewModel.postAnnouncement(classId, title, body, uid, currentUser)
            dismiss()
            // Close the sheet — the feed will auto-refresh via Firestore listener.
        }
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
```

