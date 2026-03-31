package com.syed.classconnect.ui.quiz

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.syed.classconnect.data.model.QuizQuestion
import com.syed.classconnect.databinding.ItemEditableQuestionBinding

class EditableQuestionsAdapter(
    private val onEdit: (Int, QuizQuestion) -> Unit,
    private val onDelete: (Int, QuizQuestion) -> Unit
) : ListAdapter<QuizQuestion, EditableQuestionsAdapter.QuestionViewHolder>(DiffCallback()) {

    inner class QuestionViewHolder(
        private val binding: ItemEditableQuestionBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: QuizQuestion, position: Int) {
            binding.tvQuestionTitle.text = "Q${position + 1}. ${item.question}"
            binding.tvQuestionMeta.text = "${item.options.size} options • Correct: ${('A'.code + item.correctIndex).toChar()} • ${item.marks} mark(s)"
            binding.btnEditQuestion.setOnClickListener {
                val currentPos = bindingAdapterPosition
                if (currentPos != RecyclerView.NO_POSITION) {
                    onEdit(currentPos, item)
                }
            }
            binding.btnDeleteQuestion.setOnClickListener { 
                val currentPos = bindingAdapterPosition
                if (currentPos != RecyclerView.NO_POSITION) {
                    onDelete(currentPos, item)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionViewHolder {
        val binding = ItemEditableQuestionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return QuestionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: QuestionViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }

    private class DiffCallback : DiffUtil.ItemCallback<QuizQuestion>() {
        override fun areItemsTheSame(oldItem: QuizQuestion, newItem: QuizQuestion): Boolean =
            oldItem == newItem

        override fun areContentsTheSame(oldItem: QuizQuestion, newItem: QuizQuestion): Boolean =
            oldItem == newItem
    }
}
