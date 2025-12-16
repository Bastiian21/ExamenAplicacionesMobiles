package com.example.diariodeviaje.data.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object BackendClient {
    private const val BASE_URL = "http://10.0.2.2:8080/"

    val service: BackendService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(BackendService::class.java)
    }
}