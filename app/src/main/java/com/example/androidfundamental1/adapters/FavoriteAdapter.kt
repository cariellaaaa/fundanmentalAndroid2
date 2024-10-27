package com.example.androidfundamental1.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.androidfundamental1.databinding.ItemFavoriteEventBinding
import com.example.androidfundamental1.models.Events

class FavoriteEventAdapter(
    private var events: List<Events>,
    private val onItemClick: (Events) -> Unit,
    private val onRemoveFavorite: (String) -> Unit // Callback untuk menghapus event dari favorite
) : RecyclerView.Adapter<FavoriteEventAdapter.EventViewHolder>() {

    inner class EventViewHolder(private val binding: ItemFavoriteEventBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(event: Events) {
            // Set event name and date
            binding.tvEventTitle.text = event.name
            binding.tvEventDate.text = event.beginTime

            // Load event image using Glide
            Glide.with(binding.ivEventLogo.context)
                .load(event.mediaCover)
                .into(binding.ivEventLogo)

            // Set click listener for the item to open event details
            binding.root.setOnClickListener { onItemClick(event) }

            // Set click listener for the favorite icon to remove from favorites
            binding.ivFavorite.setOnClickListener {
                onRemoveFavorite(event.id.toString()) // Call the callback to remove
                // Remove the event from the list and update the adapter
                val updatedEvents = events.filterNot { it.id.toString() == event.id.toString() }
                updateData(updatedEvents)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val binding = ItemFavoriteEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EventViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        holder.bind(events[position])
    }

    override fun getItemCount(): Int = events.size

    // Method to update the adapter's data
    fun updateData(newEvents: List<Events>) {
        events = newEvents
        notifyDataSetChanged()
    }
}
