package com.example.diariodeviaje.data.network

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("id") val id: Long,
    @SerializedName("email") val email: String,
    @SerializedName("nombre") val nombre: String?
)