package com.example.androidfundamental1.models

data class EventList(
    val error: Boolean,
    val listEvents: MutableList<Events>,
    val message: String
)