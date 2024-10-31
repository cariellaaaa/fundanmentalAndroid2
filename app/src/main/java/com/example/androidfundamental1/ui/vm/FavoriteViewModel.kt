package com.example.androidfundamental1.ui.vm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.androidfundamental1.db.EventDatabase
import com.example.androidfundamental1.models.Events
import com.example.androidfundamental1.models.FavoriteEntity
import com.example.androidfundamental1.repo.FavoriteRepository
import kotlinx.coroutines.launch

class FavoriteViewModel(application: Application) : AndroidViewModel(application) {
    private val favoriteDao = EventDatabase.getDatabase(application).getFavoriteEventDao()
    private val repository: FavoriteRepository = FavoriteRepository(favoriteDao) // Inisialisasi repository
    val allFavorites: LiveData<List<FavoriteEntity>> = repository.getAllFavorites()

    fun isFavorite(eventId: Int): LiveData<Boolean> {
        return repository.isFavorite(eventId) // Pastikan metode ini ada di repository
    }

    fun isFavoriteNow(eventId: Int): Boolean {
        return repository.isFavoriteNow(eventId) // Pastikan metode ini juga ada
    }

    fun addFavorite(favorite: FavoriteEntity) {
        viewModelScope.launch {
            repository.addFavorite(favorite) // Tambahkan ke repository
        }
    }

    fun removeFavorite(favorite: FavoriteEntity) {
        viewModelScope.launch {
            repository.removeFavorite(favorite) // Hapus dari repository
        }
    }
}
