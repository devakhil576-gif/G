package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [PoetryProjectEntity::class], version = 1, exportSchema = false)
abstract class PoetryDatabase : RoomDatabase() {
    abstract fun poetryDao(): PoetryDao

    companion object {
        @Volatile
        private var INSTANCE: PoetryDatabase? = null

        fun getInstance(context: Context): PoetryDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PoetryDatabase::class.java,
                    "sukhan_poetry.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
