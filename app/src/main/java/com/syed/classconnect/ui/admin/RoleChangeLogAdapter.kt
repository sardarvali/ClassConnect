package com.syed.classconnect.ui.admin

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.syed.classconnect.data.model.RoleChangeLog
import com.syed.classconnect.databinding.ItemRoleChangeLogBinding
import com.syed.classconnect.util.DateUtils

class RoleChangeLogAdapter :
    ListAdapter<RoleChangeLog, RoleChangeLogAdapter.VH>(DIFF) {

<<<<<<< HEAD
    inner class VH(val binding: ItemRoleChangeLogBinding) :
=======
    class VH(val binding: ItemRoleChangeLogBinding) :
>>>>>>> final
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemRoleChangeLogBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val log = getItem(position)
        with(holder.binding) {
            tvTargetUser.text = log.targetUserName
<<<<<<< HEAD
            tvRoleChange.text = "${log.fromRole.replaceFirstChar { it.uppercase() }} → ${log.toRole.replaceFirstChar { it.uppercase() }}"
=======
            tvRoleChange.text =
                "${log.fromRole.replaceFirstChar { it.uppercase() }} → ${log.toRole.replaceFirstChar { it.uppercase() }}"
>>>>>>> final
            tvChangedBy.text = "By: ${log.changedByAdminName}"
            if (log.reason.isNotEmpty()) {
                tvReason.text = "Reason: ${log.reason}"
                tvReason.isVisible = true
            } else {
                tvReason.isVisible = false
            }
            tvTimestamp.text = DateUtils.formatRelative(log.changedAt)
        }
    }

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<RoleChangeLog>() {
            override fun areItemsTheSame(a: RoleChangeLog, b: RoleChangeLog) = a.id == b.id
            override fun areContentsTheSame(a: RoleChangeLog, b: RoleChangeLog) = a == b
        }
    }
}

