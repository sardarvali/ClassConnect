package com.syed.classconnect.ui.classes

import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.syed.classconnect.R
import com.syed.classconnect.data.model.ClassRoom
import com.syed.classconnect.databinding.ItemClassCardBinding
import com.syed.classconnect.util.addPressEffect

class ClassAdapter(private val onClick: (ClassRoom) -> Unit) :
    ListAdapter<ClassRoom, ClassAdapter.ViewHolder>(DiffCallback()) {

    private val gradients = listOf(
        Pair(R.color.class_1_start, R.color.class_1_end),
        Pair(R.color.class_2_start, R.color.class_2_end),
        Pair(R.color.class_3_start, R.color.class_3_end),
        Pair(R.color.class_4_start, R.color.class_4_end),
        Pair(R.color.class_5_start, R.color.class_5_end),
        Pair(R.color.class_6_start, R.color.class_6_end),
        Pair(R.color.class_7_start, R.color.class_7_end),
        Pair(R.color.class_8_start, R.color.class_8_end),
    )

    inner class ViewHolder(private val binding: ItemClassCardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ClassRoom, position: Int) {
            binding.tvClassName.text = item.name
            binding.tvSubject.text = item.subject
            binding.tvTeacher.text = item.teacherName
            binding.tvStudentCount.text = "${item.studentIds.size} students"

            // Apply gradient accent bar color
            val (startColor, endColor) = gradients[position % gradients.size]
            val gradient = GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                intArrayOf(
                    ContextCompat.getColor(binding.root.context, startColor),
                    ContextCompat.getColor(binding.root.context, endColor)
                )
            )
            binding.viewColorBar.background = gradient

            binding.root.addPressEffect()
            binding.root.setOnClickListener { onClick(item) }

            // Stagger entrance animation
            binding.root.alpha = 0f
            binding.root.animate()
                .alpha(1f)
                .setDuration(300)
                .setStartDelay(position * 50L)
                .start()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(ItemClassCardBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(getItem(position), position)

    class DiffCallback : DiffUtil.ItemCallback<ClassRoom>() {
        override fun areItemsTheSame(a: ClassRoom, b: ClassRoom) = a.id == b.id
        override fun areContentsTheSame(a: ClassRoom, b: ClassRoom) = a == b
    }
}
