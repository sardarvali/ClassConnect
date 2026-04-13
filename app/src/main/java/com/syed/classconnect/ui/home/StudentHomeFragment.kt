package com.syed.classconnect.ui.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
<<<<<<< HEAD
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
=======
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
>>>>>>> final
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.syed.classconnect.R
import com.syed.classconnect.databinding.FragmentStudentHomeBinding
import com.syed.classconnect.sensor.SensorHandler
import com.syed.classconnect.util.NetworkUtils
<<<<<<< HEAD
=======
import com.syed.classconnect.util.addPressEffect
>>>>>>> final
import com.syed.classconnect.util.animateEntrance
import com.syed.classconnect.util.hide
import com.syed.classconnect.util.loadAvatar
import com.syed.classconnect.util.shakeAnimation
import com.syed.classconnect.util.show
import com.syed.classconnect.util.showSnackbar
<<<<<<< HEAD
import androidx.core.content.ContextCompat
=======
>>>>>>> final
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class StudentHomeFragment : Fragment() {

    private var _binding: FragmentStudentHomeBinding? = null
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
    private lateinit var deadlinesAdapter: UpcomingDeadlinesAdapter
    private lateinit var announcementsAdapter: RecentAnnouncementsAdapter

<<<<<<< HEAD
=======
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
<<<<<<< HEAD
        listOf<View>(binding.tvGreeting, binding.tvName, binding.ivAvatar,
            binding.ivNotificationBell).animateEntrance(startDelay = 100L, stepDelay = 60L)
=======
        if (!hasPlayedEntrance) {
            listOf<View>(
                binding.tvGreeting,
                binding.tvName,
                binding.ivAvatar,
                binding.ivNotificationBell,
                binding.btnMarkAttendance,
                binding.btnJoinClass,
                binding.btnAiBuddy
            ).animateEntrance(startDelay = 100L, stepDelay = 50L)
            hasPlayedEntrance = true
        }

        // Add subtle tap feedback to key home actions.
        listOf<View>(
            binding.ivNotificationBell,
            binding.btnMarkAttendance,
            binding.btnJoinClass,
            binding.btnAiBuddy,
            binding.tvViewTimetable
        ).forEach { it.addPressEffect() }
>>>>>>> final

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
<<<<<<< HEAD
            binding.tvNoTodayClasses.visibility = if (classes.isEmpty()) View.VISIBLE else View.GONE
            if (classes.isNotEmpty()) binding.rvTodayClasses.scheduleLayoutAnimation()
=======
            val hasItems = classes.isNotEmpty()
            if (hasItems) {
                if (binding.rvTodayClasses.visibility != View.VISIBLE) {
                    com.syed.classconnect.util.crossFade(
                        binding.rvTodayClasses,
                        binding.tvNoTodayClasses
                    )
                } else {
                    binding.tvNoTodayClasses.hide()
                    binding.rvTodayClasses.show()
                }
                binding.rvTodayClasses.scheduleLayoutAnimation()
            } else {
                if (binding.tvNoTodayClasses.visibility != View.VISIBLE) {
                    com.syed.classconnect.util.crossFade(
                        binding.tvNoTodayClasses,
                        binding.rvTodayClasses
                    )
                } else {
                    binding.rvTodayClasses.hide()
                    binding.tvNoTodayClasses.show()
                }
            }
>>>>>>> final
        }

        viewModel.upcomingDeadlines.observe(viewLifecycleOwner) { assignments ->
            deadlinesAdapter.submitList(assignments)
<<<<<<< HEAD
            binding.layoutDeadlinesEmpty.visibility =
                if (assignments.isEmpty()) View.VISIBLE else View.GONE
            if (assignments.isNotEmpty()) binding.rvDeadlines.scheduleLayoutAnimation()
=======
            val hasItems = assignments.isNotEmpty()
            if (hasItems) {
                if (binding.rvDeadlines.visibility != View.VISIBLE) {
                    com.syed.classconnect.util.crossFade(
                        binding.rvDeadlines,
                        binding.layoutDeadlinesEmpty
                    )
                } else {
                    binding.layoutDeadlinesEmpty.hide()
                    binding.rvDeadlines.show()
                }
                binding.rvDeadlines.scheduleLayoutAnimation()
            } else {
                if (binding.layoutDeadlinesEmpty.visibility != View.VISIBLE) {
                    com.syed.classconnect.util.crossFade(
                        binding.layoutDeadlinesEmpty,
                        binding.rvDeadlines
                    )
                } else {
                    binding.rvDeadlines.hide()
                    binding.layoutDeadlinesEmpty.show()
                }
            }
>>>>>>> final
        }

        viewModel.recentAnnouncements.observe(viewLifecycleOwner) { announcements ->
            announcementsAdapter.submitList(announcements)
<<<<<<< HEAD
            binding.layoutAnnouncementsEmpty.visibility =
                if (announcements.isEmpty()) View.VISIBLE else View.GONE
            if (announcements.isNotEmpty()) binding.rvAnnouncements.scheduleLayoutAnimation()
=======
            val hasItems = announcements.isNotEmpty()
            if (hasItems) {
                if (binding.rvAnnouncements.visibility != View.VISIBLE) {
                    com.syed.classconnect.util.crossFade(
                        binding.rvAnnouncements,
                        binding.layoutAnnouncementsEmpty
                    )
                } else {
                    binding.layoutAnnouncementsEmpty.hide()
                    binding.rvAnnouncements.show()
                }
                binding.rvAnnouncements.scheduleLayoutAnimation()
            } else {
                if (binding.layoutAnnouncementsEmpty.visibility != View.VISIBLE) {
                    com.syed.classconnect.util.crossFade(
                        binding.layoutAnnouncementsEmpty,
                        binding.rvAnnouncements
                    )
                } else {
                    binding.rvAnnouncements.hide()
                    binding.layoutAnnouncementsEmpty.show()
                }
            }
>>>>>>> final
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
<<<<<<< HEAD
            findNavController().navigate(R.id.notificationsFragment)
=======
            findNavController().navigate(R.id.notificationsFragment, null, homeNavOptions)
>>>>>>> final
        }

        binding.btnMarkAttendance.setOnClickListener {
            showSnackbar(getString(R.string.scan_qr))
        }

        binding.btnJoinClass.setOnClickListener {
            // Opens join class bottom sheet from ClassListFragment
<<<<<<< HEAD
            findNavController().navigate(R.id.classListFragment)
        }

        binding.btnAiBuddy.setOnClickListener {
            findNavController().navigate(R.id.aiBuddyFragment)
=======
            findNavController().navigate(R.id.classListFragment, null, homeNavOptions)
        }

        binding.tvViewTimetable.setOnClickListener {
            findNavController().navigate(R.id.classListFragment, null, homeNavOptions)
        }

        binding.btnAiBuddy.setOnClickListener {
            findNavController().navigate(R.id.aiBuddyFragment, null, homeNavOptions)
>>>>>>> final
        }

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.loadCurrentUser()
            viewModel.loadStudentHomeData(uid)
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
<<<<<<< HEAD
        val prefs = requireContext().getSharedPreferences("classconnect_prefs", Context.MODE_PRIVATE)
=======
        val prefs =
            requireContext().getSharedPreferences("classconnect_prefs", Context.MODE_PRIVATE)
>>>>>>> final
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
