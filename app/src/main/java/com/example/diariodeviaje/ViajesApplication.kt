package com.example.diariodeviaje

import android.app.Application
import com.example.diariodeviaje.data.local.AppDataBase
import com.example.diariodeviaje.data.local.LocationService
import com.example.diariodeviaje.data.local.UserPreferences
import com.example.diariodeviaje.data.network.LocationApiService
import com.example.diariodeviaje.data.repository.ActividadRepository
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ViajesApplication : Application() {

    private val database by lazy { AppDataBase.getDatabase(this) }

    private val locationApiClient by lazy {
        Retrofit.Builder()
            .baseUrl("https://nominatim.openstreetmap.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(LocationApiService::class.java)
    }

    val repository by lazy {
        ActividadRepository(
            database.actividadDao(),
            locationApiClient
        )
    }

    val userPreferences by lazy { UserPreferences(this) }

    val locationService by lazy { LocationService(this) }
}