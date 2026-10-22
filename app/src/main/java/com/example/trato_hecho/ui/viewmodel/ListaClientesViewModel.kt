package com.example.trato_hecho.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.trato_hecho.data.model.ClienteEntity
import com.example.trato_hecho.data.repository.ClienteRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class ListaClientesViewModel(repository: ClienteRepository) : ViewModel() {

    // Convertimos el Flow de Room en un StateFlow que la vista puede observar.
    // Si la base de datos cambia, la lista se actualiza sola.
    val clientes: StateFlow<List<ClienteEntity>> = repository.obtenerClientes()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}

// Factory para construir este ViewModel
class ListaClientesViewModelFactory(private val repository: ClienteRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ListaClientesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ListaClientesViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}