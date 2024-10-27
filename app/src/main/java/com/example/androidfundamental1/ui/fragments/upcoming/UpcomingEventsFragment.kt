package com.example.androidfundamental1.ui.fragments.upcoming

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidfundamental1.R
import com.example.androidfundamental1.adapters.EventAdapter
import com.example.androidfundamental1.api.RetrofitInstance
import com.example.androidfundamental1.databinding.FragmentUpcomingEventsBinding
import com.example.androidfundamental1.db.EventDatabase
import com.example.androidfundamental1.repo.EventRepo
import com.example.androidfundamental1.ui.vm.EventViewModel
import com.example.androidfundamental1.ui.vm.EventViewModelProviderFactory
import com.example.androidfundamental1.util.Status

class UpcomingEventsFragment : Fragment(R.layout.fragment_upcoming_events) {
    private var _binding: FragmentUpcomingEventsBinding? = null
    private val binding get() = _binding?: throw IllegalStateException("Binding tidak dapat diakses")
    private lateinit var viewModel: EventViewModel
    private lateinit var eventAdapter: EventAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpcomingEventsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.title = "Upcoming Events"

        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.upcoming_events)

        val bangkitAPI = RetrofitInstance.api
        val eventDatabase = EventDatabase.getDatabase(requireContext())
        val sharedPreferences = requireContext().getSharedPreferences("FavoritesPrefs", Context.MODE_PRIVATE)
        val eventRepo = EventRepo(bangkitAPI, eventDatabase, sharedPreferences)

        val viewModelProviderFactory = EventViewModelProviderFactory(eventRepo)

        viewModel = ViewModelProvider(this, viewModelProviderFactory)[EventViewModel::class.java]

        setupRecyclerView()

        viewModel.upcomingEventsLiveData.observe(viewLifecycleOwner) { resource ->
            when (resource.status) {
                Status.SUCCESS -> {
                    resource.data?.let { eventAdapter.submitList(it) }
                    binding.paginationProgressBar.visibility = View.GONE
                }
                Status.ERROR -> {
                    Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show()
                    binding.paginationProgressBar.visibility = View.GONE
                }
                Status.LOADING -> {
                    binding.paginationProgressBar.visibility = View.VISIBLE
                }
            }
        }

        viewModel.fetchUpcomingEvents()
    }

    private fun setupRecyclerView() {
        eventAdapter = EventAdapter { event ->
            val action = UpcomingEventsFragmentDirections.actionToEventDetail(event.id)
            findNavController().navigate(action)
        }

        binding.rvUpcomingEvents.apply {
            adapter = eventAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

