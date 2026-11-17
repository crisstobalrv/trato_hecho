package com.example.trato_hecho.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.trato_hecho.data.model.CotizacionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CotizacionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertarCotizacion(cotizacion: CotizacionEntity)

    @Update
    fun actualizarCotizacion(cotizacion: CotizacionEntity)

    @Query("SELECT * FROM cotizaciones WHERE id = :id LIMIT 1")
    fun obtenerCotizacionPorId(id: Int): CotizacionEntity?

    @Query("SELECT * FROM cotizaciones")
    fun obtenerTodasLasCotizaciones(): Flow<List<CotizacionEntity>>
}