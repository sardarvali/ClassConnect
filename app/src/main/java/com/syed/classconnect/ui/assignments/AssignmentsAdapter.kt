package com.syed.classconnect.ui.assignments

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.syed.classconnect.R
import com.syed.classconnect.data.model.Assignment
import com.syed.classconnect.databinding.ItemAssignmentBinding
import com.syed.classconnect.util.DateUtils.daysUntil
import com.syed.classconnect.util.DateUtils.isOverdue
import com.syed.classconnect.util.DateUtils.toDisplayDate
import com.syed.classconnect.util.addPressEffect

class AssignmentsAdapter(
<<<<<<< HEAD
    private val onItemClick: (Assignment) -> Unit,
    private val onLongPress: ((Assignment) -> Unit)? = null
=======
    private val isTeacherMode: Boolean,
    private val onItemClick: (Assignment) -> Unit,
    private val onEdit: ((Assignment) -> Unit)? = null,
    private val onDelete: ((Assignment) -> Unit)? = null
>>>>>>> final
) :
    ListAdapter<Assignment, AssignmentsAdapter.ViewHolder>(DiffCallback()) {

    inner class ViewHolder(private val b: ItemAssignmentBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(item: Assignment, position: Int) {
            b.tvTitle.text = item.title
            b.tvDueDate.text = "Due: ${item.dueDate.toDisplayDate()}"
            b.tvMarks.text = "${item.totalMarks} marks"

            val ctx = b.root.context
            val days = item.dueDate.daysUntil()
            when {
                item.dueDate.isOverdue() -> {
<<<<<<< HEAD
                    b.chipStatus.text = "Overdue"
                    b.chipStatus.chipBackgroundColor = android.content.res.ColorStateList.valueOf(
                        ContextCompat.getColor(ctx, R.color.semantic_error_surface)
                    )
                    b.chipStatus.setTextColor(ContextCompat.getColor(ctx, R.color.semantic_error))
                    b.viewUrgencyBar.setBackgroundColor(ContextCompat.getColor(ctx, R.color.semantic_error))
                }
                days == 0L -> {
                    b.chipStatus.text = "Due Today"
                    b.chipStatus.chipBackgroundColor = android.content.res.ColorStateList.valueOf(
                        ContextCompat.getColor(ctx, R.color.semantic_warning_surface)
                    )
                    b.chipStatus.setTextColor(ContextCompat.getColor(ctx, R.color.semantic_warning))
                    b.viewUrgencyBar.setBackgroundColor(ContextCompat.getColor(ctx, R.color.semantic_warning))
                }
                days <= 3 -> {
                    b.chipStatus.text = "$days days left"
                    b.chipStatus.chipBackgroundColor = android.content.res.ColorStateList.valueOf(
                        ContextCompat.getColor(ctx, R.color.semantic_warning_surface)
                    )
                    b.chipStatus.setTextColor(ContextCompat.getColor(ctx, R.color.semantic_warning))
                    b.viewUrgencyBar.setBackgroundColor(ContextCompat.getColor(ctx, R.color.semantic_warning))
                }
                else -> {
                    b.chipStatus.text = "$days days left"
                    b.chipStatus.chipBackgroundColor = android.content.res.ColorStateList.valueOf(
                        ContextCompat.getColor(ctx, R.color.semantic_success_surface)
                    )
                    b.chipStatus.setTextColor(ContextCompat.getColor(ctx, R.color.semantic_success))
                    b.viewUrgencyBar.setBackgroundColor(ContextCompat.getColor(ctx, R.color.semantic_success))
=======
                    b.statusChip.text = "Overdue"
                    b.statusChip.chipBackgroundColor = android.content.res.ColorStateList.valueOf(
                        ContextCompat.getColor(ctx, R.color.semantic_error_surface)
                    )
                    b.statusChip.setTextColor(ContextCompat.getColor(ctx, R.color.semantic_error))
                    b.urgencyBar.setBackgroundColor(
                        ContextCompat.getColor(
                            ctx,
                            R.color.semantic_error
                        )
                    )
                }

                days == 0L -> {
                    b.statusChip.text = "Due Today"
                    b.statusChip.chipBackgroundColor = android.content.res.ColorStateList.valueOf(
                        ContextCompat.getColor(ctx, R.color.semantic_warning_surface)
                    )
                    b.statusChip.setTextColor(ContextCompat.getColor(ctx, R.color.semantic_warning))
                    b.urgencyBar.setBackgroundColor(
                        ContextCompat.getColor(
                            ctx,
                            R.color.semantic_warning
                        )
                    )
                }

                days <= 3 -> {
                    b.statusChip.text = "$days days left"
                    b.statusChip.chipBackgroundColor = android.content.res.ColorStateList.valueOf(
                        ContextCompat.getColor(ctx, R.color.semantic_warning_surface)
                    )
                    b.statusChip.setTextColor(ContextCompat.getColor(ctx, R.color.semantic_warning))
                    b.urgencyBar.setBackgroundColor(
                        ContextCompat.getColor(
                            ctx,
                            R.color.semantic_warning
                        )
                    )
                }

                else -> {
                    b.statusChip.text = "$days days left"
                    b.statusChip.chipBackgroundColor = android.content.res.ColorStateList.valueOf(
                        ContextCompat.getColor(ctx, R.color.semantic_success_surface)
                    )
                    b.statusChip.setTextColor(ContextCompat.getColor(ctx, R.color.semantic_success))
                    b.urgencyBar.setBackgroundColor(
                        ContextCompat.getColor(
                            ctx,
                            R.color.semantic_success
                        )
                    )
>>>>>>> final
                }
            }

            b.root.addPressEffect()
            b.root.setOnClickListener { onItemClick(item) }
<<<<<<< HEAD
            b.root.setOnLongClickListener {
                onLongPress?.invoke(item)
                onLongPress != null
            }
=======
            b.teacherActions.visibility =
                if (isTeacherMode) android.view.View.VISIBLE else android.view.View.GONE
            b.btnSubmit.visibility =
                if (!isTeacherMode && !item.dueDate.isOverdue()) android.view.View.VISIBLE else android.view.View.GONE
            b.btnEdit.setOnClickListener { onEdit?.invoke(item) }
            b.btnDelete.setOnClickListener { onDelete?.invoke(item) }
>>>>>>> final

            // Stagger entrance
            b.root.alpha = 0f
            b.root.animate().alpha(1f).setDuration(300).setStartDelay(position * 40L).start()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
<<<<<<< HEAD
        ViewHolder(ItemAssignmentBinding.inflate(LayoutInflater.from(parent.context), parent, false))
=======
        ViewHolder(
            ItemAssignmentBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
>>>>>>> final

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(getItem(position), position)

    class DiffCallback : DiffUtil.ItemCallback<Assignment>() {
        override fun areItemsTheSame(a: Assignment, b: Assignment) = a.id == b.id
        override fun areContentsTheSame(a: Assignment, b: Assignment) = a == b
    }
}
