package com.example.trato_hecho.data.remote

import com.example.trato_hecho.data.model.IndicadoresResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface RetrofitService {

    @GET("api")
    suspend fun obtenerIndicadores(): IndicadoresResponse
}

object RetrofitClient {
    private const val BASE_URL = "https://mindicador.cl/"

    val instance: RetrofitService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RetrofitService::class.java)
    }
}