package com.syed.classconnect.ui.classes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.syed.classconnect.R
import com.syed.classconnect.databinding.FragmentClassListBinding
import com.syed.classconnect.util.Constants
import com.syed.classconnect.util.NetworkResult
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
    @Inject lateinit var auth: FirebaseAuth
    private lateinit var adapter: ClassAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentClassListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ClassAdapter { classRoom ->
            ClassDetailActivity.start(requireContext(), classRoom.id, classRoom.name, classRoom.color, classRoom.classCode)
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

        val uid = auth.currentUser?.uid ?: return

        viewModel.loadUser(uid)
        viewModel.currentUser.observe(viewLifecycleOwner) { user ->
            user ?: return@observe
            viewModel.loadClasses(uid, user.role)
            binding.fabAction.setOnClickListener {
                when (user.role) {
                    Constants.ROLE_TEACHER, Constants.ROLE_ADMIN ->
                        CreateClassBottomSheet().show(parentFragmentManager, "create_class")
                    else ->
                        JoinClassBottomSheet().show(parentFragmentManager, "join_class")
                }
            }
        }

        viewModel.classes.observe(viewLifecycleOwner) { result ->
            when (result) {
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
            }
        }

        binding.swipeRefresh.setOnRefreshListener {
            val uid2 = auth.currentUser?.uid ?: return@setOnRefreshListener
            viewModel.currentUser.value?.role?.let { role -> viewModel.loadClasses(uid2, role) }
            binding.swipeRefresh.isRefreshing = false
        }
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}

