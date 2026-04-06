# AttendanceHistoryAdapter.kt — RecyclerView adapter displaying per-session attendance records

---

## 📁 File Location
`app/src/main/java/com/syed/classconnect/ui/attendance/AttendanceHistoryAdapter.kt`

---

## 🎯 What This File Does
`AttendanceHistoryAdapter` renders a list of `AttendanceRecord` objects in the attendance history section of `AttendanceFragment`. Each row shows the session date, the count of present students (with a checkmark), and the count of absent students (with an X). It uses a basic `RecyclerView.Adapter` (not `ListAdapter`) with a `submitList()` pattern that clears and refills `items` then calls `notifyDataSetChanged()`. Without it, the attendance history section has no renderer.

---

## 📦 Every Import — Explained

| Import | From Library | What It Is | Why Used Here |
|--------|-------------|-----------|--------------|
| `android.view.LayoutInflater` | Android SDK | XML → View | `item_attendance_history.xml` |
| `android.view.ViewGroup` | Android SDK | Parent container | `onCreateViewHolder` |
| `androidx.recyclerview.widget.RecyclerView` | RecyclerView | Adapter base + ViewHolder | Base class for this adapter |
| `com.syed.classconnect.data.model.AttendanceRecord` | Project | Session attendance data | Item type |
| `com.syed.classconnect.databinding.ItemAttendanceHistoryBinding` | ViewBinding | `item_attendance_history.xml` | `tvDate`, `tvPresent`, `tvAbsent` |

---

## 🔑 Every Keyword, Annotation & Concept Used

### Basic `RecyclerView.Adapter` vs `ListAdapter`
This adapter uses the base `RecyclerView.Adapter` rather than `ListAdapter`. The trade-off:
- No automatic diff animation — `notifyDataSetChanged()` redraws the entire list.
- Simpler implementation for a list that doesn't change item-by-item (it's always a full reload).
- A `ListAdapter` with `DiffUtil` would give better UX with item animations.

### `notifyDataSetChanged()`
Tells RecyclerView: "Redraw every item." Inefficient for large lists but acceptable for small attendance history lists.

### `"✓ ${item.present.size} present"` / `"✗ ${item.absent.size} absent"`
String templates using Unicode tick (✓) and cross (✗) for visual clarity.

---

## 🏗️ Class Structure
`class AttendanceHistoryAdapter : RecyclerView.Adapter<ViewHolder>()` — manual list management with `submitList()` helper.

---

## 📋 Properties

| Property | Type | Modifier | What It Holds | Why It Exists |
|----------|------|----------|--------------|--------------|
| `items` | `MutableList<AttendanceRecord>` | `private val` | Current attendance records | Backing data for RecyclerView |

---

## ⚙️ Functions

### `submitList(list: List<AttendanceRecord>)`
Clears `items`, adds all from `list`, calls `notifyDataSetChanged()`.

### `bind(item: AttendanceRecord)`
Sets `tvDate`, `tvPresent` ("✓ N present"), `tvAbsent` ("✗ N absent").

---

## 📝 Full Source Code with Line-by-Line Comments

```kotlin
package com.syed.classconnect.ui.attendance

// (imports as listed above)

class AttendanceHistoryAdapter : RecyclerView.Adapter<AttendanceHistoryAdapter.ViewHolder>() {

    private val items = mutableListOf<AttendanceRecord>()
    // Internal list — not exposed publicly.

    fun submitList(list: List<AttendanceRecord>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
        // Full redraw — acceptable for small lists.
    }

    inner class ViewHolder(private val b: ItemAttendanceHistoryBinding) :
        RecyclerView.ViewHolder(b.root) {
        fun bind(item: AttendanceRecord) {
            b.tvDate.text = item.date
            b.tvPresent.text = "✓ ${item.present.size} present"
            b.tvAbsent.text = "✗ ${item.absent.size} absent"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(ItemAttendanceHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(items[position])
    override fun getItemCount() = items.size
}
```

