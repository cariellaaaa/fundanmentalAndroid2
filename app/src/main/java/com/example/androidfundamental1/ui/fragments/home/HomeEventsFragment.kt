package com.example.androidfundamental1.ui.fragments.home

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidfundamental1.R
import com.example.androidfundamental1.adapters.UpcomingEventAdapter
import com.example.androidfundamental1.adapters.PastEventAdapter
import com.example.androidfundamental1.api.RetrofitInstance
import com.example.androidfundamental1.databinding.FragmentHomeEventsBinding
import com.example.androidfundamental1.databinding.ItemErrorBinding
import com.example.androidfundamental1.db.EventDatabase
import com.example.androidfundamental1.repo.EventRepo
import com.example.androidfundamental1.ui.vm.EventViewModel
import com.example.androidfundamental1.ui.vm.EventViewModelProviderFactory
import com.example.androidfundamental1.util.Status

class HomeEventsFragment : Fragment(R.layout.fragment_home_events) {
    private var _binding: FragmentHomeEventsBinding? = null
    private val binding get() = _binding?: throw IllegalStateException("Binding tidak dapat diakses")
    private lateinit var viewModel: EventViewModel
    private lateinit var upcomingEventAdapter: UpcomingEventAdapter
    private lateinit var pastEventAdapter: PastEventAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeEventsBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize view itemError
        val errorLayout = binding.itemError.root
        val errorBinding = ItemErrorBinding.bind(binding.itemError.root)

        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
        (activity as AppCompatActivity).supportActionBar?.title = "Home"

        // Initialize ViewModel with Factory
        val bangkitAPI = RetrofitInstance.api
        val eventDatabase = EventDatabase.getDatabase(requireContext())
        val sharedPreferences = requireContext().getSharedPreferences("FavoritesPrefs", Context.MODE_PRIVATE)
        val eventRepo = EventRepo(bangkitAPI, eventDatabase, sharedPreferences)
        val factory = EventViewModelProviderFactory(eventRepo)
        viewModel = ViewModelProvider(this, factory)[EventViewModel::class.java]


        setupUpcomingRecyclerView()
        setupPastRecyclerView()


        viewModel.upcomingEventsLiveData.observe(viewLifecycleOwner) { resource ->
            when (resource.status) {
                Status.LOADING -> {
                    binding.paginationProgressBar.visibility = View.VISIBLE
                    errorLayout.visibility = View.GONE
                    errorBinding.root.visibility = View.GONE
                }
                Status.SUCCESS -> {
                    binding.paginationProgressBar.visibility = View.GONE
                    errorLayout.visibility = View.GONE
                    resource.data?.let { events ->
                        upcomingEventAdapter.submitList(events)
                    }
                }
                Status.ERROR -> {
                    binding.paginationProgressBar.visibility = View.GONE
                    errorLayout.visibility = View.VISIBLE
                    errorBinding.root.visibility = View.VISIBLE
                    binding.itemError.errorMessage.text = resource.message ?: "Unknown Error"
                    errorBinding.errorMessage.text = "Error message here"
                }
            }
        }

        // Observe past events from ViewModel
        viewModel.pastEventsLiveData.observe(viewLifecycleOwner) { resource ->
            when (resource.status) {
                Status.LOADING -> {
                    binding.paginationProgressBar.visibility = View.VISIBLE
                    errorLayout.visibility = View.GONE
                }
                Status.SUCCESS -> {
                    binding.paginationProgressBar.visibility = View.GONE
                    errorLayout.visibility = View.GONE
                    resource.data?.let { events ->
                        pastEventAdapter.submitList(events)
                    }
                }
                Status.ERROR -> {
                    binding.paginationProgressBar.visibility = View.GONE
                    errorLayout.visibility = View.VISIBLE
                    binding.itemError.errorMessage.text = resource.message ?: "Unknown Error"
                }
            }
        }


        binding.itemError.root.findViewById<Button>(R.id.retryButton).setOnClickListener {
            viewModel.retryFetchEvents()
        }
    }

    private fun setupUpcomingRecyclerView() {
        upcomingEventAdapter = UpcomingEventAdapter { event ->
            val action = HomeEventsFragmentDirections.actionHomeEventsFragmentToEventDetailFragment(event.id)
            findNavController().navigate(action)
        }

        binding.rvUpcomingEvents.apply {
            adapter = upcomingEventAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
    }

    private fun setupPastRecyclerView() {
        pastEventAdapter = PastEventAdapter { event ->
            val action = HomeEventsFragmentDirections.actionHomeEventsFragmentToEventDetailFragment(event.id)
            findNavController().navigate(action)
        }

        binding.rvPastEvents.apply {
            adapter = pastEventAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}