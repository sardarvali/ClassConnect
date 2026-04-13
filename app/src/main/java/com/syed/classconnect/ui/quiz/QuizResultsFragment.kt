package com.syed.classconnect.ui.quiz

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.firebase.firestore.FirebaseFirestore
import com.syed.classconnect.R
import com.syed.classconnect.data.model.QuizAttempt
import com.syed.classconnect.databinding.FragmentQuizResultsTeacherBinding
import com.syed.classconnect.util.Constants
import com.syed.classconnect.util.NetworkResult
import com.syed.classconnect.util.hide
import com.syed.classconnect.util.show
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlin.math.roundToInt

/**
 * QuizResultsFragment (Teacher) — shows aggregate analytics for a published quiz:
 *  • Average, highest, and lowest scores
 *  • Recycler list of per-student attempts
 *  • Bar chart of score distribution (MPAndroidChart)
 */
@AndroidEntryPoint
class QuizResultsFragment : Fragment() {

    private var _binding: FragmentQuizResultsTeacherBinding? = null
    private val binding get() = _binding!!
    private val viewModel: QuizViewModel by viewModels()

    private lateinit var classId: String
    private lateinit var quizId: String
    private lateinit var attemptsAdapter: QuizAttemptsAdapter

    companion object {
        fun newInstance(classId: String, quizId: String) = QuizResultsFragment().apply {
            arguments = Bundle().apply {
                putString(Constants.EXTRA_CLASS_ID, classId)
                putString(Constants.EXTRA_QUIZ_ID, quizId)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQuizResultsTeacherBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        classId = arguments?.getString(Constants.EXTRA_CLASS_ID) ?: return
        quizId = arguments?.getString(Constants.EXTRA_QUIZ_ID) ?: return

        attemptsAdapter = QuizAttemptsAdapter()
        binding.rvAttempts.layoutManager =
            androidx.recyclerview.widget.LinearLayoutManager(requireContext())
        binding.rvAttempts.adapter = attemptsAdapter

        viewModel.loadAllAttempts(classId, quizId)

        viewModel.allAttempts.observe(viewLifecycleOwner) { result ->
            when (result) {
                is NetworkResult.Loading -> {
                    binding.progressBar.show()
                    binding.layoutStats.hide()
                }

                is NetworkResult.Success -> {
                    binding.progressBar.hide()
                    val attempts = result.data
                    if (attempts.isEmpty()) {
                        binding.layoutEmpty.show()
                        binding.layoutStats.hide()
                    } else {
                        binding.layoutEmpty.hide()
                        binding.layoutStats.show()
                        // Backfill missing student names
                        viewLifecycleOwner.lifecycleScope.launch {
                            val enriched = enrichAttemptsWithNames(attempts)
                            populateStats(enriched)
                            attemptsAdapter.submitList(enriched)
                            populateBarChart(enriched)
                        }
                    }
                }

                is NetworkResult.Error -> {
                    binding.progressBar.hide()
                    binding.layoutEmpty.show()
                }
            }
        }
    }

    /** Fetch student names for attempts that are missing them (old data) */
    private suspend fun enrichAttemptsWithNames(attempts: List<QuizAttempt>): List<QuizAttempt> {
        val firestore = FirebaseFirestore.getInstance()
        return attempts.map { attempt ->
            if (attempt.studentName.isBlank()) {
                try {
                    val userDoc = firestore.collection("users")
                        .document(attempt.studentId).get().await()
                    attempt.copy(
                        studentName = userDoc.getString("name") ?: "Unknown",
                        studentEmail = userDoc.getString("email") ?: ""
                    )
                } catch (_: Exception) {
                    attempt
                }
            } else attempt
        }
    }

    private fun populateStats(attempts: List<QuizAttempt>) {
        if (attempts.isEmpty()) return
        val scores = attempts.map { it.score }
        val avg = scores.average().roundToInt()
        val highest = scores.max()
        val lowest = scores.min()
        val totalMarks = attempts.first().totalMarks

        binding.tvAvgScore.text = "$avg / $totalMarks"
        binding.tvHighestScore.text = "$highest / $totalMarks"
        binding.tvLowestScore.text = "$lowest / $totalMarks"
        binding.tvAttemptCount.text =
            "${attempts.size} attempt${if (attempts.size != 1) "s" else ""}"
    }

    private fun populateBarChart(attempts: List<QuizAttempt>) {
        if (attempts.isEmpty()) return
        val totalMarks = attempts.first().totalMarks

        // Distribute into 5 equal buckets: 0-20%, 21-40%, 41-60%, 61-80%, 81-100%
        val buckets = IntArray(5)
        for (a in attempts) {
            val pct = if (totalMarks == 0) 0 else (a.score * 100 / totalMarks)
            val idx = (pct / 20).coerceAtMost(4)
            buckets[idx]++
        }

        val entries = buckets.mapIndexed { i, count -> BarEntry(i.toFloat(), count.toFloat()) }
        val dataSet = BarDataSet(entries, getString(R.string.quiz_score_distribution)).apply {
            color = resources.getColor(R.color.primary, null)
            valueTextSize = 11f
        }

        binding.barChart.apply {
            data = BarData(dataSet)
            description.isEnabled = false
            legend.isEnabled = true
            setFitBars(true)
            animateY(600)

            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                granularity = 1f
                valueFormatter = IndexAxisValueFormatter(
                    arrayOf("0-20%", "21-40%", "41-60%", "61-80%", "81-100%")
                )
            }
            axisLeft.apply {
                axisMinimum = 0f
                granularity = 1f
            }
            axisRight.isEnabled = false
            invalidate()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

