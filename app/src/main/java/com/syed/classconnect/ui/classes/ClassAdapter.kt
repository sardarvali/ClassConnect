package com.syed.classconnect.ui.classes

import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
<<<<<<< HEAD
import android.view.ViewGroup
import androidx.core.content.ContextCompat
=======
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
>>>>>>> final
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.syed.classconnect.R
import com.syed.classconnect.data.model.ClassRoom
import com.syed.classconnect.databinding.ItemClassCardBinding
<<<<<<< HEAD
import com.syed.classconnect.util.addPressEffect

class ClassAdapter(private val onClick: (ClassRoom) -> Unit) :
=======
import com.syed.classconnect.util.Constants
import com.syed.classconnect.util.addPressEffect

class ClassAdapter(private val onClick: (ClassRoom, View, View) -> Unit) :
>>>>>>> final
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

<<<<<<< HEAD
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
=======
            try {
                val colorInt =
                    android.graphics.Color.parseColor(if (item.color.isNotEmpty()) item.color else "#1565C0")
                val gradient = GradientDrawable(
                    GradientDrawable.Orientation.TOP_BOTTOM,
                    intArrayOf(colorInt, colorInt)
                )
                binding.viewColorBar.background = gradient
            } catch (e: Exception) {
                val colorIndex = item.id.hashCode().rem(gradients.size)
                    .let { if (it < 0) it + gradients.size else it }
                val (startColor, endColor) = gradients[colorIndex]
                val gradient = GradientDrawable(
                    GradientDrawable.Orientation.TOP_BOTTOM,
                    intArrayOf(
                        ContextCompat.getColor(binding.root.context, startColor),
                        ContextCompat.getColor(binding.root.context, endColor)
                    )
                )
                binding.viewColorBar.background = gradient
            }

            ViewCompat.setTransitionName(
                binding.viewColorBar,
                Constants.TRANSITION_CLASS_HEADER_PREFIX + item.id
            )
            ViewCompat.setTransitionName(
                binding.tvClassName,
                Constants.TRANSITION_CLASS_TITLE_PREFIX + item.id
            )

            binding.root.addPressEffect()
            binding.root.setOnClickListener {
                onClick(item, binding.viewColorBar, binding.tvClassName)
            }
>>>>>>> final

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
