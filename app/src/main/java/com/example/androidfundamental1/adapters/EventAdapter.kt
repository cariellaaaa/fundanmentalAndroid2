package com.example.androidfundamental1.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.androidfundamental1.R
import com.example.androidfundamental1.databinding.ItemEventBinding
import com.example.androidfundamental1.models.Events

class EventAdapter(
    private val onItemClick: (Events) -> Unit // Lambda untuk menangani klik item
) : ListAdapter<Events, EventAdapter.EventViewHolder>(DiffCallback()) {

    // ViewHolder untuk menampilkan setiap item event
    class EventViewHolder(private val binding: ItemEventBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(event: Events, onItemClick: (Events) -> Unit) {
            // Akses view langsung melalui binding
            binding.eventName.text = event.name
            binding.eventCategory.text = event.category
            binding.eventSummary.text = event.summary

            // Gunakan string resource dengan placeholder untuk waktu
            binding.eventTime.text = binding.root.context.getString(
                R.string.event_time_format, event.beginTime, event.endTime
            )

            binding.eventCityName.text = event.cityName

            // Muat logo event menggunakan Glide
            Glide.with(binding.root.context)
                .load(event.imageLogo)
                .into(binding.eventLogo)

            // Set content description for accessibility
            binding.eventLogo.contentDescription = "${event.name} logo"

            // Set onClickListener untuk item
            binding.root.setOnClickListener {
                onItemClick(event)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        // Inflate layout menggunakan ViewBinding
        val binding = ItemEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EventViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = getItem(position)
        holder.bind(event, onItemClick)
    }

    // DiffCallback untuk membandingkan item secara efisien
    class DiffCallback : DiffUtil.ItemCallback<Events>() {
        override fun areItemsTheSame(oldItem: Events, newItem: Events): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Events, newItem: Events): Boolean {
            return oldItem == newItem
        }
    }
}
