package com.example.androidfundamental1.repo

import androidx.lifecycle.LiveData
import com.example.androidfundamental1.db.FavoriteDao
import com.example.androidfundamental1.models.FavoriteEntity

class FavoriteRepository(private val favoriteDao: FavoriteDao) {

    fun getAllFavorites(): LiveData<List<FavoriteEntity>> = favoriteDao.getAllFavorites()

    suspend fun addFavorite(favorite: FavoriteEntity) {
        favoriteDao.insert(favorite)
    }

    suspend fun removeFavorite(favorite: FavoriteEntity) {
        favoriteDao.delete(favorite)
    }

    fun isFavorite(itemId: Int): LiveData<Boolean> = favoriteDao.isFavorite(itemId)

    // Fungsi sinkron untuk mendapatkan status favorit
    fun isFavoriteNow(favoriteId: Int): Boolean {
        return favoriteDao.isFavoriteNow(favoriteId)
    }
}
