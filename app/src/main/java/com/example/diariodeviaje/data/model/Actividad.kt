package com.example.diariodeviaje.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "actividades")
data class Actividad(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val usuarioId: Long,
    val titulo: String,
    val descripcion: String,
    val distanciaKm: Double,
    val tiempoSegundos: Long,
    val fotoUri: String?,
    val direccion: String?,
    val fecha: Long = System.currentTimeMillis(),
    val latInicio: Double = 0.0,
    val lonInicio: Double = 0.0,
    val latFin: Double = 0.0,
    val lonFin: Double = 0.0,
    val rutaCompleta: String? = null
)