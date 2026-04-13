# FeedFragment.kt — Tab fragment that displays pinned and recent announcements for a class with teacher posting ability

---

## 📁 File Location
`app/src/main/java/com/syed/classconnect/ui/classes/feed/FeedFragment.kt`

---

## 🎯 What This File Does
`FeedFragment` is the "Feed" tab inside `ClassDetailActivity`. It shows a real-time list of class announcements sorted by pin status first, then by recency. Teachers and admins see a floating action button (FAB) that opens `PostAnnouncementDialog` to post new announcements. Students see the feed in read-only mode. It supports swipe-to-refresh. The fragment receives its `classId` via a `Bundle` argument (not via navigation args) because it is created programmatically by `ClassTabAdapter`. Without this fragment, the Feed tab would be blank.

---

## 📦 Every Import — Explained

| Import | From Library | What It Is | Why Used Here |
|--------|-------------|-----------|--------------|
| `android.os.Bundle` | Android SDK | Key-value argument map | Receives `classId` argument |
| `android.view.LayoutInflater` | Android SDK | XML → View | `onCreateView` inflation |
| `android.view.View` | Android SDK | Base view class | `onViewCreated` param |
| `android.view.ViewGroup` | Android SDK | Layout container | `onCreateView` param |
| `androidx.fragment.app.Fragment` | AndroidX | Base Fragment class | FeedFragment extends it |
| `androidx.fragment.app.viewModels` | AndroidX | ViewModel delegation | `by viewModels()` |
| `androidx.recyclerview.widget.LinearLayoutManager` | RecyclerView | Vertical list layout | RecyclerView layout manager |
| `com.google.firebase.auth.FirebaseAuth` | Firebase | Current user | Gets UID to load role |
| `com.syed.classconnect.databinding.FragmentFeedBinding` | ViewBinding | `fragment_feed.xml` typed access | `rvFeed`, `fab`, `layoutEmpty`, `swipeRefresh` |
| `com.syed.classconnect.util.Constants` | Project | `EXTRA_CLASS_ID` constant | Bundle argument key |
| `com.syed.classconnect.util.hide` | Project | Extension: `visibility = GONE` | Hide empty state / RecyclerView |
| `com.syed.classconnect.util.show` | Project | Extension: `visibility = VISIBLE` | Show empty state / RecyclerView |
| `dagger.hilt.android.AndroidEntryPoint` | Hilt | Enables DI in this Fragment | `@Inject` works |
| `javax.inject.Inject` | Javax / Hilt | Field injection | Injects `FirebaseAuth` |

---

## 🔑 Every Keyword, Annotation & Concept Used

### `companion object { fun newInstance(classId: String) }`
Factory method for creating the Fragment with its argument. Preferred over constructors with parameters (which break on configuration changes). Sets `classId` as a Bundle argument.

