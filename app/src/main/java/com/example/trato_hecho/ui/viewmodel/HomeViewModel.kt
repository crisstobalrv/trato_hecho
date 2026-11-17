package com.example.trato_hecho.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.trato_hecho.data.local.SessionManager
import com.example.trato_hecho.data.repository.ClienteRepository
import com.example.trato_hecho.data.repository.CotizacionRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class HomeState(
    val usuarioCorreo: String = "",
    val rolUsuario: String = "Vendedor",
    val ivaPorcentaje: Double = 19.0,
    val totalClientes: Int = 0,
    val totalCotizaciones: Int = 0,
    val cotizacionesBorrador: Int = 0,
    val cotizacionesEnviadas: Int = 0
)

class HomeViewModel(
    clienteRepository: ClienteRepository,
    cotizacionRepository: CotizacionRepository
) : ViewModel() {

    val state: StateFlow<HomeState> = combine(
        clienteRepository.obtenerClientes(),
        cotizacionRepository.obtenerCotizaciones(),
        SessionManager.usuarioCorreo,
        SessionManager.rolActivo,
        SessionManager.ivaPorcentaje
    ) { clientes, cotizaciones, correo, rol, iva ->
        HomeState(
            usuarioCorreo = correo,
            rolUsuario = rol,
            ivaPorcentaje = iva,
            totalClientes = clientes.size,
            totalCotizaciones = cotizaciones.size,
            cotizacionesBorrador = cotizaciones.count { it.estado == "Borrador" },
            cotizacionesEnviadas = cotizaciones.count { it.estado == "Enviada" }
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeState()
    )

    fun actualizarIva(nuevoIva: Double): Boolean {
        if (nuevoIva < 0.0 || nuevoIva > 100.0) return false
        SessionManager.actualizarIva(nuevoIva)
        return true
    }

    fun cerrarSesion() {
        SessionManager.cerrarSesion()
    }
}

class HomeViewModelFactory(
    private val clienteRepository: ClienteRepository,
    private val cotizacionRepository: CotizacionRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(clienteRepository, cotizacionRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}