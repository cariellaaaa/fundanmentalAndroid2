package com.example.androidfundamental1.ui.main

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.androidfundamental1.R
import com.example.androidfundamental1.util.ThemeHelper
import com.example.androidfundamental1.worker.DailyReminderWorker
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var themeHelper: ThemeHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupDailyReminder()

        themeHelper = ThemeHelper(this)

        setupToolbarAndNavigation()

        // Set tema awal saat aplikasi dibuka
        setInitialTheme()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        updateThemeIcon(menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed() // Kembali ke layar sebelumnya
                true
            }
            R.id.themeToggle -> {
                toggleThemeMode() // Ubah mode tema
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupToolbarAndNavigation() {
        // Inisialisasi Toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Setup NavController
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.newsNavHostFragment) as NavHostFragment
        val navController = navHostFragment.navController

        // Hubungkan BottomNavigationView dengan NavController
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.setupWithNavController(navController)
    }

    override fun onBackPressed() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.newsNavHostFragment) as NavHostFragment
        val navController = navHostFragment.navController

        if (!navController.navigateUp()) {
            super.onBackPressed()
        }
    }

    private fun setInitialTheme() {
        val currentMode = themeHelper.getThemeMode()
        themeHelper.setThemeMode(currentMode)
    }

    private fun toggleThemeMode() {
        val currentMode = themeHelper.getThemeMode()
        val newMode = if (currentMode == ThemeHelper.MODE_NIGHT_YES) ThemeHelper.MODE_NIGHT_NO else ThemeHelper.MODE_NIGHT_YES
        themeHelper.setThemeMode(newMode)

        // Refresh ikon setelah mode berubah
        invalidateOptionsMenu()
    }

    private fun updateThemeIcon(menu: Menu?) {
        val themeItem = menu?.findItem(R.id.themeToggle)
        val currentMode = themeHelper.getThemeMode()

        // Ganti ikon berdasarkan mode tema saat ini
        themeItem?.icon = if (currentMode == ThemeHelper.MODE_NIGHT_YES) {
            getDrawable(R.drawable.baseline_dark_mode_24)
        } else {
            getDrawable(R.drawable.baseline_brightness_4_24)
        }
    }

    private fun setupDailyReminder() {
        val workManager = WorkManager.getInstance(this)
        val dailyReminderRequest = PeriodicWorkRequestBuilder<DailyReminderWorker>(1, TimeUnit.DAYS)
            .build()

        workManager.enqueueUniquePeriodicWork(
            "DailyReminderWork",
            ExistingPeriodicWorkPolicy.REPLACE,
            dailyReminderRequest
        )
    }
}
