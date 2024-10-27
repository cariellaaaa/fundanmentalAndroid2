package com.example.androidfundamental1.ui.fragments.finish

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidfundamental1.R
import com.example.androidfundamental1.adapters.EventAdapter
import com.example.androidfundamental1.api.RetrofitInstance
import com.example.androidfundamental1.databinding.FragmentPastEventsBinding
import com.example.androidfundamental1.db.EventDatabase
import com.example.androidfundamental1.repo.EventRepo
import com.example.androidfundamental1.ui.vm.EventViewModel
import com.example.androidfundamental1.ui.vm.EventViewModelProviderFactory
import com.example.androidfundamental1.util.Status

class PastEventsFragment : Fragment(R.layout.fragment_past_events) {
    private var _binding: FragmentPastEventsBinding? = null
    private val binding get() = _binding ?: throw IllegalStateException("Binding tidak dapat diakses")
    private lateinit var viewModel: EventViewModel
    private lateinit var eventAdapter: EventAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPastEventsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        setupActionBar()
        setupViewModel()
        setupRecyclerView()
        observePastEvents()
        setupSearchFunctionality()
    }

    private fun setupRecyclerView() {
        eventAdapter = EventAdapter { event ->
            val action = PastEventsFragmentDirections.actionPastEventsFragmentToEventDetailFragment(event.id)
            findNavController().navigate(action)
        }

        binding.rvPastEvents.apply {
            adapter = eventAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setupActionBar() {
        (activity as AppCompatActivity).supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = getString(R.string.past_events)
        }
    }

    private fun setupViewModel() {
        val bangkitAPI = RetrofitInstance.api
        val eventDatabase = EventDatabase.getDatabase(requireContext())
        val sharedPreferences = requireContext().getSharedPreferences("FavoritesPrefs", Context.MODE_PRIVATE)
        val eventRepo = EventRepo(bangkitAPI, eventDatabase, sharedPreferences)
        val factory = EventViewModelProviderFactory(eventRepo)
        viewModel = ViewModelProvider(this, factory)[EventViewModel::class.java]
    }

    private fun observePastEvents() {
        viewModel.pastEventsLiveData.observe(viewLifecycleOwner) { resource ->
            when (resource.status) {
                Status.LOADING -> {
                    binding.paginationProgressBar.visibility = View.VISIBLE
                    binding.rvPastEvents.visibility = View.GONE
                    binding.itemError.root.visibility = View.GONE
                }
                Status.SUCCESS -> {
                    binding.paginationProgressBar.visibility = View.GONE
                    binding.rvPastEvents.visibility = View.VISIBLE
                    resource.data?.let { events ->
                        eventAdapter.submitList(events)
                    }
                }
                Status.ERROR -> {
                    binding.paginationProgressBar.visibility = View.GONE
                    binding.itemError.root.visibility = View.VISIBLE
                    binding.itemError.errorMessage.text = resource.message ?: "Unknown Error"
                }
            }
        }
    }

    private fun setupSearchFunctionality() {
        binding.searchEdit.setOnClickListener {
            // Tindakan saat EditText diklik
            performSearch()
        }


        binding.searchEdit.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch()
                true
            } else {
                false
            }
        }
    }

    private fun performSearch() {
        val query = binding.searchEdit.text.toString().trim()
        if (query.isNotEmpty()) {
            viewModel.searchPastEvents(query)
            binding.searchEdit.clearFocus()
        } else {
            Toast.makeText(requireContext(), "Please enter a search term", Toast.LENGTH_SHORT).show()
        }
    }

    private fun isDrawableEndClicked(event: MotionEvent): Boolean {
        val drawableEnd = 2
        return event.rawX >= (binding.searchEdit.right - binding.searchEdit.compoundDrawables[drawableEnd].bounds.width())
    }




    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}
