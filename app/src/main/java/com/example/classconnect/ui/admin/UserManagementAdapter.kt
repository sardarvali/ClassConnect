package com.syed.classconnect.ui.admin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.syed.classconnect.R
import com.syed.classconnect.data.model.User
import com.syed.classconnect.databinding.ItemUserManagementBinding
import com.syed.classconnect.util.loadAvatar

class UserManagementAdapter(
    private val onApprove: (User) -> Unit,
    private val onReject: (User) -> Unit,
    private val onItemClick: (User) -> Unit = {}
) : ListAdapter<User, UserManagementAdapter.ViewHolder>(DiffCallback()) {

    // Controls whether Approve/Reject buttons are ever shown.
    // TRUE only on Pending tab (tab 0). FALSE on Teachers/Students tabs.
    var showApproveActions: Boolean = true

    inner class ViewHolder(private val b: ItemUserManagementBinding) :
        RecyclerView.ViewHolder(b.root) {

        fun bind(item: User) {
            b.tvName.text = item.name
            b.tvEmail.text = item.email
            b.tvRole.text = item.role.replaceFirstChar { it.uppercase() }
            b.ivAvatar.loadAvatar(item.photoUrl)
            b.root.setOnClickListener { onItemClick(item) }

            // Only show action buttons on Pending tab AND for genuinely pending users
            if (showApproveActions && !item.isApproved && !item.isRejected) {
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
        ViewHolder(
            ItemUserManagementBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(getItem(position))

    class DiffCallback : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(a: User, b: User) = a.uid == b.uid
        override fun areContentsTheSame(a: User, b: User) = a == b
    }
}
