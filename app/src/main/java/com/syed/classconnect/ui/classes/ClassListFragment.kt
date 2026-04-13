package com.syed.classconnect.ui.classes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
<<<<<<< HEAD
=======
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
>>>>>>> final
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.syed.classconnect.R
import com.syed.classconnect.databinding.FragmentClassListBinding
import com.syed.classconnect.util.Constants
import com.syed.classconnect.util.NetworkResult
<<<<<<< HEAD
=======
import com.syed.classconnect.util.addPressEffect
>>>>>>> final
import com.syed.classconnect.util.animateFabEntrance
import com.syed.classconnect.util.hide
import com.syed.classconnect.util.show
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ClassListFragment : Fragment() {

    private var _binding: FragmentClassListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ClassViewModel by viewModels()
<<<<<<< HEAD
    @Inject lateinit var auth: FirebaseAuth
    private lateinit var adapter: ClassAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
=======
    @Inject
    lateinit var auth: FirebaseAuth
    private lateinit var adapter: ClassAdapter
    private var hasPlayedContentTransition = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
>>>>>>> final
        _binding = FragmentClassListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

<<<<<<< HEAD
        adapter = ClassAdapter { classRoom ->
            ClassDetailActivity.start(requireContext(), classRoom.id, classRoom.name, classRoom.color, classRoom.classCode)
=======
        val fabBaseMargin = resources.getDimensionPixelSize(R.dimen.fab_margin)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            val bottomNavOverlay = getBottomNavOverlayHeight()
            val navBottom = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom
            val bottomInset = maxOf(navBottom, bottomNavOverlay)

            binding.rvClasses.updatePadding(bottom = bottomInset + 88) // Extra for FAB

            binding.fabAction.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                bottomMargin = fabBaseMargin + bottomInset
            }

            insets
        }

        adapter = ClassAdapter { classRoom, sharedBg, sharedTitle ->
            ClassDetailActivity.start(
                context = requireContext(),
                classId = classRoom.id,
                className = classRoom.name,
                color = classRoom.color,
                classCode = classRoom.classCode,
                subject = classRoom.subject,
                teacherName = classRoom.teacherName,
                studentCount = classRoom.studentIds.size,
                sharedBackgroundView = sharedBg,
                sharedTitleView = sharedTitle
            )
>>>>>>> final
        }
        binding.rvClasses.layoutManager = LinearLayoutManager(requireContext())
        binding.rvClasses.adapter = adapter
        binding.rvClasses.setHasFixedSize(true)

        // Brand SwipeRefresh colors
        binding.swipeRefresh.setColorSchemeColors(
            ContextCompat.getColor(requireContext(), R.color.brand_primary),
            ContextCompat.getColor(requireContext(), R.color.brand_accent)
        )
        binding.swipeRefresh.setProgressBackgroundColorSchemeColor(
            ContextCompat.getColor(requireContext(), R.color.bg_surface_raised)
        )

        // FAB entrance pop-in
        binding.fabAction.animateFabEntrance(300)
<<<<<<< HEAD
=======
        binding.fabAction.addPressEffect()
>>>>>>> final

        val uid = auth.currentUser?.uid ?: return

        viewModel.loadUser(uid)
        viewModel.currentUser.observe(viewLifecycleOwner) { user ->
            user ?: return@observe
            viewModel.loadClasses(uid, user.role)
            binding.fabAction.setOnClickListener {
                when (user.role) {
                    Constants.ROLE_TEACHER, Constants.ROLE_ADMIN ->
                        CreateClassBottomSheet().show(parentFragmentManager, "create_class")
<<<<<<< HEAD
=======

>>>>>>> final
                    else ->
                        JoinClassBottomSheet().show(parentFragmentManager, "join_class")
                }
            }
        }

        viewModel.classes.observe(viewLifecycleOwner) { result ->
            when (result) {
<<<<<<< HEAD
                is NetworkResult.Loading -> { binding.progressBar.show(); binding.layoutEmpty.hide() }
                is NetworkResult.Success -> {
                    binding.progressBar.hide()
                    if (result.data.isEmpty()) {
                        binding.rvClasses.hide(); binding.layoutEmpty.show()
                        // Animate empty state
                        binding.tvEmptyTitle.alpha = 0f; binding.tvEmptySubtitle.alpha = 0f
                        binding.tvEmptyTitle.animate().alpha(1f).setStartDelay(300).setDuration(400).start()
                        binding.tvEmptySubtitle.animate().alpha(1f).setStartDelay(450).setDuration(400).start()
                    } else {
                        binding.rvClasses.show(); binding.layoutEmpty.hide()
                        adapter.submitList(result.data)
                        binding.rvClasses.scheduleLayoutAnimation()
                    }
                }
                is NetworkResult.Error -> { binding.progressBar.hide() }
=======
                is NetworkResult.Loading -> {
                    binding.progressBar.show()
                    binding.layoutEmpty.hide()
                }

                is NetworkResult.Success -> {
                    binding.progressBar.hide()
                    if (result.data.isEmpty()) {
                        adapter.submitList(emptyList())
                        if (binding.layoutEmpty.visibility != View.VISIBLE && hasPlayedContentTransition) {
                            com.syed.classconnect.util.crossFade(
                                binding.layoutEmpty,
                                binding.rvClasses
                            )
                        } else {
                            binding.rvClasses.hide()
                            binding.layoutEmpty.show()
                        }
                        // Animate empty state
                        binding.tvEmptyTitle.alpha = 0f
                        binding.tvEmptySubtitle.alpha = 0f
                        binding.tvEmptyTitle.animate().alpha(1f).setStartDelay(180).setDuration(260)
                            .start()
                        binding.tvEmptySubtitle.animate().alpha(1f).setStartDelay(260)
                            .setDuration(260).start()
                    } else {
                        adapter.submitList(result.data)
                        if (binding.rvClasses.visibility != View.VISIBLE && hasPlayedContentTransition) {
                            com.syed.classconnect.util.crossFade(
                                binding.rvClasses,
                                binding.layoutEmpty
                            )
                        } else {
                            binding.layoutEmpty.hide()
                            binding.rvClasses.show()
                        }
                        binding.rvClasses.scheduleLayoutAnimation()
                    }
                    hasPlayedContentTransition = true
                }

                is NetworkResult.Error -> {
                    binding.progressBar.hide()
                }
>>>>>>> final
            }
        }

        binding.swipeRefresh.setOnRefreshListener {
            val uid2 = auth.currentUser?.uid ?: return@setOnRefreshListener
            viewModel.currentUser.value?.role?.let { role -> viewModel.loadClasses(uid2, role) }
            binding.swipeRefresh.isRefreshing = false
        }
    }

<<<<<<< HEAD
    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}

=======
    private fun getBottomNavOverlayHeight(): Int {
        val navContainer = activity?.findViewById<View>(R.id.bottom_nav_container) ?: return 0
        if (navContainer.visibility != View.VISIBLE) return 0
        val lp = navContainer.layoutParams as? ViewGroup.MarginLayoutParams
        return navContainer.height + (lp?.bottomMargin ?: 0)
    }

    override fun onDestroyView() {
        super.onDestroyView(); _binding = null
    }
}
>>>>>>> final
