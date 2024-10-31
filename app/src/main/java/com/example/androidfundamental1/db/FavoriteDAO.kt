package com.example.androidfundamental1.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.androidfundamental1.models.FavoriteEntity

@Dao
interface FavoriteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: FavoriteEntity)

    @Delete
    suspend fun delete(item: FavoriteEntity)

    @Query("SELECT * FROM favorite_items")
    fun getAllFavorites(): LiveData<List<FavoriteEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_items WHERE id = :itemId)")
    fun isFavorite(itemId: Int): LiveData<Boolean>

    // Metode sinkron untuk status favorit
    @Query("SELECT EXISTS(SELECT 1 FROM favorite_items WHERE id = :itemId)")
    fun isFavoriteNow(itemId: Int): Boolean
}

