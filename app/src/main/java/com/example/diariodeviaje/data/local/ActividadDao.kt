package com.example.diariodeviaje.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.diariodeviaje.data.model.Actividad
import kotlinx.coroutines.flow.Flow

@Dao
interface ActividadDao {
    @Query("SELECT * FROM actividades WHERE usuarioId = :userId ORDER BY id DESC")
    fun getAllByUserId(userId: Long): Flow<List<Actividad>>

    @Query("SELECT * FROM actividades WHERE usuarioId = :userId AND (titulo LIKE '%' || :query || '%' OR descripcion LIKE '%' || :query || '%')")
    fun buscarPorUsuario(userId: Long, query: String): Flow<List<Actividad>>

    @Query("SELECT * FROM actividades ORDER BY id DESC")
    fun getAll(): Flow<List<Actividad>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(actividad: Actividad)

    @Query("SELECT * FROM actividades WHERE id = :id")
    suspend fun getById(id: Long): Actividad?

    @Query("DELETE FROM actividades")
    suspend fun deleteAll()
}