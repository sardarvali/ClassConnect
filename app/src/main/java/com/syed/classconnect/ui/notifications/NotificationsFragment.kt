package com.syed.classconnect.ui.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.syed.classconnect.R
import com.syed.classconnect.databinding.FragmentNotificationsBinding
import com.syed.classconnect.util.NetworkResult
import com.syed.classconnect.util.hide
import com.syed.classconnect.util.show
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: NotificationsViewModel by viewModels()
<<<<<<< HEAD
    @Inject lateinit var auth: FirebaseAuth
    private lateinit var adapter: NotificationsAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
=======
    @Inject
    lateinit var auth: FirebaseAuth
    private lateinit var adapter: NotificationsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
>>>>>>> final
        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val uid = auth.currentUser?.uid ?: return

        adapter = NotificationsAdapter { notification ->
            viewModel.markAsRead(uid, notification.id)
        }
        binding.rvNotifications.layoutManager = LinearLayoutManager(requireContext())
        binding.rvNotifications.adapter = adapter

        // Swipe to delete
<<<<<<< HEAD
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(rv: RecyclerView, v: RecyclerView.ViewHolder, t: RecyclerView.ViewHolder) = false
=======
        ItemTouchHelper(object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                rv: RecyclerView,
                v: RecyclerView.ViewHolder,
                t: RecyclerView.ViewHolder
            ) = false

>>>>>>> final
            override fun onSwiped(holder: RecyclerView.ViewHolder, dir: Int) {
                val notif = adapter.currentList[holder.adapterPosition]
                viewModel.deleteNotification(uid, notif.id)
            }
        }).attachToRecyclerView(binding.rvNotifications)

        viewModel.loadNotifications(uid)

        viewModel.notifications.observe(viewLifecycleOwner) { result ->
            when (result) {
<<<<<<< HEAD
                is NetworkResult.Loading -> { binding.progressBar.show(); binding.layoutEmpty.hide() }
                is NetworkResult.Success -> {
                    binding.progressBar.hide()
                    if (result.data.isEmpty()) { binding.layoutEmpty.show(); binding.rvNotifications.hide() }
                    else { binding.layoutEmpty.hide(); binding.rvNotifications.show(); adapter.submitList(result.data) }
                }
=======
                is NetworkResult.Loading -> {
                    binding.progressBar.show(); binding.layoutEmpty.hide()
                }

                is NetworkResult.Success -> {
                    binding.progressBar.hide()
                    if (result.data.isEmpty()) {
                        binding.layoutEmpty.show(); binding.rvNotifications.hide()
                    } else {
                        binding.layoutEmpty.hide(); binding.rvNotifications.show(); adapter.submitList(
                            result.data
                        )
                    }
                }

>>>>>>> final
                is NetworkResult.Error -> binding.progressBar.hide()
            }
        }

        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, inflater: MenuInflater) {
                inflater.inflate(R.menu.menu_notifications, menu)
            }
<<<<<<< HEAD
            override fun onMenuItemSelected(item: MenuItem): Boolean {
                if (item.itemId == R.id.action_mark_all_read) { viewModel.markAllAsRead(uid); return true }
=======

            override fun onMenuItemSelected(item: MenuItem): Boolean {
                if (item.itemId == R.id.action_mark_all_read) {
                    viewModel.markAllAsRead(uid); return true
                }
>>>>>>> final
                return false
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

<<<<<<< HEAD
    override fun onDestroyView() { super.onDestroyView(); _binding = null }
=======
    override fun onDestroyView() {
        super.onDestroyView(); _binding = null
    }
>>>>>>> final
}

