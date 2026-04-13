package com.syed.classconnect.ui.home

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
<<<<<<< HEAD
import com.syed.classconnect.data.model.ClassRoom
import com.syed.classconnect.databinding.ItemTodayClassBinding
import com.syed.classconnect.util.addPressEffect
import java.util.Calendar

/** Horizontal card adapter for today's timetable on the home screen. */
class TodayClassesAdapter(
    private val onClick: ((ClassRoom) -> Unit)? = null
) : ListAdapter<ClassRoom, TodayClassesAdapter.VH>(DIFF) {
=======
import com.syed.classconnect.databinding.ItemTodayClassBinding
import com.syed.classconnect.util.Constants
import com.syed.classconnect.util.addPressEffect

/** Horizontal card adapter for today's timetable on the home screen. */
class TodayClassesAdapter(
    private val onClick: ((TodayClassSessionItem, View, View) -> Unit)? = null
) : ListAdapter<TodayClassSessionItem, TodayClassesAdapter.VH>(DIFF) {
>>>>>>> final

    private val gradients = listOf(
        Pair(R.color.class_1_start, R.color.class_1_end),
        Pair(R.color.class_2_start, R.color.class_2_end),
        Pair(R.color.class_3_start, R.color.class_3_end),
        Pair(R.color.class_4_start, R.color.class_4_end),
        Pair(R.color.class_5_start, R.color.class_5_end),
        Pair(R.color.class_6_start, R.color.class_6_end),
        Pair(R.color.class_7_start, R.color.class_7_end),
<<<<<<< HEAD
        Pair(R.color.class_8_start, R.color.class_8_end),
    )

    inner class VH(private val b: ItemTodayClassBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(cls: ClassRoom, position: Int) {
            b.tvClassName.text = cls.name
            b.tvSubject.text = cls.subject
            // Show today's scheduled time
            val dayIndex = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
            val today = when (dayIndex) {
                Calendar.SUNDAY    -> "Sunday"
                Calendar.MONDAY    -> "Monday"
                Calendar.TUESDAY   -> "Tuesday"
                Calendar.WEDNESDAY -> "Wednesday"
                Calendar.THURSDAY  -> "Thursday"
                Calendar.FRIDAY    -> "Friday"
                Calendar.SATURDAY  -> "Saturday"
                else -> ""
            }
            b.tvTime.text = cls.schedule.entries
                .firstOrNull { it.key.trim().equals(today, ignoreCase = true) }
                ?.value ?: ""

            // Apply gradient background
=======
        Pair(R.color.class_8_start, R.color.class_8_end)
    )

    inner class VH(private val b: ItemTodayClassBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(item: TodayClassSessionItem, position: Int) {
            val cls = item.classRoom
            b.tvClassName.text = cls.name
            b.tvSubject.text = cls.subject
            b.tvTime.text = b.root.context.getString(R.string.today_time_format, item.slot)

>>>>>>> final
            val (startColor, endColor) = gradients[position % gradients.size]
            val gradient = GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                intArrayOf(
                    ContextCompat.getColor(b.root.context, startColor),
                    ContextCompat.getColor(b.root.context, endColor)
                )
            ).apply {
                cornerRadius = b.root.resources.getDimension(R.dimen.card_corner_radius)
            }
            b.viewGradientBg.background = gradient

<<<<<<< HEAD
            b.root.addPressEffect()
            b.root.setOnClickListener { onClick?.invoke(cls) }

            // Stagger entrance
=======
            ViewCompat.setTransitionName(
                b.viewGradientBg,
                Constants.TRANSITION_CLASS_HEADER_PREFIX + cls.id
            )
            ViewCompat.setTransitionName(
                b.tvClassName,
                Constants.TRANSITION_CLASS_TITLE_PREFIX + cls.id
            )

            b.root.addPressEffect()
            b.root.setOnClickListener { onClick?.invoke(item, b.viewGradientBg, b.tvClassName) }

>>>>>>> final
            b.root.alpha = 0f
            b.root.animate().alpha(1f).setDuration(300).setStartDelay(position * 60L).start()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemTodayClassBinding.inflate(LayoutInflater.from(parent.context), parent, false))

<<<<<<< HEAD
    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(getItem(position), position)

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<ClassRoom>() {
            override fun areItemsTheSame(a: ClassRoom, b: ClassRoom) = a.id == b.id
            override fun areContentsTheSame(a: ClassRoom, b: ClassRoom) = a == b
=======
    override fun onBindViewHolder(holder: VH, position: Int) =
        holder.bind(getItem(position), position)

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<TodayClassSessionItem>() {
            override fun areItemsTheSame(a: TodayClassSessionItem, b: TodayClassSessionItem) =
                a.classRoom.id == b.classRoom.id && a.slot == b.slot

            override fun areContentsTheSame(a: TodayClassSessionItem, b: TodayClassSessionItem) = a == b
>>>>>>> final
        }
    }
}
