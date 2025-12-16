package com.example.diariodeviaje.data.network

import com.google.gson.annotations.SerializedName
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

data class ActividadNetwork(
    val id: Long = 0,
    val titulo: String,
    val descripcion: String,
    val lugar: String,
    @SerializedName("usuario_email") val usuarioEmail: String,
    val distancia: String,
    val tiempo: String
)

data class ComentarioRequest(
    val usuario: String,
    val mensaje: String
)

data class UsuarioResponse(
    val id: Long,
    val nombre: String,
    val email: String
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val nombre: String,
    val email: String,
    val password: String
)

interface BackendService {
    @POST("api/comentarios")
    suspend fun enviarComentario(@Body comentario: ComentarioRequest): ComentarioRequest

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): UsuarioResponse

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): UsuarioResponse

    @GET("api/auth/usuarios")
    suspend fun obtenerUsuarios(): List<UsuarioResponse>

    @POST("api/actividades")
    suspend fun guardarActividadRemota(@Body actividad: ActividadNetwork): ActividadNetwork

    @GET("api/actividades/usuario/{email}")
    suspend fun obtenerActividadesDeUsuario(@Path("email") email: String): List<ActividadNetwork>
}