package com.syed.classconnect.ui.quiz

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.syed.classconnect.R
import com.syed.classconnect.data.model.Quiz
import com.syed.classconnect.databinding.ItemQuizBinding
import com.syed.classconnect.util.addPressEffect
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class QuizAdapter(
    private val isTeacherMode: Boolean,
    private val onClick: (Quiz) -> Unit,
    private val onLongClick: ((Quiz) -> Unit)? = null,
    private val onEdit: ((Quiz) -> Unit)? = null,
    private val onPublishToggle: ((Quiz) -> Unit)? = null,
    private val onDelete: ((Quiz) -> Unit)? = null
) : ListAdapter<Quiz, QuizAdapter.ViewHolder>(DiffCallback()) {

    var attemptedQuizIds: Set<String> = emptySet()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    private val dateFormatter = SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault())

    inner class ViewHolder(private val b: ItemQuizBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(item: Quiz) {
            b.tvTitle.text = item.title
            b.tvDescription.text = item.description.ifBlank { "No description" }
            b.tvDuration.text = "⏱ ${item.durationMinutes} min"
            b.tvMarks.text = "📊 ${item.totalMarks} marks  •  ${item.questions.size} questions"
            b.tvSchedule.text = buildScheduleText(item)

            val ctx = b.root.context
            if (item.published) {
                b.chipStatus.text = "Published"
                b.chipStatus.chipBackgroundColor = ColorStateList.valueOf(
                    ContextCompat.getColor(ctx, R.color.semantic_success_surface)
                )
                b.chipStatus.setTextColor(ContextCompat.getColor(ctx, R.color.semantic_success))
            } else {
                b.chipStatus.text = "Draft"
                b.chipStatus.chipBackgroundColor = ColorStateList.valueOf(
                    ContextCompat.getColor(ctx, R.color.semantic_warning_surface)
                )
                b.chipStatus.setTextColor(ContextCompat.getColor(ctx, R.color.semantic_warning))
            }

            b.root.addPressEffect()
            b.root.setOnClickListener { onClick(item) }
            b.root.setOnLongClickListener {
                onLongClick?.invoke(item)
                onLongClick != null
            }
            b.layoutTeacherActions.visibility =
                if (isTeacherMode) android.view.View.VISIBLE else android.view.View.GONE
            b.btnStudentAction.visibility =
                if (!isTeacherMode && item.published) android.view.View.VISIBLE else android.view.View.GONE

            if (!isTeacherMode && item.published) {
                if (attemptedQuizIds.contains(item.id)) {
                    b.btnStudentAction.text = "View Result"
                    b.btnStudentAction.backgroundTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(ctx, R.color.brand_accent_dim)
                    )
                } else {
                    b.btnStudentAction.text = "Start Quiz"
                    b.btnStudentAction.backgroundTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(ctx, R.color.brand_primary)
                    )
                }
            }

            b.btnStudentAction.setOnClickListener { onClick(item) }

            b.btnEdit.setOnClickListener { onEdit?.invoke(item) }
            b.btnPublishToggle.text = if (item.published) "Unpublish" else "Publish"
            b.btnPublishToggle.setOnClickListener { onPublishToggle?.invoke(item) }
            b.btnDelete.setOnClickListener { onDelete?.invoke(item) }
        }

        private fun buildScheduleText(item: Quiz): String {
            val start = item.startTime?.toDate()
            val end = item.endTime?.toDate()
            val now = Date()
            return when {
                start != null && end != null && now.before(start) ->
                    "Starts ${dateFormatter.format(start)} • Ends ${dateFormatter.format(end)}"

                start != null && end != null && now.after(end) ->
                    "Closed ${dateFormatter.format(end)}"

                start != null && end != null ->
                    "Open now • Ends ${dateFormatter.format(end)}"

                start != null -> "Starts ${dateFormatter.format(start)}"
                end != null -> "Ends ${dateFormatter.format(end)}"
                else -> "Available after publishing"
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(ItemQuizBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(getItem(position))

    class DiffCallback : DiffUtil.ItemCallback<Quiz>() {
        override fun areItemsTheSame(a: Quiz, b: Quiz) = a.id == b.id
        override fun areContentsTheSame(a: Quiz, b: Quiz) = a == b
    }
}
