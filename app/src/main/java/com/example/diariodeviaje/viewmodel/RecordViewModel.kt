package com.example.diariodeviaje.viewmodel

import android.app.Application
import android.location.Location
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.diariodeviaje.data.TempData
import com.example.diariodeviaje.data.local.LocationService
import com.example.diariodeviaje.data.repository.ActividadRepository
import com.example.diariodeviaje.navigation.AppScreen
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class EstadoGrabacion {
    GRABANDO,
    PAUSADO,
    DETENIDO
}

data class RecordUiState(
    val estado: EstadoGrabacion = EstadoGrabacion.DETENIDO,
    val tiempoSegundos: Long = 0L,
    val distanciaMetros: Double = 0.0,
    val ubicacionActual: Location? = null,
    val ubicacionAnterior: Location? = null
)

class RecordViewModel(
    private val repository: ActividadRepository,
    application: Application,
    private val locationService: LocationService
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(RecordUiState())
    val uiState: StateFlow<RecordUiState> = _uiState.asStateFlow()

    private val _navEvents = Channel<String>()
    val navEvents = _navEvents.receiveAsFlow()

    private var cronometroJob: Job? = null
    private var ubicacionInicio: Location? = null

    private val puntosRuta = mutableListOf<Location>()

    init {
        locationService.currentLocation
            .onEach { location ->
                _uiState.update { it.copy(ubicacionActual = location) }

                if (_uiState.value.estado == EstadoGrabacion.GRABANDO && location != null) {
                    if (ubicacionInicio == null) {
                        ubicacionInicio = location
                    }

                    puntosRuta.add(location)

                    actualizarDistancia(location)
                }
            }
            .launchIn(viewModelScope)
    }

    fun iniciarGrabacion() {
        if (_uiState.value.estado == EstadoGrabacion.DETENIDO) {
            ubicacionInicio = _uiState.value.ubicacionActual
            puntosRuta.clear()

            _uiState.value.ubicacionActual?.let { puntosRuta.add(it) }

            _uiState.update {
                it.copy(
                    estado = EstadoGrabacion.GRABANDO,
                    tiempoSegundos = 0L,
                    distanciaMetros = 0.0,
                    ubicacionAnterior = null,
                    ubicacionActual = null
                )
            }
            iniciarCronometro()
            locationService.startLocationUpdates()
        }
    }

    fun pausarGrabacion() {
        if (_uiState.value.estado == EstadoGrabacion.GRABANDO) {
            _uiState.update { it.copy(estado = EstadoGrabacion.PAUSADO) }
            cronometroJob?.cancel()
            locationService.stopLocationUpdates()
        }
    }

    fun reanudarGrabacion() {
        if (_uiState.value.estado == EstadoGrabacion.PAUSADO) {
            _uiState.update { it.copy(estado = EstadoGrabacion.GRABANDO) }
            iniciarCronometro()
            locationService.startLocationUpdates()
        }
    }

    fun detenerGrabacion() {
        val latFin = _uiState.value.ubicacionActual?.latitude ?: 0.0
        val lonFin = _uiState.value.ubicacionActual?.longitude ?: 0.0
        val latInicio = ubicacionInicio?.latitude ?: latFin
        val lonInicio = ubicacionInicio?.longitude ?: lonFin

        locationService.stopLocationUpdates()
        cronometroJob?.cancel()

        val rutaString = puntosRuta.joinToString(separator = ";") { loc ->
            "${loc.latitude},${loc.longitude}"
        }

        TempData.rutaReciente = rutaString

        val route = AppScreen.Save.createRoute(
            distancia = _uiState.value.distanciaMetros / 1000,
            tiempo = _uiState.value.tiempoSegundos,
            latInicio = latInicio,
            lonInicio = lonInicio,
            latFin = latFin,
            lonFin = lonFin
        )

        viewModelScope.launch {
            _navEvents.send(route)
        }

        _uiState.update { it.copy(estado = EstadoGrabacion.DETENIDO) }
    }

    private fun iniciarCronometro() {
        cronometroJob?.cancel()
        cronometroJob = viewModelScope.launch {
            while (_uiState.value.estado == EstadoGrabacion.GRABANDO) {
                delay(1000L)
                _uiState.update {
                    it.copy(tiempoSegundos = it.tiempoSegundos + 1)
                }
            }
        }
    }

    private fun actualizarDistancia(nuevaUbicacion: Location) {
        val ubicacionAnterior = _uiState.value.ubicacionAnterior

        if (ubicacionAnterior != null) {
            val distancia = ubicacionAnterior.distanceTo(nuevaUbicacion)
            _uiState.update {
                it.copy(
                    distanciaMetros = it.distanciaMetros + distancia,
                    ubicacionAnterior = nuevaUbicacion
                )
            }
        } else {
            _uiState.update { it.copy(ubicacionAnterior = nuevaUbicacion) }
        }
    }
}