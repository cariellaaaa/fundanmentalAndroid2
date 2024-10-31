package com.example.androidfundamental1.util

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate

class ThemeHelper(private val context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("settings", Context.MODE_PRIVATE)

    companion object {
        const val MODE_NIGHT_YES = "night"
        const val MODE_NIGHT_NO = "light"
        const val MODE_NIGHT_FOLLOW_SYSTEM = "follow_system"
    }

    fun getThemeMode(): String {
        return sharedPreferences.getString("theme_mode", MODE_NIGHT_FOLLOW_SYSTEM) ?: MODE_NIGHT_FOLLOW_SYSTEM
    }

    fun setThemeMode(mode: String) {
        val editor = sharedPreferences.edit()
        editor.putString("theme_mode", mode)
        editor.apply()

        // Set default night mode
        when (mode) {
            MODE_NIGHT_YES -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            MODE_NIGHT_NO -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            MODE_NIGHT_FOLLOW_SYSTEM -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
    }
}