### `arguments?.getString(Constants.EXTRA_CLASS_ID) ?: return`
Reads the classId from Bundle. If null (shouldn't happen), returns early — guard clause.

### `@AndroidEntryPoint` + `@Inject lateinit var auth: FirebaseAuth`
Hilt injects FirebaseAuth into the fragment. This fragment doesn't use a ViewModel for auth but reads the current user's UID directly to call `loadUserRole`.

### `by viewModels()`
Scopes the `FeedViewModel` to this Fragment's lifecycle. Each FeedFragment instance gets its own ViewModel.

### `viewModel.feedItems.observe(viewLifecycleOwner) { items -> }`
`viewLifecycleOwner` is the Fragment's view lifecycle — the observer is automatically removed when the view is destroyed (between `onDestroyView` and the next `onCreateView`). Using `viewLifecycleOwner` instead of `this` prevents leaks.

### `binding.fab.show()` / `.hide()`
These are FAB-specific methods (not the same as `View.visibility`). They animate the FAB in/out with a Material animation.

### `PostAnnouncementDialog.newInstance(classId).show(parentFragmentManager, "post_announcement")`
Shows the bottom sheet dialog. `parentFragmentManager` is used (not `childFragmentManager`) so the dialog can access the same ViewModel via `by viewModels()`.

---

## 🏗️ Class Structure
`@AndroidEntryPoint class FeedFragment : Fragment()` — no layout ID in constructor (uses `onCreateView`).

---

## 📋 Properties

| Property | Type | Modifier | What It Holds | Why It Exists |
|----------|------|----------|--------------|--------------|
| `_binding` | `FragmentFeedBinding?` | `private var` | Nullable ViewBinding | Null-ed in onDestroyView |
| `binding` | `FragmentFeedBinding` | `private val get()` | Non-null ViewBinding accessor | Safe access between create and destroy |
| `viewModel` | `FeedViewModel` | `private val` | ViewModel for this fragment | Business logic + data |
| `auth` | `FirebaseAuth` | `@Inject lateinit var` | Current user authentication | Get UID for role check |
| `adapter` | `FeedAdapter` | `private lateinit var` | RecyclerView adapter | Renders announcement list |
| `classId` | `String` | `private lateinit var` | The class ID passed via arguments | Firestore query key |

---

## ⚙️ Functions

### `newInstance(classId: String): FeedFragment` *(companion)*
Creates the fragment with classId bundled as an argument.

### `onCreateView(...)` 
Inflates `FragmentFeedBinding`, returns root.

### `onViewCreated(view, savedInstanceState)`
**Step by step:**
1. Reads `classId` from arguments.
2. Gets UID, calls `viewModel.loadUserRole(uid)`.
3. Creates `FeedAdapter` with pin click handler.
4. Sets up RecyclerView with LinearLayoutManager.
5. Calls `viewModel.loadFeed(classId)`.
6. Observes `feedItems`: if empty → show empty layout, hide list; else → show list.
7. Observes `userRole`: teacher/admin → show FAB; student → hide FAB.
8. FAB click → show `PostAnnouncementDialog`.
9. Swipe refresh → reload feed.

### `onDestroyView()`
Sets `_binding = null` to prevent memory leak.

---

## 🔄 Data Flow Diagram
```
FeedFragment created with classId="abc123"
        ↓
viewModel.loadFeed("abc123")
        ↓
FeedRepository.getAnnouncements("abc123") — callbackFlow
        ↓
Firestore real-time listener on classes/abc123/announcements
        ↓
_feedItems updated → sorted (pinned first, then by date)
        ↓
Fragment observes → adapter.submitList(items)
        ↓
RecyclerView renders announcements

Teacher taps FAB → PostAnnouncementDialog shown
        ↓
viewModel.postAnnouncement(classId, title, body, uid, name)
        ↓
FeedRepository.postAnnouncement() → Firestore write
        ↓
Real-time listener fires → feed auto-refreshes
```

---

## 🧩 Dependencies

| Depends On | Why |
|-----------|-----|
| `FeedViewModel` | Business logic, data loading |
| `FeedAdapter` | Renders announcement list |
| `PostAnnouncementDialog` | Teacher announcement posting UI |
| `FeedRepository` | (via ViewModel) Firestore reads/writes |
| `Constants.EXTRA_CLASS_ID` | Bundle argument key |

---

## ⚠️ Important Notes & Gotchas
- `classId` is read from arguments in `onViewCreated`, not `onCreateView`. This is safe because arguments are available at both points.
- FAB visibility is driven by `userRole` — it updates asynchronously after `loadUserRole()` completes. There is a brief moment where the FAB might be visible for a student before the role loads; `hide()` is called when the role arrives.
- `parentFragmentManager` (not `childFragmentManager`) is used for the dialog so it shares the same ViewModel scope.

---

## 📝 Full Source Code with Line-by-Line Comments

```kotlin
package com.syed.classconnect.ui.classes.feed

// (imports as listed above)

@AndroidEntryPoint
class FeedFragment : Fragment() {

    private var _binding: FragmentFeedBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FeedViewModel by viewModels()
    @Inject lateinit var auth: FirebaseAuth

    private lateinit var adapter: FeedAdapter
    private lateinit var classId: String

    companion object {
        fun newInstance(classId: String) = FeedFragment().apply {
            arguments = Bundle().apply { putString(Constants.EXTRA_CLASS_ID, classId) }
            // Passes classId via Bundle — survives configuration changes.
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentFeedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        classId = arguments?.getString(Constants.EXTRA_CLASS_ID) ?: return
        // Guard: if classId missing, do nothing.

        val uid = auth.currentUser?.uid ?: return
        viewModel.loadUserRole(uid)
        // Async: loads role to show/hide FAB.

        adapter = FeedAdapter(
            onPinClick = { ann -> viewModel.togglePin(classId, ann.id, !ann.isPinned) }
            // Teacher can toggle pin status.
        )
        binding.rvFeed.layoutManager = LinearLayoutManager(requireContext())
        binding.rvFeed.adapter = adapter

        viewModel.loadFeed(classId)
        // Starts listening to Firestore real-time announcements.

        viewModel.feedItems.observe(viewLifecycleOwner) { items ->
            if (items.isEmpty()) { binding.layoutEmpty.show(); binding.rvFeed.hide() }
            else { binding.layoutEmpty.hide(); binding.rvFeed.show(); adapter.submitList(items) }
        }

        viewModel.userRole.observe(viewLifecycleOwner) { role ->
            if (role == Constants.ROLE_TEACHER || role == Constants.ROLE_ADMIN) {
                binding.fab.show()    // Teachers can post announcements.
            } else {
                binding.fab.hide()   // Students: read-only feed.
            }
        }

        binding.fab.setOnClickListener {
            PostAnnouncementDialog.newInstance(classId)
                .show(parentFragmentManager, "post_announcement")
            // parentFragmentManager: dialog shares ViewModel with this fragment.
        }

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.loadFeed(classId)
            binding.swipeRefresh.isRefreshing = false
        }
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
```

