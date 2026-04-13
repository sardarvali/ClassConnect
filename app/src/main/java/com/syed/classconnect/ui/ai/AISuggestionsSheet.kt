package com.syed.classconnect.ui.ai

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.syed.classconnect.databinding.BottomSheetAiSuggestionsBinding
import com.syed.classconnect.util.dismissWithPremiumAnimation
import dagger.hilt.android.AndroidEntryPoint

/**
 * Shows 2–3 AI-generated variants for an assignment or quiz.
 * onPick(title, description) is called when the user taps a card.
 */
@AndroidEntryPoint
class AISuggestionsSheet : BottomSheetDialogFragment() {

    private var _b: BottomSheetAiSuggestionsBinding? = null
    private val b get() = _b!!
    private val viewModel: AIViewModel by activityViewModels()

    private var variants: List<Pair<String, String>> = emptyList()
    private var quizVariants: List<AIQuizSuggestion> = emptyList()

    companion object {
        private const val ARG_TYPE = "type"     // "assignment" | "quiz"
        private const val ARG_CONTEXT = "context"  // "Title: X\nDescription: Y"

        fun newInstance(type: String, context: String) = AISuggestionsSheet().apply {
            arguments = Bundle().apply {
                putString(ARG_TYPE, type)
                putString(ARG_CONTEXT, context)
            }
        }
    }

    /** Called when user picks a variant. Dismiss already handled. */
    var onPick: ((title: String, description: String) -> Unit)? = null
    var onPickQuiz: ((AIQuizSuggestion) -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _b = BottomSheetAiSuggestionsBinding.inflate(inflater, container, false)
        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val type = arguments?.getString(ARG_TYPE) ?: "assignment"
        val context = arguments?.getString(ARG_CONTEXT) ?: ""

        b.tvSheetTitle.text = if (type == "quiz") "Quiz Suggestions" else "Assignment Suggestions"
        b.tvSheetSubtitle.text = if (type == "quiz") {
            "Tap a card to load a full quiz with ready-made questions"
        } else {
            "Tap a card to use that version"
        }

        showLoading(true)
        loadSuggestions(type, context)

        b.btnRetry.setOnClickListener {
            showLoading(true)
            b.layoutError.visibility = View.GONE
            loadSuggestions(type, context)
        }
    }

    private fun loadSuggestions(type: String, context: String) {
        if (type == "quiz") {
            viewModel.generateQuizSuggestionVariants(context) { list ->
                quizVariants = list
                if (list.isEmpty()) showError("Could not generate quiz options. Try again.")
                else {
                    showLoading(false)
                    bindQuizCards(list)
                }
            }
        } else {
            viewModel.generateContentVariants(type, context) { list ->
                variants = list
                if (list.isEmpty()) showError("Could not generate suggestions. Try again.")
                else {
                    showLoading(false)
                    bindCards(list)
                }
            }
        }
    }

    private fun bindCards(list: List<Pair<String, String>>) {
        b.layoutError.visibility = View.GONE
        b.layoutCards.visibility = View.VISIBLE

        val cards = listOf(b.card1, b.card2, b.card3)
        val titles = listOf(b.tvTitle1, b.tvTitle2, b.tvTitle3)
        val descs = listOf(b.tvDesc1, b.tvDesc2, b.tvDesc3)

        cards.forEachIndexed { i, card ->
            if (i < list.size) {
                card.visibility = View.VISIBLE
                titles[i].text = list[i].first
                descs[i].text = list[i].second
                card.setOnClickListener {
                    onPick?.invoke(list[i].first, list[i].second)
                    dismissWithPremiumAnimation()
                }
            } else {
                card.visibility = View.GONE
            }
        }
    }

    private fun bindQuizCards(list: List<AIQuizSuggestion>) {
        b.layoutError.visibility = View.GONE
        b.layoutCards.visibility = View.VISIBLE

        val cards = listOf(b.card1, b.card2, b.card3)
        val titles = listOf(b.tvTitle1, b.tvTitle2, b.tvTitle3)
        val descs = listOf(b.tvDesc1, b.tvDesc2, b.tvDesc3)

        cards.forEachIndexed { i, card ->
            if (i < list.size) {
                val suggestion = list[i]
                card.visibility = View.VISIBLE
                titles[i].text = suggestion.title
                descs[i].text = buildString {
                    append("${suggestion.questions.size} questions")
                    if (suggestion.description.isNotBlank()) {
                        append(" • ")
                        append(suggestion.description)
                    }
                }
                card.setOnClickListener {
                    onPickQuiz?.invoke(suggestion)
                    dismissWithPremiumAnimation()
                }
            } else {
                card.visibility = View.GONE
            }
        }
    }

    private fun showLoading(show: Boolean) {
        b.layoutLoading.visibility = if (show) View.VISIBLE else View.GONE
        if (show) b.layoutCards.visibility = View.GONE
    }

    private fun showError(msg: String) {
        showLoading(false)
        b.layoutError.visibility = View.VISIBLE
        b.tvError.text = msg
    }

    override fun onDestroyView() {
        super.onDestroyView(); _b = null
    }
}

