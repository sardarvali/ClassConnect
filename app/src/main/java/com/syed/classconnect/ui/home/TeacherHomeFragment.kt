package com.syed.classconnect.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
<<<<<<< HEAD
=======
import androidx.navigation.NavOptions
>>>>>>> final
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.syed.classconnect.R
import com.syed.classconnect.databinding.FragmentTeacherHomeBinding
import com.syed.classconnect.sensor.SensorHandler
import com.syed.classconnect.util.NetworkUtils
<<<<<<< HEAD
import com.syed.classconnect.util.hide
import com.syed.classconnect.util.loadAvatar
import com.syed.classconnect.util.animateEntrance
=======
import com.syed.classconnect.util.addPressEffect
import com.syed.classconnect.util.animateEntrance
import com.syed.classconnect.util.hide
import com.syed.classconnect.util.loadAvatar
>>>>>>> final
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
<<<<<<< HEAD
    @Inject lateinit var auth: FirebaseAuth
=======
    @Inject
    lateinit var auth: FirebaseAuth
>>>>>>> final

    private lateinit var sensorHandler: SensorHandler
    private lateinit var todayClassesAdapter: TodayClassesAdapter
    private lateinit var newsAdapter: NewsAdapter
<<<<<<< HEAD
=======
    private var lastNewsStatus: String? = null
    private var hasPlayedEntrance = false
    private val homeNavOptions by lazy {
        NavOptions.Builder()
            .setEnterAnim(R.anim.fade_scale_in)
            .setExitAnim(R.anim.fade_scale_out)
            .setPopEnterAnim(R.anim.fade_scale_in)
            .setPopExitAnim(R.anim.fade_scale_out)
            .build()
    }
>>>>>>> final

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

<<<<<<< HEAD
        // Staggered header entrance
        listOf<View>(binding.tvName, binding.ivAvatar)
            .animateEntrance(startDelay = 100L, stepDelay = 60L)
=======
        if (!hasPlayedEntrance) {
            listOf<View>(
                binding.tvName,
                binding.ivAvatar,
                binding.ivNotificationBell,
                binding.btnNewAssignment,
                binding.btnAiLessonPlan
            ).animateEntrance(startDelay = 100L, stepDelay = 60L)
            hasPlayedEntrance = true
        }

        listOf<View>(
            binding.ivNotificationBell,
            binding.btnNewAssignment,
            binding.btnAiLessonPlan
        ).forEach { it.addPressEffect() }
>>>>>>> final

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

<<<<<<< HEAD
=======
        viewModel.newsStatus.observe(viewLifecycleOwner) { status ->
            if (!status.isNullOrBlank() && status != lastNewsStatus) {
                lastNewsStatus = status
                showSnackbar(status)
            }
        }

>>>>>>> final
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
<<<<<<< HEAD
            findNavController().navigate(R.id.notificationsFragment)
        }

        binding.btnNewAssignment.setOnClickListener {
            findNavController().navigate(R.id.classListFragment)
        }

        binding.btnAiLessonPlan.setOnClickListener {
            findNavController().navigate(R.id.lessonPlannerFragment)
=======
            findNavController().navigate(R.id.notificationsFragment, null, homeNavOptions)
        }

        binding.btnNewAssignment.setOnClickListener {
            findNavController().navigate(R.id.classListFragment, null, homeNavOptions)
        }

        binding.btnAiLessonPlan.setOnClickListener {
            findNavController().navigate(R.id.lessonPlannerFragment, null, homeNavOptions)
>>>>>>> final
        }

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.loadCurrentUser()
            viewModel.loadTeacherHomeData(uid)
            binding.swipeRefresh.isRefreshing = false
        }
    }

    private fun setupRecyclerViews() {
<<<<<<< HEAD
        todayClassesAdapter = TodayClassesAdapter { classModel ->
=======
        todayClassesAdapter = TodayClassesAdapter { item, sharedBg, sharedTitle ->
            val classModel = item.classRoom
>>>>>>> final
            com.syed.classconnect.ui.classes.ClassDetailActivity.start(
                requireContext(),
                classModel.id,
                classModel.name,
                classModel.color,
<<<<<<< HEAD
                classModel.classCode
=======
                classModel.classCode,
                sharedBackgroundView = sharedBg,
                sharedTitleView = sharedTitle
>>>>>>> final
            )
        }
        binding.rvTodayClasses.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvTodayClasses.adapter = todayClassesAdapter

        newsAdapter = NewsAdapter { article ->
            startActivity(
<<<<<<< HEAD
                android.content.Intent(requireContext(),
                    com.syed.classconnect.ui.webview.WebViewActivity::class.java).apply {
=======
                android.content.Intent(
                    requireContext(),
                    com.syed.classconnect.ui.webview.WebViewActivity::class.java
                ).apply {
>>>>>>> final
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
