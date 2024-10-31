package com.example.androidfundamental1.ui.fragments.fav

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidfundamental1.R
import com.example.androidfundamental1.adapters.FavoriteEventAdapter
import com.example.androidfundamental1.databinding.FragmentFavoriteBinding
import com.example.androidfundamental1.models.FavoriteEntity
import com.example.androidfundamental1.ui.vm.FavoriteViewModel

class FavoriteFragment : Fragment(R.layout.fragment_favorite) {

    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!
    private lateinit var favoritesAdapter: FavoriteEventAdapter
    private lateinit var favoriteViewModel: FavoriteViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        favoriteViewModel = ViewModelProvider(this).get(FavoriteViewModel::class.java)

        setupRecyclerView()

        favoriteViewModel.allFavorites.observe(viewLifecycleOwner) { favorites ->
            favorites?.let {
                favoritesAdapter.updateData(favorites) // Pastikan `updateData` sudah terimplementasi
            }
        }
    }

    private fun setupRecyclerView() {
        favoritesAdapter = FavoriteEventAdapter(emptyList(), { favorite ->
            val action = FavoriteFragmentDirections.actionFavoriteEventsFragmentToEventDetailFragment(favorite.id)
            findNavController().navigate(action)
        }, { favoriteId ->
            removeFavorite(favoriteId)
        })
        binding.rvFavoritesEvent.layoutManager = LinearLayoutManager(requireContext())
        binding.rvFavoritesEvent.adapter = favoritesAdapter
    }

    private fun removeFavorite(eventId: Int) {
        val favoriteEntity = FavoriteEntity(id = eventId, name = "", date = "")
        favoriteViewModel.removeFavorite(favoriteEntity)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
