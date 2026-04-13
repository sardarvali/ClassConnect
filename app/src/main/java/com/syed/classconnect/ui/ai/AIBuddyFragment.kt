<<<<<<< HEAD
package com.syed.classconnect.ui.ai

import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.syed.classconnect.R
import com.syed.classconnect.databinding.FragmentAiBuddyBinding
import com.syed.classconnect.util.NetworkResult
import com.syed.classconnect.util.hide
import com.syed.classconnect.util.show
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class AIBuddyFragment : Fragment() {

    private var _binding: FragmentAiBuddyBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AIViewModel by viewModels()
    private lateinit var adapter: AIChatAdapter

    private val speechLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val text = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull()
        if (!text.isNullOrEmpty()) {
            binding.etInput.setText(text)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAiBuddyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = AIChatAdapter()
        binding.rvChat.layoutManager = LinearLayoutManager(requireContext()).apply { stackFromEnd = true }
        binding.rvChat.adapter = adapter

        setupSuggestions()

        binding.btnSend.setOnClickListener {
            val input = binding.etInput.text.toString().trim()
            if (input.isNotEmpty()) {
                binding.etInput.setText("")
                binding.layoutSuggestions.hide()
                viewModel.sendMessage(input)
            }
        }

        binding.btnVoice.setOnClickListener {
            val intent = android.content.Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            }
            speechLauncher.launch(intent)
        }

        binding.btnClear.setOnClickListener {
            viewModel.clearConversation()
            adapter.submitList(emptyList())
            binding.layoutSuggestions.show()
        }

        viewModel.messages.observe(viewLifecycleOwner) { messages ->
            adapter.submitList(messages.toList())
            if (messages.isNotEmpty()) binding.rvChat.scrollToPosition(messages.size - 1)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
            binding.btnSend.isEnabled = !loading
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error ?: return@observe
            adapter.addErrorMessage(error)
        }
    }

    private fun setupSuggestions() {
        val suggestions = listOf(
            "Explain this concept",
            "Create practice questions",
            "Summarize my notes",
            "Help me study for exam"
        )
        val chips = listOf(binding.chip1, binding.chip2, binding.chip3, binding.chip4)
        suggestions.forEachIndexed { i, text ->
            chips.getOrNull(i)?.apply {
                this.text = text
                setOnClickListener { binding.etInput.setText(text) }
            }
        }
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}

=======
﻿package com.syed.classconnect.ui.ai

