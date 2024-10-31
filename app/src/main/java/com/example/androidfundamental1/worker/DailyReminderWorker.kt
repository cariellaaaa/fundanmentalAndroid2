package com.example.androidfundamental1.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.core.app.NotificationCompat
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.androidfundamental1.R
import com.example.androidfundamental1.api.EventAPI
import com.example.androidfundamental1.api.RetrofitInstance
import com.example.androidfundamental1.db.EventDatabase
import com.example.androidfundamental1.repo.EventRepo

class DailyReminderWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    private val bangkitAPI: EventAPI = RetrofitInstance.api
    private val eventDatabase: EventDatabase = EventDatabase.getDatabase(appContext)
    private val sharedPreferences: SharedPreferences = appContext.getSharedPreferences("FavoritesPrefs", Context.MODE_PRIVATE)

    private val eventRepository = EventRepo(bangkitAPI, eventDatabase, sharedPreferences)

    override suspend fun doWork(): Result {
        Log.d("DailyReminderWorker", "Running Worker")

        return try {
            val events = eventRepository.getUpcomingEvents(active = 1)
            Log.d("DailyReminderWorker", "Event berhasil diambil: ${events?.listEvents?.size}") // Log untuk melihat jumlah event yang diambil

            if (events != null && events.listEvents.isNotEmpty()) {
                val nearestEvent = events.listEvents.first()
                showNotification(nearestEvent.name, nearestEvent.beginTime)
                Log.d("DailyReminderWorker", "Notifikasi berhasil ditampilkan untuk event: ${nearestEvent.name}") // Log setelah notifikasi ditampilkan
            } else {
                Log.d("DailyReminderWorker", "Tidak ada event yang ditemukan.")
            }
            Result.success()
        } catch (e: Exception) {
            Log.e("DailyReminderWorker", "Gagal menampilkan notifikasi: ${e.message}")
            Result.failure()
        }
    }

    private fun showNotification(eventName: String, eventDate: String) {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "event_channel",
                "Event Notification",
                NotificationManager.IMPORTANCE_HIGH // Ubah dari IMPORTANCE_DEFAULT ke IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
            Log.d("DailyReminderWorker", "NotificationChannel dibuat") // Log untuk memastikan NotificationChannel dibuat
        }

        val notification = NotificationCompat.Builder(applicationContext, "event_channel")
            .setContentTitle("Upcoming Event Reminder")
            .setContentText("$eventName on $eventDate")
            .setSmallIcon(R.drawable.baseline_notifications_24)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(1, notification)
        Log.d("DailyReminderWorker", "Notifikasi dikirim untuk event: $eventName")
    }
}
