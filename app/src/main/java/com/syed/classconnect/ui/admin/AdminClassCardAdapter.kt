package com.syed.classconnect.ui.admin

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.syed.classconnect.data.model.ClassRoom
import com.syed.classconnect.databinding.ItemAdminClassCardBinding

class AdminClassCardAdapter(
    private val onAssignTeacher: (ClassRoom) -> Unit
) : ListAdapter<ClassRoom, AdminClassCardAdapter.VH>(DIFF) {

<<<<<<< HEAD
    inner class VH(val binding: ItemAdminClassCardBinding) : RecyclerView.ViewHolder(binding.root)
=======
    class VH(val binding: ItemAdminClassCardBinding) : RecyclerView.ViewHolder(binding.root)
>>>>>>> final

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemAdminClassCardBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val classRoom = getItem(position)
        with(holder.binding) {
            tvClassName.text = classRoom.name
            val isUnassigned = classRoom.teacherId.isEmpty()
            if (isUnassigned) {
                tvTeacherName.text = "No Teacher Assigned"
                chipUnassigned.isVisible = true
                btnAssignTeacher.isVisible = true
<<<<<<< HEAD
                try { viewColorBar.setBackgroundColor(Color.parseColor("#E65100")) }
                catch (_: Exception) {}
=======
                try {
                    viewColorBar.setBackgroundColor(Color.parseColor("#E65100"))
                } catch (_: Exception) {
                }
>>>>>>> final
            } else {
                tvTeacherName.text = "Teacher: ${classRoom.teacherName}"
                chipUnassigned.isVisible = false
                btnAssignTeacher.isVisible = false
<<<<<<< HEAD
                try { viewColorBar.setBackgroundColor(Color.parseColor(classRoom.color)) }
                catch (_: Exception) {}
=======
                try {
                    viewColorBar.setBackgroundColor(Color.parseColor(classRoom.color))
                } catch (_: Exception) {
                }
>>>>>>> final
            }
            btnAssignTeacher.setOnClickListener { onAssignTeacher(classRoom) }
        }
    }

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<ClassRoom>() {
            override fun areItemsTheSame(a: ClassRoom, b: ClassRoom) = a.id == b.id
            override fun areContentsTheSame(a: ClassRoom, b: ClassRoom) = a == b
        }
    }
}

