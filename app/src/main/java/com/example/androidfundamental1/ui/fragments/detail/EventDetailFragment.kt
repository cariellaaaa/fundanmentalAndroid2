package com.example.androidfundamental1.ui.fragments.detail

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.androidfundamental1.R
import com.example.androidfundamental1.api.RetrofitInstance
import com.example.androidfundamental1.databinding.FragmentEventDetailBinding
import com.example.androidfundamental1.db.EventDatabase
import com.example.androidfundamental1.repo.EventRepo
import com.example.androidfundamental1.ui.vm.EventViewModel
import com.example.androidfundamental1.ui.vm.EventViewModelProviderFactory
import com.example.androidfundamental1.util.SharedPreferencesHelper
import com.example.androidfundamental1.util.Status

class EventDetailFragment : Fragment(R.layout.fragment_event_detail) {
    private var _binding: FragmentEventDetailBinding? = null
    private val binding get() = _binding ?: throw IllegalStateException("Binding tidak dapat diakses")
    private lateinit var viewModel: EventViewModel
    private val args: EventDetailFragmentArgs by navArgs()

    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    private lateinit var favoriteIcon: ImageView
    private var eventId: Int = 0  // ID event sebagai Int

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEventDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inisialisasi ViewModel
        val bangkitAPI = RetrofitInstance.api
        val eventDatabase = EventDatabase.getDatabase(requireContext())
        val sharedPreferences = requireContext().getSharedPreferences("FavoritesPrefs", Context.MODE_PRIVATE)
        val eventRepo = EventRepo(bangkitAPI, eventDatabase, sharedPreferences)
        val viewModelProviderFactory = EventViewModelProviderFactory(eventRepo)
        viewModel = ViewModelProvider(this, viewModelProviderFactory)[EventViewModel::class.java]

        // Mendapatkan eventId dari argumen
        eventId = args.eventId
        binding.progressBar.visibility = View.VISIBLE
        binding.fab.isEnabled = false

        // Ambil detail event dari ViewModel
        viewModel.getEventDetail(eventId).observe(viewLifecycleOwner) { resource ->
            when (resource.status) {
                Status.LOADING -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                Status.SUCCESS -> {
                    binding.progressBar.visibility = View.GONE
                    val detailEvent = resource.data
                    detailEvent?.event?.let { event ->
                        binding.tvEventName.text = event.name
                        binding.tvEventDescription.text = Html.fromHtml(event.description, Html.FROM_HTML_MODE_LEGACY)
                        binding.tvEventDescription.movementMethod = LinkMovementMethod.getInstance()
                        binding.tvEventTimeAndLocation.text = getString(
                            R.string.event_time_and_location_format,
                            event.beginTime, event.endTime, event.cityName
                        )
                        binding.tvEventQuota.text = getString(
                            R.string.remaining_quota_format,
                            event.quota - event.registrants
                        )
                        Glide.with(this).load(event.mediaCover).into(binding.ivEventImage)
                        binding.tvEventOwner.text = getString(R.string.event_owner_format, event.ownerName)
                        binding.fab.isEnabled = true
                        binding.fab.setOnClickListener {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(event.link))
                            startActivity(intent)
                        }
                    }
                }
                Status.ERROR -> {
                    binding.progressBar.visibility = View.GONE
                }
            }
        }

        // Inisialisasi SharedPreferencesHelper dan ikon favorit
        sharedPreferencesHelper = SharedPreferencesHelper(requireContext())
        favoriteIcon = view.findViewById(R.id.iv_fav)

        // Cek apakah event ini sudah difavoritkan
        updateFavoriteIcon(sharedPreferencesHelper.isFavorite(eventId.toString()))

        // Tambah atau hapus event dari favorit saat icon favorit diklik
        favoriteIcon.setOnClickListener {
            val isFavorite = sharedPreferencesHelper.isFavorite(eventId.toString())
            if (isFavorite) {
                eventRepo.removeFavoriteEventId(eventId.toString())
            } else {
                eventRepo.addFavoriteEventId(eventId.toString())
            }
            updateFavoriteIcon(!isFavorite)  // Perbarui ikon
        }

    }

    // Fungsi untuk memperbarui ikon favorit
    private fun updateFavoriteIcon(isFavorite: Boolean) {
        if (isFavorite) {
            favoriteIcon.setImageResource(R.drawable.baseline_favorite_24) // Ikon event favorit
        } else {
            favoriteIcon.setImageResource(R.drawable.baseline_favorite_border_24) // Ikon belum favorit
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
