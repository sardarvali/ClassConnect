package com.syed.classconnect.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.syed.classconnect.R
import com.syed.classconnect.databinding.FragmentTeacherHomeBinding
import com.syed.classconnect.sensor.SensorHandler
import com.syed.classconnect.util.NetworkUtils
import com.syed.classconnect.util.hide
import com.syed.classconnect.util.loadAvatar
import com.syed.classconnect.util.animateEntrance
import com.syed.classconnect.util.show
import com.syed.classconnect.util.showSnackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class TeacherHomeFragment : Fragment() {

    private var _binding: FragmentTeacherHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels()
    @Inject lateinit var auth: FirebaseAuth

    private lateinit var sensorHandler: SensorHandler
    private lateinit var todayClassesAdapter: TodayClassesAdapter
    private lateinit var newsAdapter: NewsAdapter
    private var lastNewsStatus: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTeacherHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val uid = auth.currentUser?.uid ?: return

        setupRecyclerViews()
        setupSensor()
        observeConnectivity()

        // Staggered header entrance
        listOf<View>(binding.tvName, binding.ivAvatar)
            .animateEntrance(startDelay = 100L, stepDelay = 60L)

        viewModel.loadCurrentUser()
        viewModel.loadTeacherHomeData(uid)

        viewModel.currentUser.observe(viewLifecycleOwner) { user ->
            user ?: return@observe
            binding.tvName.text = getString(R.string.welcome_name, user.name)
            binding.ivAvatar.loadAvatar(user.photoUrl)
        }

        viewModel.todayClasses.observe(viewLifecycleOwner) { classes ->
            todayClassesAdapter.submitList(classes)
        }

        viewModel.news.observe(viewLifecycleOwner) { articles ->
            newsAdapter.submitList(articles)
        }

        viewModel.newsStatus.observe(viewLifecycleOwner) { status ->
            if (!status.isNullOrBlank() && status != lastNewsStatus) {
                lastNewsStatus = status
                showSnackbar(status)
            }
        }

        viewModel.unreadCount.observe(viewLifecycleOwner) { count ->
            if (count > 0) {
                binding.tvNotificationBadge.show()
                binding.tvNotificationBadge.text = if (count > 99) "99+" else count.toString()
                val shake = android.view.animation.AnimationUtils.loadAnimation(
                    requireContext(), R.anim.shake
                )
                binding.ivNotificationBell.startAnimation(shake)
            } else {
                binding.tvNotificationBadge.hide()
            }
        }

        binding.ivNotificationBell.setOnClickListener {
            findNavController().navigate(R.id.notificationsFragment)
        }

        binding.btnNewAssignment.setOnClickListener {
            findNavController().navigate(R.id.classListFragment)
        }

        binding.btnAiLessonPlan.setOnClickListener {
            findNavController().navigate(R.id.lessonPlannerFragment)
        }

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.loadCurrentUser()
            viewModel.loadTeacherHomeData(uid)
            binding.swipeRefresh.isRefreshing = false
        }
    }

    private fun setupRecyclerViews() {
        todayClassesAdapter = TodayClassesAdapter { classModel ->
            com.syed.classconnect.ui.classes.ClassDetailActivity.start(
                requireContext(),
                classModel.id,
                classModel.name,
                classModel.color,
                classModel.classCode
            )
        }
        binding.rvTodayClasses.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvTodayClasses.adapter = todayClassesAdapter

        newsAdapter = NewsAdapter { article ->
            startActivity(
                android.content.Intent(requireContext(),
                    com.syed.classconnect.ui.webview.WebViewActivity::class.java).apply {
                    putExtra(com.syed.classconnect.util.Constants.EXTRA_URL, article.url)
                    putExtra(com.syed.classconnect.util.Constants.EXTRA_TITLE, article.title)
                }
            )
        }
        binding.rvNews.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvNews.adapter = newsAdapter
    }

    private fun setupSensor() {
        sensorHandler = SensorHandler(
            context = requireContext(),
            onShake = {
                val uid = auth.currentUser?.uid ?: return@SensorHandler
                viewModel.loadTeacherHomeData(uid)
                showSnackbar(getString(R.string.refreshed))
            },
            onFlat = {}
        )
    }

    private fun observeConnectivity() {
        viewLifecycleOwner.lifecycleScope.launch {
            NetworkUtils.observeConnectivity(requireContext()).collectLatest { isOnline ->
                if (isOnline) binding.bannerOffline.hide()
                else binding.bannerOffline.show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        sensorHandler.register()
    }

    override fun onPause() {
        super.onPause()
        sensorHandler.unregister()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
