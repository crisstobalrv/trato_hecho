package com.example.trato_hecho.data.repository

import com.example.trato_hecho.data.local.CotizacionDao
import com.example.trato_hecho.data.model.CotizacionEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class CotizacionRepository(private val cotizacionDao: CotizacionDao) {

    suspend fun guardarCotizacion(cotizacion: CotizacionEntity) {
        withContext(Dispatchers.IO) {
            cotizacionDao.insertarCotizacion(cotizacion)
        }
    }

    suspend fun actualizarCotizacion(cotizacion: CotizacionEntity) {
        withContext(Dispatchers.IO) {
            cotizacionDao.actualizarCotizacion(cotizacion)
        }
    }

    suspend fun obtenerCotizacionPorId(id: Int): CotizacionEntity? {
        return withContext(Dispatchers.IO) {
            cotizacionDao.obtenerCotizacionPorId(id)
        }
    }

    fun obtenerCotizaciones(): Flow<List<CotizacionEntity>> {
        return cotizacionDao.obtenerTodasLasCotizaciones()
    }
}