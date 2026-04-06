# RoleChangeLogAdapter.kt — RecyclerView adapter displaying admin role change audit log entries

---

## 📁 File Location
`app/src/main/java/com/syed/classconnect/ui/admin/RoleChangeLogAdapter.kt`

---

## 🎯 What This File Does
`RoleChangeLogAdapter` renders audit log entries in `RoleChangeHistoryFragment`. Each row shows: the user whose role was changed, the role transition arrow ("Teacher → Student"), the admin who made the change, an optional reason (hidden if empty), and the relative timestamp. It is a read-only display adapter with no click handlers. Without it, the role change history screen has no content renderer.

---

## 📦 Every Import — Explained

| Import | From Library | What It Is | Why Used Here |
|--------|-------------|-----------|--------------|
| `android.view.LayoutInflater` | Android SDK | XML → View | `item_role_change_log.xml` |
| `android.view.ViewGroup` | Android SDK | Parent | `onCreateViewHolder` |
| `androidx.core.view.isVisible` | AndroidX | Visibility shorthand | Show/hide reason text |
| `androidx.recyclerview.widget.DiffUtil` | RecyclerView | Diff | `ListAdapter` |
| `androidx.recyclerview.widget.ListAdapter` | RecyclerView | Adapter + DiffUtil | Base class |
| `androidx.recyclerview.widget.RecyclerView` | RecyclerView | ViewHolder | ViewHolder base |
| `com.syed.classconnect.data.model.RoleChangeLog` | Project | Log entry data class | Item type |
| `com.syed.classconnect.databinding.ItemRoleChangeLogBinding` | ViewBinding | `item_role_change_log.xml` | All item views |
| `com.syed.classconnect.util.DateUtils` | Project | Date formatting | `formatRelative()` |

---

## 🔑 Every Keyword, Annotation & Concept Used

### `log.fromRole.replaceFirstChar { it.uppercase() }`
Capitalizes the first character: "teacher" → "Teacher". The role change display reads "Teacher → Student".

### `log.reason.isNotEmpty()` → `tvReason.isVisible = true`
The reason field is optional. If not provided (empty string), the `tvReason` view is hidden via `isVisible = false`.

### `DateUtils.formatRelative(log.changedAt)`
Formats a Firestore `Timestamp` as a relative string ("2 days ago", "just now", etc.).

---

## 📝 Full Source Code with Line-by-Line Comments

```kotlin
package com.syed.classconnect.ui.admin

// (imports as listed above)

class RoleChangeLogAdapter :
    ListAdapter<RoleChangeLog, RoleChangeLogAdapter.VH>(DIFF) {

    inner class VH(val binding: ItemRoleChangeLogBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemRoleChangeLogBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val log = getItem(position)
        with(holder.binding) {
            tvTargetUser.text = log.targetUserName
            // The user whose role was changed.

            tvRoleChange.text = "${log.fromRole.replaceFirstChar { it.uppercase() }} → ${log.toRole.replaceFirstChar { it.uppercase() }}"
            // "Teacher → Student" — the arrow shows the direction of change.

            tvChangedBy.text = "By: ${log.changedByAdminName}"
            // Which admin performed the role change.

            if (log.reason.isNotEmpty()) {
                tvReason.text = "Reason: ${log.reason}"
                tvReason.isVisible = true
            } else {
                tvReason.isVisible = false
                // Hide the reason row entirely if none provided.
            }

            tvTimestamp.text = DateUtils.formatRelative(log.changedAt)
            // "2 days ago", "just now", etc.
        }
    }

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<RoleChangeLog>() {
            override fun areItemsTheSame(a: RoleChangeLog, b: RoleChangeLog) = a.id == b.id
            override fun areContentsTheSame(a: RoleChangeLog, b: RoleChangeLog) = a == b
        }
    }
}
```

