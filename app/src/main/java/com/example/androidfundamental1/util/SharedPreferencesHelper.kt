package com.example.androidfundamental1.util

import android.content.Context

class SharedPreferencesHelper(context: Context) {
    private val sharedPreferences = context.getSharedPreferences("favorites_prefs", Context.MODE_PRIVATE)

    // Menyimpan event sebagai favorit menggunakan ID event
    fun saveFavorite(eventId: String) {
        val editor = sharedPreferences.edit()
        val favorites = getFavoriteIds().toMutableSet()
        favorites.add(eventId)  // Tambahkan ID event
        editor.putStringSet("favorite_events", favorites)
        editor.apply()
    }

    // Menghapus event dari favorit menggunakan ID event
    fun removeFavorite(eventId: String) {
        val editor = sharedPreferences.edit()
        val favorites = getFavoriteIds().toMutableSet()
        favorites.remove(eventId)  // Hapus ID event
        editor.putStringSet("favorite_events", favorites)
        editor.apply()
    }

    // Mengambil semua ID favorit yang tersimpan
    fun getFavoriteIds(): Set<String> {
        return sharedPreferences.getStringSet("favorite_events", emptySet()) ?: emptySet()
    }

    // Memeriksa apakah event ID tertentu difavoritkan
    fun isFavorite(eventId: String): Boolean {
        return getFavoriteIds().contains(eventId)
    }
}
