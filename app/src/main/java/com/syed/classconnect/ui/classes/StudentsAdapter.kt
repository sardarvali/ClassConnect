package com.syed.classconnect.ui.classes

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.syed.classconnect.data.model.User
import com.syed.classconnect.databinding.ItemStudentRowBinding
import com.syed.classconnect.util.loadAvatar

class StudentsAdapter :
    ListAdapter<User, StudentsAdapter.ViewHolder>(DiffCallback()) {

<<<<<<< HEAD
    inner class ViewHolder(private val b: ItemStudentRowBinding) :
=======
    class ViewHolder(private val b: ItemStudentRowBinding) :
>>>>>>> final
        RecyclerView.ViewHolder(b.root) {
        fun bind(item: User) {
            b.tvName.text = item.name
            b.tvEmail.text = item.email
            b.ivAvatar.loadAvatar(item.photoUrl)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
<<<<<<< HEAD
        ViewHolder(ItemStudentRowBinding.inflate(LayoutInflater.from(parent.context), parent, false))
=======
        ViewHolder(
            ItemStudentRowBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
>>>>>>> final

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(getItem(position))

    class DiffCallback : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(a: User, b: User) = a.uid == b.uid
        override fun areContentsTheSame(a: User, b: User) = a == b
    }
}

