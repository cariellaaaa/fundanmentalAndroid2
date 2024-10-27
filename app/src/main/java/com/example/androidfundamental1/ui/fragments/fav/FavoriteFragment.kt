package com.example.androidfundamental1.ui.fragments.fav

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.androidfundamental1.R
import com.example.androidfundamental1.adapters.FavoriteEventAdapter
import com.example.androidfundamental1.api.RetrofitInstance
import com.example.androidfundamental1.db.EventDatabase
import com.example.androidfundamental1.models.Events
import com.example.androidfundamental1.repo.EventRepo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FavoriteFragment : Fragment(R.layout.fragment_favorite) {

    private lateinit var favoritesAdapter: FavoriteEventAdapter
    private lateinit var recyclerView: RecyclerView
    private var favoriteEvents: List<Events> = emptyList()
    private lateinit var eventRepository: EventRepo
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_favorite, container, false)
        recyclerView = view.findViewById(R.id.rvFavoritesEvent)
        setupRecyclerView()
        return view
    }

    private fun setupRecyclerView() {
        favoritesAdapter = FavoriteEventAdapter(favoriteEvents, { event ->
            val action = FavoriteFragmentDirections.actionFavoriteEventsFragmentToEventDetailFragment(event.id)
            findNavController().navigate(action)
        }, { eventId ->
            removeFavorite(eventId) // Panggil removeFavorite saat ikon di klik
        })
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = favoritesAdapter
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inisialisasi SharedPreferences
        sharedPreferences = requireContext().getSharedPreferences("FavoritesPrefs", Context.MODE_PRIVATE)

        // Inisialisasi repository
        val bangkitAPI = RetrofitInstance.api
        val eventDatabase = EventDatabase.getDatabase(requireContext())
        eventRepository = EventRepo(bangkitAPI, eventDatabase, sharedPreferences)

        // Muat event favorit
        loadFavoriteEvents()
    }

    private fun loadFavoriteEvents() {
        CoroutineScope(Dispatchers.Main).launch {
            val favoriteEventIds = eventRepository.getFavoriteEventIds()
            Log.d("FavoriteFragment", "Favorite Event IDs: $favoriteEventIds")

            favoriteEvents = withContext(Dispatchers.IO) {
                eventRepository.getEventsByIds(favoriteEventIds)
            }
            favoritesAdapter.updateData(favoriteEvents)
        }
    }

    private fun removeFavorite(eventId: String) {
        eventRepository.removeFavoriteEventId(eventId)  // Menggunakan EventRepo
        loadFavoriteEvents()  // Muat ulang daftar favorit
    }
}
