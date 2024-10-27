package com.example.androidfundamental1.repo

import android.content.SharedPreferences
import com.example.androidfundamental1.api.EventAPI
import com.example.androidfundamental1.db.EventDatabase
import com.example.androidfundamental1.models.EventList
import com.example.androidfundamental1.models.DetailEvent
import com.example.androidfundamental1.models.Events
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class EventRepo(
    private val eventAPI: EventAPI,
    private val eventDatabase: EventDatabase,
    private val sharedPreferences: SharedPreferences
) {

    // Mendapatkan semua event yang akan datang
    suspend fun getUpcomingEvents(active: Int = 1): EventList? {
        return fetchEvents { eventAPI.getUpcomingEvents(active) }
    }

    // Mendapatkan semua event yang sudah selesai
    suspend fun getPastEvents(active: Int = 0): EventList? {
        return fetchEvents { eventAPI.getPastEvents(active) }
    }

    // Mencari event berdasarkan keyword untuk past events
    suspend fun searchPastEvents(query: String, active: Int = 0): EventList? {
        return fetchEvents { eventAPI.searchEvents(active, query) }
    }

    // Mendapatkan detail event berdasarkan ID
    suspend fun getEventDetail(eventId: Int): DetailEvent? {
        var detailEvent = eventDatabase.getEventDao().getDetailEventById(eventId)
        if (detailEvent == null) {
            detailEvent = withContext(Dispatchers.IO) {
                try {
                    eventAPI.getEventDetail(eventId)
                } catch (e: Exception) {
                    null
                }
            }
            detailEvent?.let { eventDatabase.getEventDao().upsert(it) }
        }
        return detailEvent
    }

    // Mendapatkan ID dari event favorit
    fun getFavoriteEventIds(): Set<String> {
        return sharedPreferences.getStringSet("favorite_events", emptySet()) ?: emptySet()
    }

    // Mengambil event berdasarkan ID dari API atau database
    suspend fun getEventsByIds(eventIds: Set<String>): List<Events> {
        return withContext(Dispatchers.IO) {
            eventIds.mapNotNull { id ->
                try {
                    eventAPI.getEventDetail(id.toInt()).event // Ambil detail dari API atau gunakan database
                } catch (e: Exception) {
                    null
                }
            }
        }
    }

    // Menambahkan ID event favorit
    fun addFavoriteEventId(eventId: String) {
        val favorites = sharedPreferences.getStringSet("favorite_events", mutableSetOf())?.toMutableSet()
        favorites?.add(eventId)
        sharedPreferences.edit().putStringSet("favorite_events", favorites).apply()
    }

    // Menghapus ID event favorit
    fun removeFavoriteEventId(eventId: String) {
        val favorites = sharedPreferences.getStringSet("favorite_events", mutableSetOf())?.toMutableSet()
        favorites?.remove(eventId)
        sharedPreferences.edit().putStringSet("favorite_events", favorites).apply()
    }


    // Fungsi privat untuk mengambil data event dengan penanganan error
    private suspend fun fetchEvents(fetch: suspend () -> EventList?): EventList? {
        return withContext(Dispatchers.IO) {
            try {
                fetch()
            } catch (e: Exception) {
                null
            }
        }
    }
}
