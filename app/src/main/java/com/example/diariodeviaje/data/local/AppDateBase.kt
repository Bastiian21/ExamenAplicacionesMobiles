package com.example.diariodeviaje.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.diariodeviaje.data.model.Actividad

@Database(entities = [Actividad::class], version = 2, exportSchema = false)
abstract class AppDataBase : RoomDatabase() {

    abstract fun actividadDao(): ActividadDao

    companion object {
        @Volatile
        private var INSTANCE: AppDataBase? = null

        fun getDatabase(context: Context): AppDataBase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDataBase::class.java,
                    "viajes_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}