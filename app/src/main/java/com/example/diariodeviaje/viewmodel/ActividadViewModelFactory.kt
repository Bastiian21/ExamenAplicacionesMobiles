package com.example.diariodeviaje.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.diariodeviaje.ViajesApplication
import com.example.diariodeviaje.data.local.LocationService
import com.example.diariodeviaje.data.local.UserPreferences
import com.example.diariodeviaje.data.repository.ActividadRepository

class ActividadViewModelFactory(
    private val repository: ActividadRepository,
    private val userPreferences: UserPreferences,
    private val application: ViajesApplication,
    private val locationService: LocationService
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {

        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")

            return HomeViewModel(repository, userPreferences) as T
        }

        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(userPreferences) as T
        }

        if (modelClass.isAssignableFrom(RecordViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RecordViewModel(
                repository = repository,
                application = application,
                locationService = locationService
            ) as T
        }

        if (modelClass.isAssignableFrom(SaveViewModel::class.java)) {
            val savedStateHandle = extras.createSavedStateHandle()
            @Suppress("UNCHECKED_CAST")
            return SaveViewModel(repository, userPreferences, savedStateHandle) as T
        }

        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProfileViewModel(repository, userPreferences) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}