package com.example.androidfundamental1.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_items")
data class FavoriteEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val date: String
)
