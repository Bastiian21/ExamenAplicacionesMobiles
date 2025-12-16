package com.example.diariodeviaje.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.diariodeviaje.data.local.UserPreferences
import com.example.diariodeviaje.data.model.Actividad
import com.example.diariodeviaje.data.network.BackendClient
import com.example.diariodeviaje.data.network.UsuarioResponse
import com.example.diariodeviaje.data.repository.ActividadRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: ActividadRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _filtro = MutableStateFlow("")

    @OptIn(ExperimentalCoroutinesApi::class)
    val viajes: StateFlow<List<Actividad>> = userPreferences.userId
        .flatMapLatest { userId ->
            // IMPORTANTE: Aquí pasamos el ID del usuario al repositorio
            repository.getAllByUserId(userId ?: 0L)
        }
        .combine(_filtro) { lista, filtro ->
            if (filtro.isBlank()) {
                lista
            } else {
                lista.filter {
                    it.titulo.contains(filtro, ignoreCase = true) ||
                            it.direccion?.contains(filtro, ignoreCase = true) == true
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _todosLosUsuarios = MutableStateFlow<List<UsuarioResponse>>(emptyList())
    private val _usuariosFiltrados = MutableStateFlow<List<UsuarioResponse>>(emptyList())
    val usuarios: StateFlow<List<UsuarioResponse>> = _usuariosFiltrados

    init {
        cargarUsuariosDelBackend()
    }

    private fun cargarUsuariosDelBackend() {
        viewModelScope.launch {
            try {
                val listaReal = BackendClient.service.obtenerUsuarios()
                _todosLosUsuarios.value = listaReal
                _usuariosFiltrados.value = emptyList()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun filtrar(texto: String) {
        _filtro.value = texto
        if (texto.isBlank()) {
            _usuariosFiltrados.value = emptyList()
        } else {
            _usuariosFiltrados.value = _todosLosUsuarios.value.filter {
                it.nombre.contains(texto, ignoreCase = true) ||
                        it.email.contains(texto, ignoreCase = true)
            }
        }
    }
}