# UserManagementAdapter.kt — RecyclerView adapter for admin user management with conditional approve/reject buttons

---

## 📁 File Location
`app/src/main/java/com/syed/classconnect/ui/admin/UserManagementAdapter.kt`

---

## 🎯 What This File Does
`UserManagementAdapter` displays users in the admin user management screen. It has three modes controlled by `showApproveActions`: (1) Pending tab — shows Approve and Reject buttons for unapproved/non-rejected users; (2) Teachers/Students tabs — buttons are hidden, user rows are informational only. Each row shows avatar, name, email, and capitalized role. Tapping the row triggers `onItemClick`. The `showApproveActions` property is set per-tab by `UserManagementFragment`. Without this adapter, the admin user management screen has no content renderer.

---

## 📦 Every Import — Explained

| Import | From Library | What It Is | Why Used Here |
|--------|-------------|-----------|--------------|
| `android.view.LayoutInflater` | Android SDK | XML → View | `item_user_management.xml` |
| `android.view.View` | Android SDK | View visibility constants | `View.VISIBLE`, `View.GONE` |
| `android.view.ViewGroup` | Android SDK | Parent container | `onCreateViewHolder` |
| `androidx.recyclerview.widget.DiffUtil` | RecyclerView | Diff | `ListAdapter` |
| `androidx.recyclerview.widget.ListAdapter` | RecyclerView | Adapter + DiffUtil | Base class |
| `androidx.recyclerview.widget.RecyclerView` | RecyclerView | ViewHolder | ViewHolder base |
| `com.syed.classconnect.R` | Project | String resources | `R.string.approve` button text |
| `com.syed.classconnect.data.model.User` | Project | User data class | Item type |
| `com.syed.classconnect.databinding.ItemUserManagementBinding` | ViewBinding | `item_user_management.xml` | `tvName`, `tvEmail`, `tvRole`, `ivAvatar`, `btnApprove`, `btnReject` |
| `com.syed.classconnect.util.loadAvatar` | Project | Glide avatar extension | Circular photo loading |

---

## 🔑 Every Keyword, Annotation & Concept Used

### `var showApproveActions: Boolean = true`
Mutable public property. `UserManagementFragment` sets this to `false` on the Teachers and Students tabs so approve/reject buttons are never shown for already-approved users.

### `item.role.replaceFirstChar { it.uppercase() }`
Capitalizes the first character of the role string: "teacher" → "Teacher", "admin" → "Admin".

### `!item.isApproved && !item.isRejected`
Only shows approve/reject buttons if the user is truly pending (not yet approved AND not rejected). Avoids showing the buttons for a user who was previously rejected.

---

## 📝 Full Source Code with Line-by-Line Comments

```kotlin
package com.syed.classconnect.ui.admin

// (imports as listed above)

class UserManagementAdapter(
    private val onApprove: (User) -> Unit,
    private val onReject: (User) -> Unit,
    private val onItemClick: (User) -> Unit = {}
    // Default empty lambda — row click is optional.
) : ListAdapter<User, UserManagementAdapter.ViewHolder>(DiffCallback()) {

    var showApproveActions: Boolean = true
    // Set to false on Teachers/Students tabs to hide approve/reject buttons.

    inner class ViewHolder(private val b: ItemUserManagementBinding) :
        RecyclerView.ViewHolder(b.root) {
        fun bind(item: User) {
            b.tvName.text = item.name
            b.tvEmail.text = item.email
            b.tvRole.text = item.role.replaceFirstChar { it.uppercase() }
            b.ivAvatar.loadAvatar(item.photoUrl)
            b.root.setOnClickListener { onItemClick(item) }

            if (showApproveActions && !item.isApproved && !item.isRejected) {
                // Show buttons: pending tab AND user hasn't been acted on yet.
                b.btnApprove.visibility = View.VISIBLE
                b.btnReject.visibility  = View.VISIBLE
                b.btnApprove.isEnabled  = true
                b.btnApprove.setText(R.string.approve)
                b.btnApprove.setOnClickListener { onApprove(item) }
                b.btnReject.setOnClickListener  { onReject(item) }
            } else {
                b.btnApprove.visibility = View.GONE
                b.btnReject.visibility  = View.GONE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(ItemUserManagementBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(getItem(position))

    class DiffCallback : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(a: User, b: User) = a.uid == b.uid
        override fun areContentsTheSame(a: User, b: User) = a == b
    }
}
```

