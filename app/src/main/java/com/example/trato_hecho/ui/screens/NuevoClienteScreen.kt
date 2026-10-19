package com.example.trato_hecho.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import com.example.trato_hecho.ui.components.ValidatedTextField
import com.example.trato_hecho.ui.viewmodel.NuevoClienteViewModel

@Composable
fun NuevoClienteScreen(
    viewModel: NuevoClienteViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()

    // Controladores para mostrar el mensaje flotante (Snackbar)
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Registrar Cliente",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            ValidatedTextField(
                value = state.rut,
                onValueChange = { viewModel.onRutChanged(it) },
                label = "RUT del Cliente",
                errorMessage = state.rutError
            )

            Spacer(modifier = Modifier.height(8.dp))

            // ... (mismo código de arriba) ...
            ValidatedTextField(
                value = state.nombre,
                onValueChange = { viewModel.onNombreChanged(it) },
                label = "Nombre o Razón Social",
                errorMessage = state.nombreError
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Nuevo campo de Correo
            ValidatedTextField(
                value = state.correo,
                onValueChange = { viewModel.onCorreoChanged(it) },
                label = "Correo Electrónico",
                errorMessage = state.correoError
            )

            Spacer(modifier = Modifier.height(24.dp))
// ... (resto del código del botón) ...

            // Botón siempre visible, pero se habilita solo si es válido
            Button(
                onClick = {
                    // Llamamos a la función del ViewModel y le pasamos qué hacer cuando termine
                    viewModel.guardarCliente(
                        onSuccess = {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Cliente guardado en la base de datos local")
                            }
                        }
                    )
                },
                enabled = state.isFormValid,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar Cliente")
            }
        }
    }
}