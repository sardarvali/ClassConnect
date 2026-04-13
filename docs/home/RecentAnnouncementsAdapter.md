# RecentAnnouncementsAdapter.kt — RecyclerView adapter for recent class announcements on the student home screen

---

## 📁 File Location
`app/src/main/java/com/syed/classconnect/ui/home/RecentAnnouncementsAdapter.kt`

---

## 🎯 What This File Does
`RecentAnnouncementsAdapter` displays up to 3 recent `Announcement` items in the student home screen. Each row shows the announcement title, body text, author name, and a relative timestamp ("2 hours ago"). Tapping triggers the `onClick` callback. It is a minimal `ListAdapter` using ViewBinding and `DiffUtil`. Without it, the "Recent Announcements" section on the student home screen would have no content.

---

## 📦 Every Import — Explained

| Import | From Library | What It Is | Why Used Here |
|--------|-------------|-----------|--------------|
| `android.view.LayoutInflater` | Android SDK | Inflates XML layout | Creates `item_announcement.xml` views |
| `android.view.ViewGroup` | Android SDK | Parent container | `onCreateViewHolder` param |
| `androidx.recyclerview.widget.DiffUtil` | RecyclerView | List diff | Powers `ListAdapter` |
| `androidx.recyclerview.widget.ListAdapter` | RecyclerView | Adapter base with diff | Base class |
| `androidx.recyclerview.widget.RecyclerView` | RecyclerView | ViewHolder parent | ViewHolder base class |
| `com.syed.classconnect.data.model.Announcement` | Project | Announcement data class | Item type |
| `com.syed.classconnect.databinding.ItemAnnouncementBinding` | ViewBinding | `item_announcement.xml` typed access | `tvTitle`, `tvBody`, `tvAuthor`, `tvTime` |
| `com.syed.classconnect.util.DateUtils.toRelativeTime` | Project | Timestamp → relative string | "2 hours ago", "just now" |

---

## 🔑 Every Keyword, Annotation & Concept Used

### `toRelativeTime()`
Extension function on `Timestamp`. Converts a Firebase Timestamp to a human-friendly relative string like "just now", "5 minutes ago", "2 days ago". Imported as a static import from `DateUtils`.

---

## 🏗️ Class Structure
`RecentAnnouncementsAdapter(onClick) : ListAdapter<Announcement, VH>(DIFF)`

---

## ⚙️ Functions

### `bind(a: Announcement)`
Sets `tvTitle`, `tvBody`, `tvAuthor`, `tvTime` (via `toRelativeTime()`), and click listener.

---

## 📝 Full Source Code with Line-by-Line Comments

```kotlin
package com.syed.classconnect.ui.home

// (imports as listed above)

/** Recent announcements list on the student home screen. */
class RecentAnnouncementsAdapter(
    private val onClick: ((Announcement) -> Unit)? = null
) : ListAdapter<Announcement, RecentAnnouncementsAdapter.VH>(DIFF) {

    inner class VH(private val b: ItemAnnouncementBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(a: Announcement) {
            b.tvTitle.text     = a.title
            b.tvBody.text      = a.body
            b.tvAuthor.text    = a.authorName
            b.tvTime.text      = a.createdAt.toRelativeTime()
            // toRelativeTime() converts Timestamp → "just now", "5 min ago", etc.
            b.root.setOnClickListener { onClick?.invoke(a) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemAnnouncementBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(getItem(position))

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<Announcement>() {
            override fun areItemsTheSame(a: Announcement, b: Announcement) = a.id == b.id
            override fun areContentsTheSame(a: Announcement, b: Announcement) = a == b
        }
    }
}
```

