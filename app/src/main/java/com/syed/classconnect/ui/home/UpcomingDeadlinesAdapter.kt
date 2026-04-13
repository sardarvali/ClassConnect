package com.syed.classconnect.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.syed.classconnect.R
import com.syed.classconnect.data.model.Assignment
import com.syed.classconnect.databinding.ItemDeadlineBinding
import com.syed.classconnect.util.DateUtils.daysUntil
import com.syed.classconnect.util.DateUtils.toDisplayDate
import com.syed.classconnect.util.addPressEffect

/** Upcoming deadline cards for the student home screen. */
class UpcomingDeadlinesAdapter(
    private val onClick: ((Assignment) -> Unit)? = null
) : ListAdapter<Assignment, UpcomingDeadlinesAdapter.VH>(DIFF) {

    inner class VH(private val b: ItemDeadlineBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(a: Assignment) {
            b.tvTitle.text = a.title
            b.tvDueDate.text = a.dueDate.toDisplayDate()
            val days = a.dueDate.daysUntil()
            b.tvDaysLeft.text = when {
                days < 0 -> "Overdue"
                days == 0L -> "Due today"
                days == 1L -> "Due tomorrow"
                else -> "Due in $days days"
            }
            val color = when {
                days < 0 -> b.root.context.getColor(R.color.error)
                days <= 2 -> b.root.context.getColor(R.color.warning)
                else -> b.root.context.getColor(R.color.success)
            }
            b.tvDaysLeft.setTextColor(color)
            b.root.addPressEffect()
            b.root.setOnClickListener { onClick?.invoke(a) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemDeadlineBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(getItem(position))

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<Assignment>() {
            override fun areItemsTheSame(a: Assignment, b: Assignment) = a.id == b.id
            override fun areContentsTheSame(a: Assignment, b: Assignment) = a == b
        }
    }
}

