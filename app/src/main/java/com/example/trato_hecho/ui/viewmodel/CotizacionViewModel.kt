package com.example.trato_hecho.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.trato_hecho.data.model.CotizacionEntity
import com.example.trato_hecho.data.repository.CotizacionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class CotizacionState(
    val id: Int? = null,
    val clienteRut: String = "",
    val clienteRutError: String? = null,
    val moneda: String = "CLP",
    val subtotalStr: String = "",
    val subtotalError: String? = null,
    val impuestoPorcentaje: Double = 19.0,
    val isFormValid: Boolean = false
)

class CotizacionViewModel(private val repository: CotizacionRepository) : ViewModel() {

    private val _state = MutableStateFlow(CotizacionState())
    val state: StateFlow<CotizacionState> = _state.asStateFlow()

    fun cargarCotizacion(id: Int) {
        if (id == -1 || _state.value.id == id) return
        viewModelScope.launch {
            val cotizacion = repository.obtenerCotizacionPorId(id)
            if (cotizacion != null) {
                val subtotalLimpio = if (cotizacion.subtotal % 1.0 == 0.0) {
                    cotizacion.subtotal.toLong().toString()
                } else {
                    cotizacion.subtotal.toString()
                }
                _state.update {
                    it.copy(
                        id = cotizacion.id,
                        clienteRut = cotizacion.clienteRut,
                        clienteRutError = null,
                        moneda = cotizacion.moneda,
                        subtotalStr = subtotalLimpio,
                        subtotalError = null
                    )
                }
                validarFormulario()
            }
        }
    }

    fun onClienteRutChanged(rut: String) {
        val error = if (rut.isBlank()) "El RUT del cliente es obligatorio" else null
        _state.update { it.copy(clienteRut = rut, clienteRutError = error) }
        validarFormulario()
    }

    fun onMonedaChanged(moneda: String) {
        _state.update { it.copy(moneda = moneda) }
    }

    fun onSubtotalChanged(subtotal: String) {
        val num = subtotal.toDoubleOrNull()
        val error = when {
            subtotal.isBlank() -> "El monto subtotal es obligatorio"
            num == null -> "Ingrese un número válido"
            num <= 0.0 -> "El monto debe ser mayor a 0"
            else -> null
        }
        _state.update { it.copy(subtotalStr = subtotal, subtotalError = error) }
        validarFormulario()
    }

    private fun validarFormulario() {
        val current = _state.value
        val subtotalNum = current.subtotalStr.toDoubleOrNull() ?: 0.0
        val isValid = current.clienteRut.isNotBlank() && current.clienteRutError == null &&
                subtotalNum > 0.0 && current.subtotalError == null
        _state.update { it.copy(isFormValid = isValid) }
    }

    fun guardarCotizacion(onSuccess: () -> Unit) {
        viewModelScope.launch {
            val current = _state.value
            val subtotal = current.subtotalStr.toDoubleOrNull() ?: 0.0
            val ivaVigente = com.example.trato_hecho.data.local.SessionManager.ivaPorcentaje.value
            val impuestoMonto = subtotal * (ivaVigente / 100.0)
            val total = subtotal + impuestoMonto
            val fechaActual = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

            if (current.id != null) {
                val cotizacionActualizada = CotizacionEntity(
                    id = current.id,
                    clienteRut = current.clienteRut,
                    fecha = fechaActual,
                    moneda = current.moneda,
                    subtotal = subtotal,
                    impuesto = impuestoMonto,
                    total = total,
                    estado = "Borrador"
                )
                repository.actualizarCotizacion(cotizacionActualizada)
            } else {
                val nuevaCotizacion = CotizacionEntity(
                    clienteRut = current.clienteRut,
                    fecha = fechaActual,
                    moneda = current.moneda,
                    subtotal = subtotal,
                    impuesto = impuestoMonto,
                    total = total,
                    estado = "Borrador"
                )
                repository.guardarCotizacion(nuevaCotizacion)
            }

            _state.value = CotizacionState()
            onSuccess()
        }
    }
}

class CotizacionViewModelFactory(private val repository: CotizacionRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CotizacionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CotizacionViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}