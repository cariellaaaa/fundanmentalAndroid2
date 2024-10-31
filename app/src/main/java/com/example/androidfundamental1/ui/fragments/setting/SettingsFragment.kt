package com.example.androidfundamental1.ui.fragments.setting

import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SwitchCompat
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.androidfundamental1.R
import com.example.androidfundamental1.worker.DailyReminderWorker
import java.util.concurrent.TimeUnit


class SettingsFragment : Fragment() {
    private lateinit var reminderSwitch: SwitchCompat
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPreferences = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

        reminderSwitch = view.findViewById(R.id.reminderSwitch)

        reminderSwitch.isChecked = isReminderEnabled()

        reminderSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // Periksa izin notifikasi
                if (isNotificationPermissionGranted()) {
                    saveReminderSetting(true)
                    scheduleDailyReminder()
                } else {
                    requestNotificationPermission()
                }
            } else {
                saveReminderSetting(false)
                cancelDailyReminder()
            }
        }

        // Memicu worker secara manual untuk tes
        testDailyReminderWorker()
    }

    private fun saveReminderSetting(isEnabled: Boolean) {
        sharedPreferences.edit().putBoolean("daily_reminder_enabled", isEnabled).apply()
    }

    private fun isReminderEnabled(): Boolean {
        return sharedPreferences.getBoolean("daily_reminder_enabled", false)
    }

    private fun scheduleDailyReminder() {
        val workRequest = PeriodicWorkRequestBuilder<DailyReminderWorker>(1, TimeUnit.DAYS)
            .build()

        WorkManager.getInstance(requireContext())
            .enqueueUniquePeriodicWork("DailyReminder",
                ExistingPeriodicWorkPolicy.UPDATE, workRequest)
    }

    private fun cancelDailyReminder() {
        WorkManager.getInstance(requireContext()).cancelUniqueWork("DailyReminder")
    }

    private fun testDailyReminderWorker() {
        val testWorkRequest = OneTimeWorkRequestBuilder<DailyReminderWorker>().build()
        WorkManager.getInstance(requireContext()).enqueue(testWorkRequest) // Memastikan worker dijalankan untuk testing
        Log.d("SettingsFragment", "Test worker dijalankan") // Log untuk memastikan worker test dijalankan
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun isNotificationPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun requestNotificationPermission() {
        if (shouldShowRequestPermissionRationale(android.Manifest.permission.POST_NOTIFICATIONS)) {
            // Tampilkan dialog penjelasan kepada pengguna jika diperlukan
            AlertDialog.Builder(requireContext())
                .setTitle("Izin Notifikasi Diperlukan")
                .setMessage("Izinkan aplikasi ini untuk mengirim notifikasi.")
                .setPositiveButton("OK") { _, _ ->
                    ActivityCompat.requestPermissions(requireActivity(), arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 1)
                }
                .setNegativeButton("Batal", null)
                .create()
                .show()
        } else {
            // Jika tidak ada penjelasan, langsung minta izin
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 1)
        }
    }
}