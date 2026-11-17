package com.example.trato_hecho.data.local

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object SessionManager {

    private val _usuarioCorreo = MutableStateFlow("")
    val usuarioCorreo: StateFlow<String> = _usuarioCorreo.asStateFlow()

    private val _rolActivo = MutableStateFlow("Vendedor")
    val rolActivo: StateFlow<String> = _rolActivo.asStateFlow()

    private val _ivaPorcentaje = MutableStateFlow(19.0)
    val ivaPorcentaje: StateFlow<Double> = _ivaPorcentaje.asStateFlow()

    fun iniciarSesion(correo: String, rol: String) {
        _usuarioCorreo.value = correo
        _rolActivo.value = rol
    }

    fun cerrarSesion() {
        _usuarioCorreo.value = ""
        _rolActivo.value = "Vendedor"
    }

    fun actualizarIva(nuevoIva: Double) {
        _ivaPorcentaje.value = nuevoIva
    }
}