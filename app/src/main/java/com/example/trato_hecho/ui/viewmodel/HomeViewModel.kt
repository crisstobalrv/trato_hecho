package com.example.trato_hecho.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.trato_hecho.data.local.SessionManager
import com.example.trato_hecho.data.repository.ClienteRepository
import com.example.trato_hecho.data.repository.CotizacionRepository
import com.example.trato_hecho.data.repository.IndicadoresRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HomeState(
    val usuarioCorreo: String = "",
    val rolUsuario: String = "Vendedor",
    val ivaPorcentaje: Double = 19.0,
    val totalClientes: Int = 0,
    val totalCotizaciones: Int = 0,
    val cotizacionesBorrador: Int = 0,
    val cotizacionesEnviadas: Int = 0,
    val valorUf: String = "Cargando...",
    val valorDolar: String = "Cargando...",
    val cargandoIndicadores: Boolean = false,
    val errorIndicadores: String? = null
)

private data class ApiIndicadoresState(
    val valorUf: String = "Cargando...",
    val valorDolar: String = "Cargando...",
    val cargando: Boolean = false,
    val error: String? = null
)

class HomeViewModel(
    clienteRepository: ClienteRepository,
    cotizacionRepository: CotizacionRepository,
    private val indicadoresRepository: IndicadoresRepository
) : ViewModel() {

    private val _apiState = MutableStateFlow(ApiIndicadoresState())

    private val sesionFlow = combine(
        SessionManager.usuarioCorreo,
        SessionManager.rolActivo,
        SessionManager.ivaPorcentaje
    ) { correo, rol, iva ->
        Triple(correo, rol, iva)
    }

    val state: StateFlow<HomeState> = combine(
        clienteRepository.obtenerClientes(),
        cotizacionRepository.obtenerCotizaciones(),
        sesionFlow,
        _apiState
    ) { clientes, cotizaciones, sesion, api ->
        HomeState(
            usuarioCorreo = sesion.first,
            rolUsuario = sesion.second,
            ivaPorcentaje = sesion.third,
            totalClientes = clientes.size,
            totalCotizaciones = cotizaciones.size,
            cotizacionesBorrador = cotizaciones.count { it.estado == "Borrador" },
            cotizacionesEnviadas = cotizaciones.count { it.estado == "Enviada" },
            valorUf = api.valorUf,
            valorDolar = api.valorDolar,
            cargandoIndicadores = api.cargando,
            errorIndicadores = api.error
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeState()
    )

    init {
        cargarIndicadores()
    }

    fun cargarIndicadores() {
        viewModelScope.launch {
            _apiState.value = _apiState.value.copy(cargando = true, error = null)
            val resultado = indicadoresRepository.obtenerIndicadores()
            resultado.onSuccess { data ->
                val ufTexto = data.uf?.valor?.let { "$$it CLP" } ?: "No disponible"
                val dolarTexto = data.dolar?.valor?.let { "$$it CLP" } ?: "No disponible"
                _apiState.value = ApiIndicadoresState(
                    valorUf = ufTexto,
                    valorDolar = dolarTexto,
                    cargando = false,
                    error = null
                )
            }.onFailure {
                _apiState.value = ApiIndicadoresState(
                    valorUf = "Sin conexión",
                    valorDolar = "Sin conexión",
                    cargando = false,
                    error = "No se pudieron obtener los indicadores económicos"
                )
            }
        }
    }

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
    private val cotizacionRepository: CotizacionRepository,
    private val indicadoresRepository: IndicadoresRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(clienteRepository, cotizacionRepository, indicadoresRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}