import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.doOnLayout
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.syed.classconnect.R
import com.syed.classconnect.databinding.FragmentAiBuddyBinding
import com.syed.classconnect.util.Constants
import com.syed.classconnect.util.hide
import com.syed.classconnect.util.show
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class AIBuddyFragment : Fragment() {
    private var _binding: FragmentAiBuddyBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AIViewModel by activityViewModels()
    private lateinit var adapter: AIChatAdapter
    @Inject
    lateinit var auth: FirebaseAuth
    private var lastAutoPromptedAiText: String? = null
    private val speechLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val text =
                result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull()
            if (!text.isNullOrEmpty()) binding.etInput.setText(text)
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAiBuddyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val dp12 = (12 * resources.displayMetrics.density).toInt()
        val fabBottomMargin =
            (binding.fabUseResponse.layoutParams as ViewGroup.MarginLayoutParams).bottomMargin
        val progressBottomMargin =
            (binding.progressBar.layoutParams as ViewGroup.MarginLayoutParams).bottomMargin
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val statusTop = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
            val imeBottom = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom
            val navBottom = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom
            val bottomNavOverlay = getBottomNavOverlayHeight()
            val bottomInset = if (imeBottom > 0) imeBottom else maxOf(navBottom, bottomNavOverlay)
            v.updatePadding(top = statusTop)
            binding.rvChat.updatePadding(bottom = bottomInset + binding.inputCard.height + binding.layoutSuggestions.height + 24)
            binding.inputCard.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                bottomMargin = dp12 + bottomInset
            }
            binding.fabUseResponse.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                bottomMargin = fabBottomMargin + bottomInset
            }
            binding.progressBar.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                bottomMargin = progressBottomMargin + bottomInset
            }
            insets
        }
        binding.root.doOnLayout { ViewCompat.requestApplyInsets(binding.root) }
        val uid = auth.currentUser?.uid ?: return
        viewModel.loadUserRole(uid)
        viewModel.loadClassesForCurrentUser()
        adapter = AIChatAdapter()
        binding.rvChat.layoutManager =
            LinearLayoutManager(requireContext()).apply { stackFromEnd = true }
        binding.rvChat.adapter = adapter
        setupSuggestions()
        binding.btnSend.setOnClickListener {
            val input = binding.etInput.text.toString().trim()
            if (input.isNotEmpty()) {
                binding.etInput.setText(""); binding.layoutSuggestions.hide(); viewModel.sendMessage(
                    input
                )
            }
        }
        binding.btnVoice.setOnClickListener {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(
                    RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                )
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            }
            speechLauncher.launch(intent)
        }
        binding.btnClear.setOnClickListener {
            viewModel.clearConversation(); adapter.submitList(
            emptyList()
        ); binding.layoutSuggestions.show(); binding.fabUseResponse.hide()
        }
        binding.fabUseResponse.setOnClickListener {
            val lastAiText = viewModel.messages.value?.lastOrNull { !it.isUser }?.text
                ?: return@setOnClickListener
            val isPlan = isLikelyLessonPlan(lastAiText)
            val mode = if (isPlan) AIActionsSheet.MODE_LESSON_PLAN else AIActionsSheet.MODE_GENERIC
            val topic = if (isPlan) "AI Lesson Plan" else "AI Buddy Response"
            AIActionsSheet.newInstance(lastAiText, topic, mode)
                .show(childFragmentManager, "ai_actions")
        }
        viewModel.messages.observe(viewLifecycleOwner) { messages ->
            adapter.submitList(messages.toList())
            if (messages.isNotEmpty()) binding.rvChat.scrollToPosition(messages.size - 1)
            val isTeacher =
                viewModel.userRole.value == Constants.ROLE_TEACHER || viewModel.userRole.value == Constants.ROLE_ADMIN
            val hasAiResponse = messages.any { !it.isUser }
            val latestAiText = messages.lastOrNull { !it.isUser }?.text
            val shouldShowPlanActions = latestAiText != null && isLikelyLessonPlan(latestAiText)

            if (hasAiResponse && isTeacher) binding.fabUseResponse.show() else binding.fabUseResponse.hide()

            if (isTeacher && shouldShowPlanActions && latestAiText != lastAutoPromptedAiText) {
                lastAutoPromptedAiText = latestAiText
                AIActionsSheet.newInstance(
                    latestAiText,
                    "AI Lesson Plan",
                    AIActionsSheet.MODE_LESSON_PLAN
                )
                    .show(childFragmentManager, "ai_actions")
            }
        }
        viewModel.userRole.observe(viewLifecycleOwner) { role ->
            val isTeacher = role == Constants.ROLE_TEACHER || role == Constants.ROLE_ADMIN
            val latestAiText = viewModel.messages.value?.lastOrNull { !it.isUser }?.text
            val hasAiResponse = latestAiText != null

            if (hasAiResponse && isTeacher) binding.fabUseResponse.show() else binding.fabUseResponse.hide()
        }
        viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
            binding.btnSend.isEnabled = !loading
        }
        viewModel.error.observe(viewLifecycleOwner) { error ->
            error ?: return@observe; adapter.addErrorMessage(error)
        }
    }

    private fun setupSuggestions() {
        val suggestions = listOf(
            "Explain this concept",
            "Create practice questions",
            "Summarize my notes",
            "Help me study for exam"
        )
        val chips = listOf(binding.chip1, binding.chip2, binding.chip3, binding.chip4)
        suggestions.forEachIndexed { i, text ->
            chips.getOrNull(i)
                ?.apply { this.text = text; setOnClickListener { binding.etInput.setText(text) } }
        }
    }

    private fun getBottomNavOverlayHeight(): Int {
        val navContainer = activity?.findViewById<View>(R.id.bottom_nav_container) ?: return 0
        if (navContainer.visibility != View.VISIBLE) return 0
        val lp = navContainer.layoutParams as? ViewGroup.MarginLayoutParams
        return navContainer.height + (lp?.bottomMargin ?: 0)
    }

    private fun isLikelyLessonPlan(text: String): Boolean {
        val normalized = text.lowercase(Locale.getDefault())
        return normalized.contains("lesson plan") ||
                (normalized.contains("learning objectives") && normalized.contains("assessment")) ||
                normalized.contains("main activity")
    }

    override fun onDestroyView() {
        super.onDestroyView(); _binding = null
    }
}
>>>>>>> final
