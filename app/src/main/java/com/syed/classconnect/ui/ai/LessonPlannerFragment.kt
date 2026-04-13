package com.syed.classconnect.ui.ai

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
<<<<<<< HEAD
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
=======
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.doOnLayout
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.firebase.auth.FirebaseAuth
>>>>>>> final
import com.syed.classconnect.R
import com.syed.classconnect.databinding.FragmentLessonPlannerBinding
import com.syed.classconnect.util.hide
import com.syed.classconnect.util.show
import com.syed.classconnect.util.showSnackbar
import dagger.hilt.android.AndroidEntryPoint
<<<<<<< HEAD
=======
import javax.inject.Inject
>>>>>>> final

@AndroidEntryPoint
class LessonPlannerFragment : Fragment() {

    private var _binding: FragmentLessonPlannerBinding? = null
    private val binding get() = _binding!!
<<<<<<< HEAD
    private val viewModel: AIViewModel by viewModels()
    private var lastGeneratedPlan = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
=======
    private val viewModel: AIViewModel by activityViewModels()
    @Inject
    lateinit var auth: FirebaseAuth
    private var lastGeneratedPlan = ""
    private var lastTopic = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
>>>>>>> final
        _binding = FragmentLessonPlannerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

<<<<<<< HEAD
        binding.btnGenerate.setOnClickListener { generatePlan() }
        binding.btnRegenerate.setOnClickListener { generatePlan() }
        binding.btnCopy.setOnClickListener {
            val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipboard.setPrimaryClip(ClipData.newPlainText("Lesson Plan", lastGeneratedPlan))
            showSnackbar(getString(R.string.copied))
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            if (loading) { binding.progressBar.show(); binding.btnGenerate.isEnabled = false }
            else { binding.progressBar.hide(); binding.btnGenerate.isEnabled = true }
=======
        val fabBottomMargin =
            (binding.fabUsePlan.layoutParams as ViewGroup.MarginLayoutParams).bottomMargin
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val statusTop = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
            val navBottom = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom
            val bottomNavOverlay = getBottomNavOverlayHeight()
            val bottomInset = maxOf(navBottom, bottomNavOverlay)

            v.updatePadding(top = statusTop)

            // Adjust scroll view padding so content isn't hidden behind bottom nav
            binding.scrollView.updatePadding(bottom = bottomInset + 88)

            // Adjust FAB margin so it floats above the bottom navigation
            binding.fabUsePlan.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                bottomMargin = fabBottomMargin + bottomInset
            }
            insets
        }
        binding.root.doOnLayout { ViewCompat.requestApplyInsets(binding.root) }

        val uid = auth.currentUser?.uid
        if (uid != null) {
            viewModel.loadUserRole(uid)
            viewModel.loadClassesForCurrentUser()
        }

        binding.btnGenerate.setOnClickListener { generatePlan() }
        binding.btnRegenerate.setOnClickListener { generatePlan() }
        binding.btnCopy.setOnClickListener {
            val clipboard =
                requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipboard.setPrimaryClip(ClipData.newPlainText("Lesson Plan", lastGeneratedPlan))
            showSnackbar(getString(R.string.copied))
        }
        binding.fabUsePlan.setOnClickListener {
            if (lastGeneratedPlan.isBlank()) return@setOnClickListener
            AIActionsSheet.newInstance(
                lastGeneratedPlan,
                lastTopic,
                AIActionsSheet.MODE_LESSON_PLAN
            )
                .show(childFragmentManager, "ai_actions")
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            if (loading) {
                binding.progressBar.show(); binding.btnGenerate.isEnabled = false
            } else {
                binding.progressBar.hide(); binding.btnGenerate.isEnabled = true
            }
>>>>>>> final
        }

        viewModel.error.observe(viewLifecycleOwner) { err -> err?.let { showSnackbar(it) } }
    }

<<<<<<< HEAD
=======
    private fun getBottomNavOverlayHeight(): Int {
        val navContainer = activity?.findViewById<View>(R.id.bottom_nav_container) ?: return 0
        if (navContainer.visibility != View.VISIBLE) return 0
        val lp = navContainer.layoutParams as? ViewGroup.MarginLayoutParams
        return navContainer.height + (lp?.bottomMargin ?: 0)
    }

>>>>>>> final
    private fun generatePlan() {
        val subject = binding.etSubject.text.toString().trim()
        val topic = binding.etTopic.text.toString().trim()
        val grade = binding.etGrade.text.toString().trim()
        val duration = binding.etDuration.text.toString().trim()
        val objectives = binding.etObjectives.text.toString().trim()

        if (subject.isEmpty() || topic.isEmpty()) {
            showSnackbar("Subject and Topic are required")
            return
        }

<<<<<<< HEAD
        binding.layoutResult.hide()
=======
        lastTopic = "$subject — $topic"
        binding.layoutResult.hide()
        binding.fabUsePlan.hide()
>>>>>>> final
        viewModel.generateLessonPlan(subject, topic, grade, duration, objectives) { plan ->
            lastGeneratedPlan = plan
            binding.tvPlan.text = plan
            binding.layoutResult.show()
            binding.btnRegenerate.show()
            binding.btnCopy.show()
<<<<<<< HEAD
        }
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}

=======
            binding.fabUsePlan.show()

            // Auto show the actions sheet once plan is generated
            AIActionsSheet.newInstance(plan, lastTopic, AIActionsSheet.MODE_LESSON_PLAN)
                .show(childFragmentManager, "ai_actions")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView(); _binding = null
    }
}
>>>>>>> final
