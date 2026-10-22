package com.example.trato_hecho.ui.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.trato_hecho.data.model.ClienteEntity
import com.example.trato_hecho.data.repository.ClienteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// ... (Mantén tu data class NuevoClienteState exactamente igual aquí arriba) ...
data class NuevoClienteState(
    val rut: String = "",
    val rutError: String? = null,
    val nombre: String = "",
    val nombreError: String? = null,
    val correo: String = "",
    val correoError: String? = null,
    val fotoBitmap: Bitmap? = null,
    val latitud: Double? = null, // <-- NUEVO
    val longitud: Double? = null,
    val isFormValid: Boolean = false
)

// Ahora el ViewModel recibe el Repositorio
class NuevoClienteViewModel(private val repository: ClienteRepository) : ViewModel() {

    private val _state = MutableStateFlow(NuevoClienteState())
    val state: StateFlow<NuevoClienteState> = _state.asStateFlow()

    fun onRutChanged(newRut: String) {
        val error = if (newRut.isBlank()) "El RUT es obligatorio" else null
        _state.update { it.copy(rut = newRut, rutError = error) }
        checkFormValidity()
    }

    fun onNombreChanged(newNombre: String) {
        val error = if (newNombre.length < 3) "Mínimo 3 caracteres" else null
        _state.update { it.copy(nombre = newNombre, nombreError = error) }
        checkFormValidity()
    }

    fun onCorreoChanged(newCorreo: String) {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[a-zA-Z]{2,}\$".toRegex()
        val error = if (newCorreo.isBlank()) {
            "El correo es obligatorio"
        } else if (!newCorreo.matches(emailRegex)) {
            "Formato de correo inválido"
        } else null

        _state.update { it.copy(correo = newCorreo, correoError = error) }
        checkFormValidity()
    }

    private fun checkFormValidity() {
        val currentState = _state.value
        val isValid = currentState.rut.isNotBlank() && currentState.rutError == null &&
                currentState.nombre.isNotBlank() && currentState.nombreError == null &&
                currentState.correo.isNotBlank() && currentState.correoError == null

        _state.update { it.copy(isFormValid = isValid) }
    }

    // NUEVA FUNCIÓN: Guarda el cliente en Room
    fun guardarCliente(onSuccess: () -> Unit) {
        viewModelScope.launch {
            val nuevoCliente = ClienteEntity(
                rut = _state.value.rut,
                nombre = _state.value.nombre,
                correo = _state.value.correo
            )
            repository.insertarCliente(nuevoCliente)

            // Limpiamos el formulario devolviéndolo a su estado original
            _state.value = NuevoClienteState()

            // Avisamos a la interfaz que se guardó con éxito
            onSuccess()
        }
    }

    // FUNCIÓN: Guarda la foto tomada
    fun onFotoTomada(bitmap: Bitmap?) {
        _state.update { it.copy(fotoBitmap = bitmap) }
    }

    fun onUbicacionObtenida(lat: Double, lon: Double) {
        _state.update { it.copy(latitud = lat, longitud = lon) }
    }
}

// Necesitamos un "Factory" para decirle a Android cómo construir este ViewModel
// ahora que requiere un parámetro (el Repository).
class NuevoClienteViewModelFactory(private val repository: ClienteRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NuevoClienteViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NuevoClienteViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}