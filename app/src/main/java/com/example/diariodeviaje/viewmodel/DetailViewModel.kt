package com.example.diariodeviaje.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.diariodeviaje.data.model.Actividad
import com.example.diariodeviaje.data.repository.ActividadRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DetailViewModel(
    private val repository: ActividadRepository,
    private val actividadId: Long
) : ViewModel() {

    private val _actividad = MutableStateFlow<Actividad?>(null)
    val actividad: StateFlow<Actividad?> = _actividad.asStateFlow()

    init {
        obtenerActividad()
    }

    private fun obtenerActividad() {
        viewModelScope.launch {
            val resultado = repository.getById(actividadId)
            _actividad.value = resultado
        }
    }
}