package com.example.diariodeviaje.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.diariodeviaje.data.TempData
import com.example.diariodeviaje.data.local.UserPreferences
import com.example.diariodeviaje.data.model.Actividad
import com.example.diariodeviaje.data.network.ActividadNetwork
import com.example.diariodeviaje.data.network.BackendClient
import com.example.diariodeviaje.data.repository.ActividadRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SaveUiState(
    val titulo: String = "",
    val descripcion: String = "",
    val fotoUri: String? = null,
    val direccionInicio: String = "Cargando...",
    val direccionFin: String = "Cargando..."
)

class SaveViewModel(
    private val repository: ActividadRepository,
    private val userPreferences: UserPreferences,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _navegacionExitosa = Channel<Unit>()
    val navegacionExitosa = _navegacionExitosa.receiveAsFlow()

    val distanciaKm: Double = savedStateHandle.get<Float>("distancia")?.toDouble() ?: 0.0
    val tiempoSegundos: Long = savedStateHandle.get<Long>("tiempo") ?: 0L

    private val latInicio: Double = savedStateHandle.get<Float>("lat_inicio")?.toDouble() ?: 0.0
    private val lonInicio: Double = savedStateHandle.get<Float>("lon_inicio")?.toDouble() ?: 0.0
    private val latFin: Double = savedStateHandle.get<Float>("lat_fin")?.toDouble() ?: 0.0
    private val lonFin: Double = savedStateHandle.get<Float>("lon_fin")?.toDouble() ?: 0.0

    private val _uiState = MutableStateFlow(SaveUiState())
    val uiState: StateFlow<SaveUiState> = _uiState.asStateFlow()

    init {
        obtenerNombreUbicacion()
    }

    private fun obtenerNombreUbicacion() {
        viewModelScope.launch {
            var inicioTexto = "Lat: $latInicio, Lon: $lonInicio"
            var finTexto = "Lat: $latFin, Lon: $lonFin"

            try {
                val apiInicio = repository.obtenerDireccion(latInicio, lonInicio)
                val apiFin = repository.obtenerDireccion(latFin, lonFin)

                if (!apiInicio.contains("desconocida", ignoreCase = true)) inicioTexto = apiInicio
                if (!apiFin.contains("desconocida", ignoreCase = true)) finTexto = apiFin

            } catch (e: Exception) {
            }

            _uiState.update {
                it.copy(
                    direccionInicio = inicioTexto,
                    direccionFin = finTexto
                )
            }
        }
    }

    fun onTituloChange(titulo: String) {
        _uiState.update { it.copy(titulo = titulo) }
    }

    fun onDescripcionChange(descripcion: String) {
        _uiState.update { it.copy(descripcion = descripcion) }
    }

    fun onFotoChange(uri: String) {
        _uiState.update { it.copy(fotoUri = uri) }
    }

    fun guardarActividad() {
        if (_uiState.value.titulo.isBlank()) return

        val direccionCombinada = "${_uiState.value.direccionInicio} -> ${_uiState.value.direccionFin}"
        val rutaCompleta = TempData.rutaReciente

        viewModelScope.launch {
            val userId = userPreferences.userId.first() ?: 0L
            val emailUsuario = userPreferences.userEmail.first() ?: "anonimo@duoc.cl"

            val nuevaActividad = Actividad(
                usuarioId = userId,
                titulo = _uiState.value.titulo,
                descripcion = _uiState.value.descripcion,
                distanciaKm = distanciaKm,
                tiempoSegundos = tiempoSegundos,
                fotoUri = _uiState.value.fotoUri,
                direccion = direccionCombinada,
                latInicio = latInicio,
                lonInicio = lonInicio,
                latFin = latFin,
                lonFin = lonFin,
                rutaCompleta = rutaCompleta,
                fecha = System.currentTimeMillis()
            )

            repository.insert(nuevaActividad)
            println("✅ Guardado Local OK para usuario ID: $userId")

            try {
                println("📤 Intentando subir actividad de: $emailUsuario")

                val actividadNube = ActividadNetwork(
                    titulo = nuevaActividad.titulo,
                    descripcion = nuevaActividad.descripcion,
                    lugar = nuevaActividad.direccion ?: "Sin dirección",
                    usuarioEmail = emailUsuario,
                    distancia = "${nuevaActividad.distanciaKm} km",
                    tiempo = "${nuevaActividad.tiempoSegundos} seg"
                )

                BackendClient.service.guardarActividadRemota(actividadNube)
                println("🚀 ¡SUBIDA AL SERVIDOR EXITOSA!")

            } catch (e: Exception) {
                println("❌ ERROR AL SUBIR: ${e.message}")
                e.printStackTrace()
            }

            _navegacionExitosa.send(Unit)
            TempData.rutaReciente = null
        }
    }
}