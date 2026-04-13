package com.syed.classconnect.ui.quiz

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
<<<<<<< HEAD
import com.google.android.material.snackbar.Snackbar
=======
>>>>>>> final
import com.google.firebase.auth.FirebaseAuth
import com.syed.classconnect.databinding.FragmentQuizListBinding
import com.syed.classconnect.util.Constants
import com.syed.classconnect.util.NetworkResult
import com.syed.classconnect.util.hide
import com.syed.classconnect.util.show
<<<<<<< HEAD
=======
import com.syed.classconnect.util.showSnackbar
>>>>>>> final
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class QuizListFragment : Fragment() {

    private var _binding: FragmentQuizListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: QuizViewModel by viewModels()
<<<<<<< HEAD
    @Inject lateinit var auth: FirebaseAuth
=======
    @Inject
    lateinit var auth: FirebaseAuth
>>>>>>> final
    private lateinit var adapter: QuizAdapter
    private lateinit var classId: String

    companion object {
        fun newInstance(classId: String) = QuizListFragment().apply {
            arguments = Bundle().apply { putString(Constants.EXTRA_CLASS_ID, classId) }
        }
    }

<<<<<<< HEAD
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
=======
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
>>>>>>> final
        _binding = FragmentQuizListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        classId = arguments?.getString(Constants.EXTRA_CLASS_ID) ?: return
        val uid = auth.currentUser?.uid ?: return

        viewModel.loadUserRole(uid)

        adapter = QuizAdapter(
<<<<<<< HEAD
            onClick = { quiz ->
                if (viewModel.userRole.value == Constants.ROLE_TEACHER ||
                    viewModel.userRole.value == Constants.ROLE_ADMIN) {
=======
            isTeacherMode = false,
            onClick = { quiz ->
                if (viewModel.userRole.value == Constants.ROLE_TEACHER ||
                    viewModel.userRole.value == Constants.ROLE_ADMIN
                ) {
>>>>>>> final
                    val resultsFragment = QuizResultsFragment.newInstance(classId, quiz.id)
                    parentFragmentManager.beginTransaction()
                        .replace(android.R.id.content, resultsFragment)
                        .addToBackStack(null)
                        .commit()
                } else {
                    handleStudentQuizClick(quiz)
                }
            },
<<<<<<< HEAD
            onLongPress = { quiz ->
                val role = viewModel.userRole.value
                if (role == Constants.ROLE_TEACHER || role == Constants.ROLE_ADMIN) {
                    showQuizOptionsDialog(quiz)
                }
            }
=======
            onEdit = { quiz ->
                CreateQuizFragment.newInstanceForEdit(classId, quiz)
                    .show(parentFragmentManager, "edit_quiz")
            },
            onPublishToggle = { quiz ->
                viewModel.publishQuiz(classId, quiz.id, !quiz.published)
            },
            onDelete = { quiz -> showDeleteQuizConfirmDialog(quiz) }
>>>>>>> final
        )
        binding.rvQuizzes.layoutManager = LinearLayoutManager(requireContext())
        binding.rvQuizzes.adapter = adapter

        // Load quizzes after role is known (for draft filtering)
        viewModel.userRole.observe(viewLifecycleOwner) { role ->
<<<<<<< HEAD
            viewModel.loadQuizzes(classId)
=======
            adapter = QuizAdapter(
                isTeacherMode = role == Constants.ROLE_TEACHER || role == Constants.ROLE_ADMIN,
                onClick = { quiz ->
                    if (role == Constants.ROLE_TEACHER || role == Constants.ROLE_ADMIN) {
                        val resultsFragment = QuizResultsFragment.newInstance(classId, quiz.id)
                        parentFragmentManager.beginTransaction()
                            .replace(android.R.id.content, resultsFragment)
                            .addToBackStack(null)
                            .commit()
                    } else {
                        handleStudentQuizClick(quiz)
                    }
                },
                onEdit = { quiz ->
                    CreateQuizFragment.newInstanceForEdit(classId, quiz)
                        .show(parentFragmentManager, "edit_quiz")
                },
                onLongClick = if (role == Constants.ROLE_TEACHER || role == Constants.ROLE_ADMIN) {
                    { quiz -> showQuizOptionsDialog(quiz) }
                } else null,
                onPublishToggle = { quiz ->
                    viewModel.publishQuiz(
                        classId,
                        quiz.id,
                        !quiz.published
                    )
                },
                onDelete = { quiz -> showDeleteQuizConfirmDialog(quiz) }
            )
            binding.rvQuizzes.adapter = adapter
            viewModel.loadQuizzes(classId, role)   // 🔥 role passed explicitly — no timing bug
>>>>>>> final

            if (role == Constants.ROLE_TEACHER || role == Constants.ROLE_ADMIN) {
                binding.fab.show()
                binding.fab.setOnClickListener {
<<<<<<< HEAD
                    CreateQuizFragment.newInstance(classId).show(parentFragmentManager, "create_quiz")
                }
            } else {
                binding.fab.hide()
=======
                    CreateQuizFragment.newInstance(classId)
                        .show(parentFragmentManager, "create_quiz")
                }
            } else {
                binding.fab.hide()
                viewModel.loadAttemptedQuizIds(classId, uid)
            }
        }

        viewModel.attemptedQuizIds.observe(viewLifecycleOwner) { ids ->
            if (::adapter.isInitialized) {
                adapter.attemptedQuizIds = ids
>>>>>>> final
            }
        }

        viewModel.quizzes.observe(viewLifecycleOwner) { result ->
            when (result) {
<<<<<<< HEAD
                is NetworkResult.Loading -> { binding.progressBar.show(); binding.layoutEmpty.hide() }
                is NetworkResult.Success -> {
                    binding.progressBar.hide()
                    if (result.data.isEmpty()) { binding.layoutEmpty.show(); binding.rvQuizzes.hide() }
                    else { binding.layoutEmpty.hide(); binding.rvQuizzes.show(); adapter.submitList(result.data) }
                }
=======
                is NetworkResult.Loading -> {
                    binding.progressBar.show(); binding.layoutEmpty.hide()
                }

                is NetworkResult.Success -> {
                    binding.progressBar.hide()
                    val role = viewModel.userRole.value ?: Constants.ROLE_STUDENT
                    val visibleQuizzes =
                        if (role == Constants.ROLE_TEACHER || role == Constants.ROLE_ADMIN) {
                            result.data
                        } else {
                            result.data.filter { it.published }
                        }
                    if (visibleQuizzes.isEmpty()) {
                        binding.layoutEmpty.show(); binding.rvQuizzes.hide()
                    } else {
                        binding.layoutEmpty.hide(); binding.rvQuizzes.show()
                        adapter.submitList(visibleQuizzes) {
                            binding.rvQuizzes.scheduleLayoutAnimation()
                        }
                    }
                }

>>>>>>> final
                is NetworkResult.Error -> binding.progressBar.hide()
            }
        }

        viewModel.deleteResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is NetworkResult.Success -> {
<<<<<<< HEAD
                    Snackbar.make(binding.root, "Quiz deleted", Snackbar.LENGTH_SHORT).show()
                }
                is NetworkResult.Error -> {
                    Snackbar.make(binding.root, "Delete failed: ${result.message}", Snackbar.LENGTH_SHORT).show()
                }
                is NetworkResult.Loading -> { /* no-op */ }
=======
                    showSnackbar("Quiz deleted")
                }

                is NetworkResult.Error -> {
                    showSnackbar("Delete failed: ${result.message}")
                }

                is NetworkResult.Loading -> { /* no-op */
                }
>>>>>>> final
            }
        }
    }

    private fun showQuizOptionsDialog(quiz: com.syed.classconnect.data.model.Quiz) {
        val options = mutableListOf<String>()
<<<<<<< HEAD
        options.add(if (quiz.isPublished) "Unpublish" else "Publish")
        options.add("Delete")
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(quiz.title)
            .setItems(options.toTypedArray()) { _, which ->
                when (options[which]) {
                    "Publish" -> viewModel.publishQuiz(classId, quiz.id, true)
                    "Unpublish" -> viewModel.publishQuiz(classId, quiz.id, false)
                    "Delete" -> showDeleteQuizConfirmDialog(quiz)
=======
        options.add("✏️ Edit")
        options.add(if (quiz.published) "⬇️ Unpublish" else "📢 Publish")
        options.add("🗑️ Delete")
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(quiz.title)
            .setItems(options.toTypedArray()) { _, which ->
                when (which) {
                    0 -> CreateQuizFragment.newInstanceForEdit(classId, quiz)
                        .show(parentFragmentManager, "edit_quiz")

                    1 -> viewModel.publishQuiz(classId, quiz.id, !quiz.published)
                    2 -> showDeleteQuizConfirmDialog(quiz)
>>>>>>> final
                }
            }.show()
    }

    private fun showDeleteQuizConfirmDialog(quiz: com.syed.classconnect.data.model.Quiz) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Delete Quiz")
            .setMessage("Delete \"${quiz.title}\"? All student attempts will be deleted. This cannot be undone.")
            .setPositiveButton("Delete") { _, _ ->
                viewModel.deleteQuiz(classId, quiz.id)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    /** Checks if student already attempted, then navigates to result or attempt */
    private fun handleStudentQuizClick(quiz: com.syed.classconnect.data.model.Quiz) {
<<<<<<< HEAD
=======
        if (!quiz.published) {
            showSnackbar("This quiz is still in draft")
            return
        }
        val now = com.google.firebase.Timestamp.now()
        if (quiz.startTime != null && now.seconds < quiz.startTime.seconds) {
            showSnackbar("This quiz is scheduled and not open yet")
            return
        }
        if (quiz.endTime != null && now.seconds > quiz.endTime.seconds) {
            showSnackbar("This quiz is no longer available")
            return
        }
>>>>>>> final
        val studentId = auth.currentUser?.uid ?: return
        viewModel.hasStudentAttempted(classId, quiz.id, studentId) { alreadyAttempted ->
            if (alreadyAttempted) {
                // Show result instead of re-attempting
                val fragment = QuizResultFragment.newInstance(
                    classId, quiz.id, studentId, -1, quiz.totalMarks
                )
                fragment.show(parentFragmentManager, "quiz_result")
            } else {
<<<<<<< HEAD
                val intent = android.content.Intent(requireContext(), QuizAttemptActivity::class.java).apply {
                    putExtra(Constants.EXTRA_CLASS_ID, classId)
                    putExtra(Constants.EXTRA_QUIZ_ID, quiz.id)
                }
=======
                val intent =
                    android.content.Intent(requireContext(), QuizAttemptActivity::class.java)
                        .apply {
                            putExtra(Constants.EXTRA_CLASS_ID, classId)
                            putExtra(Constants.EXTRA_QUIZ_ID, quiz.id)
                        }
>>>>>>> final
                startActivity(intent)
            }
        }
    }

<<<<<<< HEAD
    override fun onDestroyView() { super.onDestroyView(); _binding = null }
=======
    override fun onDestroyView() {
        super.onDestroyView(); _binding = null
    }
>>>>>>> final
}

