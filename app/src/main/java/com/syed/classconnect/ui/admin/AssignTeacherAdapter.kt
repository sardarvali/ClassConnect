package com.syed.classconnect.ui.admin

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.syed.classconnect.R
import com.syed.classconnect.data.model.User
import com.syed.classconnect.databinding.ItemAssignTeacherBinding

class AssignTeacherAdapter(
    private val onTeacherSelected: (User) -> Unit
) : ListAdapter<User, AssignTeacherAdapter.VH>(DIFF) {

<<<<<<< HEAD
    inner class VH(val binding: ItemAssignTeacherBinding) : RecyclerView.ViewHolder(binding.root)
=======
    class VH(val binding: ItemAssignTeacherBinding) : RecyclerView.ViewHolder(binding.root)
>>>>>>> final

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemAssignTeacherBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val teacher = getItem(position)
        with(holder.binding) {
            tvTeacherName.text = teacher.name
            tvTeacherEmail.text = teacher.email
            tvClassCount.text = "${teacher.classIds.size} classes"
            Glide.with(root.context)
                .load(teacher.photoUrl.ifEmpty { null })
                .placeholder(R.drawable.ic_profile)
                .circleCrop()
                .into(ivAvatar)
            root.setOnClickListener { onTeacherSelected(teacher) }
        }
    }

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<User>() {
            override fun areItemsTheSame(a: User, b: User) = a.uid == b.uid
            override fun areContentsTheSame(a: User, b: User) = a == b
        }
    }
}
