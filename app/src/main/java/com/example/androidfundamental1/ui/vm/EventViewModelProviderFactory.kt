package com.example.androidfundamental1.ui.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.androidfundamental1.repo.EventRepo

class EventViewModelProviderFactory(
    private val eventRepo: EventRepo
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EventViewModel::class.java)) {
            return EventViewModel(eventRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
