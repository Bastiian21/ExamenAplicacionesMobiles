package com.example.diariodeviaje.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.diariodeviaje.data.local.UserPreferences
import com.example.diariodeviaje.data.model.Actividad
import com.example.diariodeviaje.data.repository.ActividadRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val repository: ActividadRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    @OptIn(ExperimentalCoroutinesApi::class)
    val misActividades: StateFlow<List<Actividad>> = userPreferences.userId
        .flatMapLatest { id ->
            repository.getAllByUserId(id ?: 0)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun cerrarSesion() {
        viewModelScope.launch {
            userPreferences.logout()

        }
    }
}