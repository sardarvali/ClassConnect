package com.syed.classconnect.ui.home

import android.content.Context
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
import com.syed.classconnect.databinding.FragmentStudentHomeBinding
import com.syed.classconnect.sensor.SensorHandler
import com.syed.classconnect.util.NetworkUtils
import com.syed.classconnect.util.animateEntrance
import com.syed.classconnect.util.hide
import com.syed.classconnect.util.loadAvatar
import com.syed.classconnect.util.shakeAnimation
import com.syed.classconnect.util.show
import com.syed.classconnect.util.showSnackbar
import androidx.core.content.ContextCompat
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class StudentHomeFragment : Fragment() {

    private var _binding: FragmentStudentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels()
    @Inject lateinit var auth: FirebaseAuth

    private lateinit var sensorHandler: SensorHandler
    private lateinit var todayClassesAdapter: TodayClassesAdapter
    private lateinit var deadlinesAdapter: UpcomingDeadlinesAdapter
    private lateinit var announcementsAdapter: RecentAnnouncementsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStudentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val uid = auth.currentUser?.uid ?: return

        setupRecyclerViews()
        setupSensor()
        observeConnectivity()
        observeCampusWifi()

        // Brand SwipeRefresh colors
        binding.swipeRefresh.setColorSchemeColors(
            ContextCompat.getColor(requireContext(), R.color.brand_primary),
            ContextCompat.getColor(requireContext(), R.color.brand_accent)
        )
        binding.swipeRefresh.setProgressBackgroundColorSchemeColor(
            ContextCompat.getColor(requireContext(), R.color.bg_surface_raised)
        )

        // Staggered header entrance
        listOf<View>(binding.tvGreeting, binding.tvName, binding.ivAvatar,
            binding.ivNotificationBell).animateEntrance(startDelay = 100L, stepDelay = 60L)

        viewModel.loadCurrentUser()
        viewModel.loadStudentHomeData(uid)

        viewModel.currentUser.observe(viewLifecycleOwner) { user ->
            user ?: return@observe
            // Dynamic greeting based on time of day
            val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
            val greeting = when {
                hour < 12 -> "Good morning,"
                hour < 17 -> "Good afternoon,"
                else -> "Good evening,"
            }
            binding.tvGreeting.text = greeting
            binding.tvName.text = user.name
            binding.ivAvatar.loadAvatar(user.photoUrl)
        }

        viewModel.todayClasses.observe(viewLifecycleOwner) { classes ->
            todayClassesAdapter.submitList(classes)
            binding.tvNoTodayClasses.visibility = if (classes.isEmpty()) View.VISIBLE else View.GONE
            if (classes.isNotEmpty()) binding.rvTodayClasses.scheduleLayoutAnimation()
        }

        viewModel.upcomingDeadlines.observe(viewLifecycleOwner) { assignments ->
            deadlinesAdapter.submitList(assignments)
            binding.layoutDeadlinesEmpty.visibility =
                if (assignments.isEmpty()) View.VISIBLE else View.GONE
            if (assignments.isNotEmpty()) binding.rvDeadlines.scheduleLayoutAnimation()
        }

        viewModel.recentAnnouncements.observe(viewLifecycleOwner) { announcements ->
            announcementsAdapter.submitList(announcements)
            binding.layoutAnnouncementsEmpty.visibility =
                if (announcements.isEmpty()) View.VISIBLE else View.GONE
            if (announcements.isNotEmpty()) binding.rvAnnouncements.scheduleLayoutAnimation()
        }

        viewModel.unreadCount.observe(viewLifecycleOwner) { count ->
            if (count > 0) {
                binding.tvNotificationBadge.show()
                binding.tvNotificationBadge.text = if (count > 99) "99+" else count.toString()
                binding.ivNotificationBell.shakeAnimation()
            } else {
                binding.tvNotificationBadge.hide()
            }
        }

        binding.ivNotificationBell.setOnClickListener {
            findNavController().navigate(R.id.notificationsFragment)
        }

        binding.btnMarkAttendance.setOnClickListener {
            showSnackbar(getString(R.string.scan_qr))
        }

        binding.btnJoinClass.setOnClickListener {
            // Opens join class bottom sheet from ClassListFragment
            findNavController().navigate(R.id.classListFragment)
        }

        binding.tvViewTimetable.setOnClickListener {
            findNavController().navigate(R.id.classListFragment)
        }

        binding.btnAiBuddy.setOnClickListener {
            findNavController().navigate(R.id.aiBuddyFragment)
        }

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.loadCurrentUser()
            viewModel.loadStudentHomeData(uid)
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

        deadlinesAdapter = UpcomingDeadlinesAdapter()
        binding.rvDeadlines.layoutManager = LinearLayoutManager(requireContext())
        binding.rvDeadlines.adapter = deadlinesAdapter

        announcementsAdapter = RecentAnnouncementsAdapter()
        binding.rvAnnouncements.layoutManager = LinearLayoutManager(requireContext())
        binding.rvAnnouncements.adapter = announcementsAdapter
    }

    private fun setupSensor() {
        sensorHandler = SensorHandler(
            context = requireContext(),
            onShake = {
                val uid = auth.currentUser?.uid ?: return@SensorHandler
                viewModel.loadStudentHomeData(uid)
                showSnackbar(getString(R.string.refreshed))
            },
            onFlat = {
                // Device face-up: suggest DND (informational only)
            }
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

    private fun observeCampusWifi() {
        val prefs = requireContext().getSharedPreferences("classconnect_prefs", Context.MODE_PRIVATE)
        val campusSsid = prefs.getString("campus_wifi_ssid", null)
        if (!campusSsid.isNullOrBlank()) {
            val current = NetworkUtils.getConnectedSsid(requireContext())
            if (current == campusSsid) binding.tvCampusIndicator.show()
            else binding.tvCampusIndicator.hide()
        } else {
            binding.tvCampusIndicator.hide()
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
