package com.example.trato_hecho.ui.viewmodel

import android.util.Patterns
import androidx.lifecycle.ViewModel
import com.example.trato_hecho.data.local.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class LoginState(
    val correo: String = "",
    val correoError: String? = null,
    val password: String = "",
    val passwordError: String? = null,
    val rolSeleccionado: String = "Vendedor", // "Vendedor" o "Administrador"
    val isFormValid: Boolean = false
)

class LoginViewModel : ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state.asStateFlow()

    fun onCorreoChanged(correo: String) {
        val error = when {
            correo.isBlank() -> "El correo es obligatorio"
            !Patterns.EMAIL_ADDRESS.matcher(correo).matches() -> "Formato de correo inválido"
            else -> null
        }
        _state.update { it.copy(correo = correo, correoError = error) }
        validarFormulario()
    }

    fun onPasswordChanged(password: String) {
        val error = when {
            password.isBlank() -> "La contraseña es obligatoria"
            password.length < 4 -> "Debe tener al menos 4 caracteres"
            else -> null
        }
        _state.update { it.copy(password = password, passwordError = error) }
        validarFormulario()
    }

    fun onRolChanged(rol: String) {
        _state.update { it.copy(rolSeleccionado = rol) }
    }

    private fun validarFormulario() {
        val current = _state.value
        val isValid = current.correo.isNotBlank() && current.correoError == null &&
                current.password.isNotBlank() && current.passwordError == null
        _state.update { it.copy(isFormValid = isValid) }
    }

    fun iniciarSesion(onSuccess: () -> Unit) {
        val current = _state.value
        if (!current.isFormValid) return

        SessionManager.iniciarSesion(
            correo = current.correo.trim(),
            rol = current.rolSeleccionado
        )
        _state.value = LoginState()
        onSuccess()
    }
}