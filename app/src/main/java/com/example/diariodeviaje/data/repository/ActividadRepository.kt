package com.example.diariodeviaje.data.repository

import com.example.diariodeviaje.data.local.ActividadDao
import com.example.diariodeviaje.data.network.LocationApiService
import com.example.diariodeviaje.data.model.Actividad
import kotlinx.coroutines.flow.Flow

class ActividadRepository(
    private val actividadDao: ActividadDao,
    private val locationService: LocationApiService
) {

    val allActividades: Flow<List<Actividad>> = actividadDao.getAll()

    fun getAllByUserId(userId: Long): Flow<List<Actividad>> {
        return actividadDao.getAllByUserId(userId)
    }

    fun buscarPorUsuario(userId: Long, query: String): Flow<List<Actividad>> {
        return actividadDao.buscarPorUsuario(userId, query)
    }

    suspend fun insert(actividad: Actividad) {
        actividadDao.insert(actividad)
    }

    suspend fun getById(id: Long): Actividad? {
        return actividadDao.getById(id)
    }

    suspend fun borrarTodoLocal() {
        actividadDao.deleteAll()
    }

    suspend fun obtenerDireccion(lat: Double, lon: Double): String {
        return try {
            val response = locationService.getDireccion(lat, lon)
            response.display_name ?: "Dirección desconocida"
        } catch (e: Exception) {
            "Sin conexión"
        }
    }
}