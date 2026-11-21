package com.example.trato_hecho.data.model

data class IndicadoresResponse(
    val fecha: String = "",
    val uf: IndicadorDetalle? = null,
    val dolar: IndicadorDetalle? = null
)

data class IndicadorDetalle(
    val codigo: String = "",
    val nombre: String = "",
    val unidad_medida: String = "",
    val fecha: String = "",
    val valor: Double = 0.0
)