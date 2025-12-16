package com.example.diariodeviaje.data.network

import com.example.diariodeviaje.data.model.LocationResponse
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface LocationApiService {
    @Headers("User-Agent: BTraxApp/1.0")
    @GET("reverse")
    suspend fun getDireccion(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("format") format: String = "json"
    ): LocationResponse
}