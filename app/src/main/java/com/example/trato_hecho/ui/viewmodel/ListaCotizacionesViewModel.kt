package com.example.trato_hecho.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.trato_hecho.data.model.CotizacionEntity
import com.example.trato_hecho.data.repository.CotizacionRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ListaCotizacionesViewModel(private val repository: CotizacionRepository) : ViewModel() {

    val cotizaciones: StateFlow<List<CotizacionEntity>> = repository.obtenerCotizaciones()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun marcarComoEnviada(cotizacion: CotizacionEntity) {
        viewModelScope.launch {
            val cotizacionActualizada = cotizacion.copy(estado = "Enviada")
            repository.actualizarCotizacion(cotizacionActualizada)
        }
    }
}

class ListaCotizacionesViewModelFactory(private val repository: CotizacionRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ListaCotizacionesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ListaCotizacionesViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}