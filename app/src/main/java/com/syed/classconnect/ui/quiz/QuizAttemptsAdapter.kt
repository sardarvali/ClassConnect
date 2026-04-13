package com.syed.classconnect.ui.quiz

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.syed.classconnect.data.model.QuizAttempt
import com.syed.classconnect.databinding.ItemQuizAttemptBinding
import com.syed.classconnect.util.DateUtils.toDisplayDateTime
import kotlin.math.roundToInt

/**
 * Adapter for the teacher's quiz results screen — one row per student attempt.
 */
class QuizAttemptsAdapter :
    ListAdapter<QuizAttempt, QuizAttemptsAdapter.ViewHolder>(DIFF) {

    class ViewHolder(private val b: ItemQuizAttemptBinding) :
        RecyclerView.ViewHolder(b.root) {

        fun bind(attempt: QuizAttempt) {
            b.tvStudentId.text = attempt.studentName.ifBlank { attempt.studentId.take(8) + "…" }
            b.tvScore.text = "${attempt.score} / ${attempt.totalMarks}"
            val pct = if (attempt.totalMarks == 0) 0
            else (attempt.score * 100.0 / attempt.totalMarks).roundToInt()
            b.tvPercentage.text = "$pct%"
            b.tvSubmittedAt.text = attempt.submittedAt.toDisplayDateTime()

            // Colour-code percentage badge
            val colorRes = when {
                pct >= 80 -> com.syed.classconnect.R.color.semantic_success
                pct >= 50 -> com.syed.classconnect.R.color.semantic_warning
                else -> com.syed.classconnect.R.color.semantic_error
            }
            b.tvPercentage.setTextColor(
                b.root.context.resources.getColor(colorRes, null)
            )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemQuizAttemptBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(getItem(position))

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<QuizAttempt>() {
            override fun areItemsTheSame(a: QuizAttempt, b: QuizAttempt) =
                a.studentId == b.studentId

            override fun areContentsTheSame(a: QuizAttempt, b: QuizAttempt) = a == b
        }
    }
}

