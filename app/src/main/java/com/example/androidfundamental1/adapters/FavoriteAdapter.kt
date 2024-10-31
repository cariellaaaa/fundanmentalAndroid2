package com.example.androidfundamental1.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.androidfundamental1.databinding.ItemFavoriteEventBinding
import com.example.androidfundamental1.models.FavoriteEntity


class FavoriteEventAdapter(
    private var favorites: List<FavoriteEntity>,
    private val onItemClick: (FavoriteEntity) -> Unit,
    private val onRemoveFavorite: (Int) -> Unit // Gunakan Int untuk eventId
) : RecyclerView.Adapter<FavoriteEventAdapter.EventViewHolder>() {

    inner class EventViewHolder(private val binding: ItemFavoriteEventBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(favorite: FavoriteEntity) {
            // Set data favorit seperti nama dan tanggal
            binding.tvEventTitle.text = favorite.name
            binding.tvEventDate.text = favorite.date

            // Set click listener untuk item
            binding.root.setOnClickListener { onItemClick(favorite) }

            // Set click listener untuk menghapus favorit
            binding.ivFavorite.setOnClickListener {
                onRemoveFavorite(favorite.id) // Menggunakan id dari FavoriteEntity
                val updatedFavorites = favorites.filterNot { it.id == favorite.id }
                updateData(updatedFavorites)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val binding = ItemFavoriteEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EventViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        holder.bind(favorites[position])
    }

    override fun getItemCount(): Int = favorites.size

    // Ubah parameter `updateData` untuk menerima List<FavoriteEntity>
    fun updateData(newFavorites: List<FavoriteEntity>) {
        favorites = newFavorites
        notifyDataSetChanged()
    }

}
