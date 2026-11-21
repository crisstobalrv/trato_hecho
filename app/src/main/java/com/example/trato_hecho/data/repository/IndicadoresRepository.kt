package com.example.trato_hecho.data.repository

import com.example.trato_hecho.data.model.IndicadoresResponse
import com.example.trato_hecho.data.remote.RetrofitClient
import com.example.trato_hecho.data.remote.RetrofitService

class IndicadoresRepository(
    private val service: RetrofitService = RetrofitClient.instance
) {
    suspend fun obtenerIndicadores(): Result<IndicadoresResponse> {
        return try {
            val response = service.obtenerIndicadores()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}