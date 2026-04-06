# UpcomingDeadlinesAdapter.kt — RecyclerView adapter for upcoming assignment deadlines with color-coded urgency indicators

---

## 📁 File Location
`app/src/main/java/com/syed/classconnect/ui/home/UpcomingDeadlinesAdapter.kt`

---

## 🎯 What This File Does
`UpcomingDeadlinesAdapter` displays a list of upcoming `Assignment` deadlines on the student home screen. Each item shows the assignment title, due date (formatted), and a "days left" label that changes color based on urgency: red for overdue, amber/warning for ≤2 days, green for ample time. Tapping triggers the `onClick` callback. Without this adapter, students would have no quick glance at approaching deadlines from their home screen.

---

## 📦 Every Import — Explained

| Import | From Library | What It Is | Why Used Here |
|--------|-------------|-----------|--------------|
| `android.view.LayoutInflater` | Android SDK | Inflates XML layouts into View objects | Creates `item_deadline.xml` views |
| `android.view.ViewGroup` | Android SDK | Layout container | Required by `onCreateViewHolder` |
| `androidx.recyclerview.widget.DiffUtil` | RecyclerView | List diff algorithm | Powers `ListAdapter` updates |
| `androidx.recyclerview.widget.ListAdapter` | RecyclerView | Adapter with DiffUtil | Base class |
| `androidx.recyclerview.widget.RecyclerView` | RecyclerView | ViewHolder parent | ViewHolder type |
| `com.syed.classconnect.R` | Project | Resource IDs | Color resources |
| `com.syed.classconnect.data.model.Assignment` | Project | Assignment data class | List item type |
| `com.syed.classconnect.databinding.ItemDeadlineBinding` | ViewBinding | `item_deadline.xml` typed references | `tvTitle`, `tvDueDate`, `tvDaysLeft` |
| `com.syed.classconnect.util.DateUtils.daysUntil` | Project | Extension on Timestamp | Days between now and due date |
| `com.syed.classconnect.util.DateUtils.toDisplayDate` | Project | Extension on Timestamp | "Mar 10, 2026" formatted string |

---

## 🔑 Every Keyword, Annotation & Concept Used

### `import com.syed.classconnect.util.DateUtils.daysUntil`
Static import of an extension function. Allows calling `a.dueDate.daysUntil()` directly rather than `DateUtils.daysUntil(a.dueDate)`. `daysUntil()` returns the number of whole days from now to the timestamp (negative if in the past).

### `b.root.context.getColor(R.color.error)`
`getColor(colorResId)` requires API 23+. Returns an `Int` representing the color. Used to set `tvDaysLeft.setTextColor(color)` dynamically based on urgency.

### Color-coding logic:
```kotlin
val color = when {
    days < 0  -> error   // Red: overdue
    days <= 2 -> warning // Amber: 0-2 days
    else      -> success // Green: 3+ days
}
```

---

## 🏗️ Class Structure
`UpcomingDeadlinesAdapter(onClick) : ListAdapter<Assignment, VH>(DIFF)` — simple adapter with one ViewHolder type.

---

## 📋 Properties

| Property | Type | Modifier | What It Holds | Why It Exists |
|----------|------|----------|--------------|--------------|
| `onClick` | `((Assignment) -> Unit)?` | Constructor param | Click callback | Navigate to assignment detail |
| `DIFF` | `DiffUtil.ItemCallback<Assignment>` | `companion object` | Item equality | Powers ListAdapter |

---

## ⚙️ Functions

### `bind(a: Assignment)`
**Purpose:** Populates one deadline row.
**Step by step:**
1. Sets `tvTitle` = assignment title.
2. Sets `tvDueDate` = formatted due date using `toDisplayDate()`.
3. Calls `daysUntil()` to get days remaining.
4. Sets `tvDaysLeft` text: "Overdue" / "Due today" / "Due tomorrow" / "Due in N days".
5. Sets text color based on urgency.
6. Sets click listener.

---

## 📝 Full Source Code with Line-by-Line Comments

```kotlin
package com.syed.classconnect.ui.home

// (imports as listed above)

/** Upcoming deadline cards for the student home screen. */
class UpcomingDeadlinesAdapter(
    private val onClick: ((Assignment) -> Unit)? = null
) : ListAdapter<Assignment, UpcomingDeadlinesAdapter.VH>(DIFF) {

    inner class VH(private val b: ItemDeadlineBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(a: Assignment) {
            b.tvTitle.text   = a.title
            b.tvDueDate.text = a.dueDate.toDisplayDate()
            // Extension: converts Firebase Timestamp → "Mar 10, 2026"

            val days = a.dueDate.daysUntil()
            // Extension: returns days between now and dueDate (negative if past).

            b.tvDaysLeft.text = when {
                days < 0  -> "Overdue"
                days == 0L -> "Due today"
                days == 1L -> "Due tomorrow"
                else       -> "Due in $days days"
            }

            val color = when {
                days < 0  -> b.root.context.getColor(R.color.error)
                days <= 2 -> b.root.context.getColor(R.color.warning)
                else      -> b.root.context.getColor(R.color.success)
            }
            b.tvDaysLeft.setTextColor(color)
            b.root.setOnClickListener { onClick?.invoke(a) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemDeadlineBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(getItem(position))

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<Assignment>() {
            override fun areItemsTheSame(a: Assignment, b: Assignment) = a.id == b.id
            override fun areContentsTheSame(a: Assignment, b: Assignment) = a == b
        }
    }
}
```

