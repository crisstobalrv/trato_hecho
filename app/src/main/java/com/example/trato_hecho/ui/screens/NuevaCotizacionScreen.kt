package com.example.trato_hecho.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.trato_hecho.ui.components.TratoHechoTopBar
import com.example.trato_hecho.ui.components.ValidatedTextField
import com.example.trato_hecho.ui.viewmodel.CotizacionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevaCotizacionScreen(
    viewModel: CotizacionViewModel = viewModel(),
    cotizacionId: Int = -1,
    onNavigateBack: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(cotizacionId) {
        if (cotizacionId != -1) {
            viewModel.cargarCotizacion(cotizacionId)
        }
    }

    val esEdicion = state.id != null

    Scaffold(
        topBar = {
            TratoHechoTopBar(
                titulo = if (esEdicion) "Editar Cotización" else "Nueva Cotización",
                onNavigateBack = onNavigateBack
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (esEdicion) "Modificar Propuesta" else "Armar Propuesta Comercial",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            ValidatedTextField(
                value = state.clienteRut,
                onValueChange = { viewModel.onClienteRutChanged(it) },
                label = "RUT del Cliente",
                errorMessage = state.clienteRutError
            )

            Spacer(modifier = Modifier.height(8.dp))

            ValidatedTextField(
                value = state.subtotalStr,
                onValueChange = { viewModel.onSubtotalChanged(it) },
                label = "Monto Subtotal",
                errorMessage = state.subtotalError,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Seleccionar Moneda",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.align(Alignment.Start)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("CLP", "USD", "UF").forEach { monedaOption ->
                    Button(
                        onClick = { viewModel.onMonedaChanged(monedaOption) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (state.moneda == monedaOption) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.secondary
                            }
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(monedaOption)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    val mensaje = if (esEdicion) "Cotización actualizada" else "Cotización guardada en borrador"
                    viewModel.guardarCotizacion(
                        onSuccess = {
                            Toast.makeText(context, mensaje, Toast.LENGTH_SHORT).show()
                            onNavigateBack()
                        }
                    )
                },
                enabled = state.isFormValid,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (esEdicion) "Actualizar Cotización" else "Guardar Cotización")
            }
        }
    }
}