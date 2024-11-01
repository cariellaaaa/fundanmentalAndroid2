package com.example.androidfundamental1.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.androidfundamental1.models.FavoriteEntity

@Database(entities = [FavoriteEntity::class], version = 2, exportSchema = false)
abstract class AppDatabase: RoomDatabase() {
//    abstract fun favoriteDao(): FavoriteDao
    abstract fun getFavoriteEventDao(): FavoriteDao // Tambahkan DAO untuk Favorite
    //cobak uninstall dl appnya yg di hpmu
    //okk kak4/, mau coba running dgn hpku ya kak?4//masukin ke favoritenya gmn?

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null



        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
