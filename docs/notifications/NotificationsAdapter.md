# NotificationsAdapter.kt — RecyclerView adapter for in-app notifications with read/unread visual state

---

## 📁 File Location
`app/src/main/java/com/syed/classconnect/ui/notifications/NotificationsAdapter.kt`

---

## 🎯 What This File Does
`NotificationsAdapter` renders a list of `AppNotification` objects in `NotificationsFragment`. Each row shows the notification title, body, relative time, and a colored dot for unread notifications (hidden when read). Unread notifications are full opacity; read ones are 70% opacity. Tapping a notification calls the `onRead` callback which marks it as read. Cards animate in with a slide-up + fade stagger. Without this adapter, the notifications screen would be blank.

---

## 📦 Every Import — Explained

| Import | From Library | What It Is | Why Used Here |
|--------|-------------|-----------|--------------|
| `android.view.LayoutInflater` | Android SDK | XML → View | `item_notification.xml` |
| `android.view.ViewGroup` | Android SDK | Parent container | `onCreateViewHolder` |
| `androidx.core.content.ContextCompat` | AndroidX | Color resolution | `brand_primary` color |
| `androidx.recyclerview.widget.DiffUtil` | RecyclerView | Diff algorithm | `ListAdapter` |
| `androidx.recyclerview.widget.ListAdapter` | RecyclerView | Adapter + DiffUtil | Base class |
| `androidx.recyclerview.widget.RecyclerView` | RecyclerView | ViewHolder parent | ViewHolder base |
| `com.syed.classconnect.R` | Project | Resource IDs | `R.color.brand_primary` |
| `com.syed.classconnect.data.model.AppNotification` | Project | Notification data class | Item type |
| `com.syed.classconnect.databinding.ItemNotificationBinding` | ViewBinding | `item_notification.xml` | `tvTitle`, `tvBody`, `tvTime`, `viewUnread`, root |
| `com.syed.classconnect.util.DateUtils.toRelativeTime` | Project | Timestamp → "2h ago" | Notification time |
| `com.syed.classconnect.util.addPressEffect` | Project | Press scale animation | Tactile feedback |
| `com.syed.classconnect.util.hide` | Project | Set GONE | Hide unread dot |
| `com.syed.classconnect.util.show` | Project | Set VISIBLE | Show unread dot |

---

## 🔑 Every Keyword, Annotation & Concept Used

### `b.root.alpha = 1f` vs `0.7f`
Unread: full alpha (1.0) — visually prominent. Read: 70% alpha — de-emphasized to show it has been seen.

### `b.root.translationY = 20f` + animate
Slides each card up 20dp from below while fading in. Combined with `setStartDelay(position * 40L)` creates a cascade effect.

### `b.viewUnread.setBackgroundColor(ContextCompat.getColor(b.root.context, R.color.brand_primary))`
The unread indicator dot is a small view whose background color is set programmatically to the app's primary brand color.

---

## 📝 Full Source Code with Line-by-Line Comments

```kotlin
package com.syed.classconnect.ui.notifications

// (imports as listed above)

class NotificationsAdapter(private val onRead: (AppNotification) -> Unit) :
    ListAdapter<AppNotification, NotificationsAdapter.ViewHolder>(DiffCallback()) {

    inner class ViewHolder(private val b: ItemNotificationBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(item: AppNotification, position: Int) {
            b.tvTitle.text = item.title
            b.tvBody.text = item.body
            b.tvTime.text = item.createdAt.toRelativeTime()

            if (item.isRead) {
                b.viewUnread.hide()        // Hide the blue dot
                b.root.alpha = 0.7f       // Dim read notifications
            } else {
                b.viewUnread.show()
                b.viewUnread.setBackgroundColor(
                    ContextCompat.getColor(b.root.context, R.color.brand_primary))
                b.root.alpha = 1f          // Full brightness for unread
            }

            b.root.addPressEffect()
            b.root.setOnClickListener { onRead(item) }
            // Tapping marks the notification as read.

            // Cascade animation
            b.root.translationY = 20f
            b.root.animate().translationY(0f).alpha(1f)
                .setDuration(300).setStartDelay(position * 40L).start()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(ItemNotificationBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(getItem(position), position)

    class DiffCallback : DiffUtil.ItemCallback<AppNotification>() {
        override fun areItemsTheSame(a: AppNotification, b: AppNotification) = a.id == b.id
        override fun areContentsTheSame(a: AppNotification, b: AppNotification) = a == b
    }
}
```

