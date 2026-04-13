package com.syed.classconnect.ui.admin

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.syed.classconnect.data.model.ClassRoom
import com.syed.classconnect.databinding.ItemAdminClassBinding

class AdminClassAdapter(
    private val onAssignTeacher: (ClassRoom) -> Unit
) : ListAdapter<ClassRoom, AdminClassAdapter.VH>(DIFF) {

    class VH(val binding: ItemAdminClassBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemAdminClassBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val c = getItem(position)
        with(holder.binding) {
            tvClassName.text = c.name
            tvSubject.text = c.subject
            tvTeacherName.text = c.teacherName.ifEmpty { "Unassigned" }
            tvStudentCount.text = "${c.studentIds.size} students"

            val isUnassigned = c.teacherId.isEmpty()
            tvUnassignedBadge.isVisible = isUnassigned
            btnAssignTeacher.isVisible = isUnassigned
            btnAssignTeacher.setOnClickListener { onAssignTeacher(c) }

            // Orange left border for unassigned
            val borderColor = if (isUnassigned) {
                root.context.getColor(com.syed.classconnect.R.color.warning)
            } else {
                try {
                    android.graphics.Color.parseColor(c.color)
                } catch (_: Exception) {
                    root.context.getColor(com.syed.classconnect.R.color.primary)
                }
            }
            viewColorBar.setBackgroundColor(borderColor)
        }
    }

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<ClassRoom>() {
            override fun areItemsTheSame(a: ClassRoom, b: ClassRoom) = a.id == b.id
            override fun areContentsTheSame(a: ClassRoom, b: ClassRoom) = a == b
        }
    }
}

