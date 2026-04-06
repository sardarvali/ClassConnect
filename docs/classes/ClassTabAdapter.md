# ClassTabAdapter.kt — ViewPager2 FragmentStateAdapter for the tabs inside ClassDetailActivity

---

## 📁 File Location
`app/src/main/java/com/syed/classconnect/ui/classes/ClassTabAdapter.kt`

---

## 🎯 What This File Does
`ClassTabAdapter` connects a `ViewPager2` widget to a list of Fragment instances inside `ClassDetailActivity`. Each page of the ViewPager corresponds to one tab: Feed, Assignments, Quiz, Attendance, Chat. `FragmentStateAdapter` manages Fragment lifecycles efficiently — it creates Fragments lazily and destroys off-screen ones to save memory. Without this adapter, `ClassDetailActivity` would have no way to display its tab pages.

---

## 📦 Every Import — Explained

| Import | From Library | What It Is | Why Used Here |
|--------|-------------|-----------|--------------|
| `androidx.fragment.app.Fragment` | AndroidX Fragment | Base fragment class | Type of each page |
| `androidx.fragment.app.FragmentActivity` | AndroidX Fragment | Activity that owns fragment manager | Constructor parameter |
| `androidx.viewpager2.adapter.FragmentStateAdapter` | ViewPager2 | Adapter that manages Fragment lifecycle | Base class |

---

## 🔑 Every Keyword, Annotation & Concept Used

### `FragmentStateAdapter`
Manages creating and destroying Fragments for ViewPager2. Unlike the older `FragmentPagerAdapter`, it destroys Fragment views when they go offscreen (saves memory). It creates a new Fragment instance for each page via `createFragment(position)`.

### `FragmentActivity` as constructor param
`FragmentStateAdapter` requires a `FragmentActivity` (or `Fragment`) to manage its fragment lifecycle. Passing `activity` ties the adapter's lifecycle to the Activity.

---

## 🏗️ Class Structure
`ClassTabAdapter(activity: FragmentActivity, fragments: List<Fragment>) : FragmentStateAdapter(activity)`

---

## 📋 Properties

| Property | Type | Modifier | What It Holds | Why It Exists |
|----------|------|----------|--------------|--------------|
| `fragments` | `List<Fragment>` | `private val` | Pre-created fragment instances | One per tab page |

---

## ⚙️ Functions

### `getItemCount(): Int`
Returns `fragments.size` — the number of pages/tabs.

### `createFragment(position: Int): Fragment`
Returns `fragments[position]` — the Fragment to show at this position.

---

## 📝 Full Source Code with Line-by-Line Comments

```kotlin
package com.syed.classconnect.ui.classes

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class ClassTabAdapter(activity: FragmentActivity, private val fragments: List<Fragment>) :
    FragmentStateAdapter(activity) {
    // Pass activity so the adapter can use its FragmentManager and lifecycle.

    override fun getItemCount() = fragments.size
    // Total number of pages = number of fragments passed in.

    override fun createFragment(position: Int) = fragments[position]
    // Return the pre-created Fragment for this page position.
    // ClassDetailActivity creates: [FeedFragment, AssignmentsFragment, QuizListFragment,
    //                                AttendanceFragment, ChatFragment]
}
```

