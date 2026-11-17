package com.example.trato_hecho.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cotizaciones")
data class CotizacionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val clienteRut: String,          // A qué cliente va dirigida
    val fecha: String,               // Fecha de emisión
    val moneda: String,              // CLP, USD, UF
    val subtotal: Double,
    val impuesto: Double,            // Porcentaje o monto de impuesto parametrizado
    val total: Double,
    val estado: String = "Borrador"   // Borrador, Enviada, Aceptada, etc.
)