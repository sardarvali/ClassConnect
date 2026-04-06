# FeedAdapter.kt — RecyclerView adapter rendering class announcements with pin toggle support

---

## 📁 File Location
`app/src/main/java/com/syed/classconnect/ui/classes/feed/FeedAdapter.kt`

---

## 🎯 What This File Does
`FeedAdapter` renders a list of `FeedItem` objects (currently only `FeedItem.AnnouncementItem`) in `FeedFragment`. Each row shows the announcement title, body, author name, relative timestamp, and a pin icon that teachers can toggle. The adapter uses `ListAdapter<FeedItem, RecyclerView.ViewHolder>` to support future multi-type feed items (e.g., mixing announcements and materials). Without this adapter, the Feed tab's RecyclerView would have no content.

---

## 📦 Every Import — Explained

| Import | From Library | What It Is | Why Used Here |
|--------|-------------|-----------|--------------|
| `android.view.LayoutInflater` | Android SDK | XML → View | Inflate `item_announcement.xml` |
| `android.view.ViewGroup` | Android SDK | Parent container | `onCreateViewHolder` |
| `androidx.recyclerview.widget.DiffUtil` | RecyclerView | Diff algorithm | `ListAdapter` |
| `androidx.recyclerview.widget.ListAdapter` | RecyclerView | Adapter + DiffUtil | Base class |
| `androidx.recyclerview.widget.RecyclerView` | RecyclerView | ViewHolder parent | ViewHolder base class |
| `com.syed.classconnect.data.model.Announcement` | Project | Announcement data class | Unwrapped from `FeedItem` |
| `com.syed.classconnect.databinding.ItemAnnouncementBinding` | ViewBinding | `item_announcement.xml` typed access | `tvTitle`, `tvBody`, `tvAuthor`, `tvTime`, `ivPin` |
| `com.syed.classconnect.util.DateUtils.toRelativeTime` | Project | Timestamp → relative time | "2 hours ago" |

---

## 🔑 Every Keyword, Annotation & Concept Used

### `ListAdapter<FeedItem, RecyclerView.ViewHolder>`
The outer type is `FeedItem` (the sealed class), `RecyclerView.ViewHolder` is the common base. This allows adding new `onCreateViewHolder` view types in future without changing the class signature.

### `(holder as? AnnouncementViewHolder)?.bind(...)`
Safe cast: if `holder` is an `AnnouncementViewHolder`, call `bind`. If other types are added, they would be handled with additional `as?` casts.

### `binding.ivPin.isSelected = item.isPinned`
`isSelected` state changes the pin icon's drawable state (defined in a state-list drawable in XML). `selected = true` → filled pin icon; `selected = false` → outline pin icon.

### `DiffCallback.areItemsTheSame`
Compares two `FeedItem` objects: if both are `AnnouncementItem`, compare their announcement IDs. This is the "stable ID" check — if items have the same ID, the framework knows they're the same row.

---

## 📝 Full Source Code with Line-by-Line Comments

```kotlin
package com.syed.classconnect.ui.classes.feed

// (imports as listed above)

class FeedAdapter(
    private val onPinClick: (Announcement) -> Unit
    // Callback for pin/unpin action — only shown/active for teachers.
) : ListAdapter<FeedItem, RecyclerView.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemAnnouncementBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AnnouncementViewHolder(binding)
        // Currently only one view type. Multi-type support ready via viewType parameter.
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as? AnnouncementViewHolder)?.bind((getItem(position) as FeedItem.AnnouncementItem).announcement)
        // Safely cast and unwrap the FeedItem to Announcement.
    }

    inner class AnnouncementViewHolder(private val binding: ItemAnnouncementBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Announcement) {
            binding.tvTitle.text = item.title
            binding.tvBody.text = item.body
            binding.tvAuthor.text = item.authorName
            binding.tvTime.text = item.createdAt.toRelativeTime()
            binding.ivPin.isSelected = item.isPinned
            // isSelected = true → filled pin icon (XML state list drawable).
            binding.ivPin.setOnClickListener { onPinClick(item) }
            // Teacher taps pin → FeedViewModel.togglePin() → Firestore update.
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<FeedItem>() {
        override fun areItemsTheSame(a: FeedItem, b: FeedItem): Boolean =
            (a is FeedItem.AnnouncementItem && b is FeedItem.AnnouncementItem && a.announcement.id == b.announcement.id)
        // Two items are "the same item" if they are both AnnouncementItems with the same ID.
        override fun areContentsTheSame(a: FeedItem, b: FeedItem) = a == b
        // No visual change needed if data class fields are equal.
    }
}
```

