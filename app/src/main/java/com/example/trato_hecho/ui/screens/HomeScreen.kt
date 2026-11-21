package com.example.trato_hecho.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.trato_hecho.ui.components.TratoHechoTopBar
import com.example.trato_hecho.ui.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToClientes: () -> Unit,
    onNavigateToNuevoCliente: () -> Unit,
    onNavigateToCotizaciones: () -> Unit,
    onNavigateToNuevaCotizacion: () -> Unit,
    onLogout: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    var ivaInput by remember(state.ivaPorcentaje) { mutableStateOf(state.ivaPorcentaje.toString()) }

    Scaffold(
        topBar = {
            TratoHechoTopBar(
                titulo = "Trato Hecho",
                actions = {
                    IconButton(
                        onClick = {
                            viewModel.cerrarSesion()
                            onLogout()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "Cerrar Sesión"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Identificador de sesión y perfil activo
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Perfil: ${state.rolUsuario}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    if (state.usuarioCorreo.isNotBlank()) {
                        Text(
                            text = state.usuarioCorreo,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    Text(
                        text = "Impuesto (IVA) vigente: ${state.ivaPorcentaje}%",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Tarjeta de API Externa (mindicador.cl vía Retrofit)
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.CurrencyExchange,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "Indicadores del Día (API)",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        IconButton(
                            onClick = { viewModel.cargarIndicadores() },
                            enabled = !state.cargandoIndicadores
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Refresh,
                                contentDescription = "Actualizar indicadores"
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Valor UF", style = MaterialTheme.typography.labelMedium)
                            Text(state.valorUf, fontWeight = FontWeight.Bold)
                        }
                        Column {
                            Text("Valor Dólar", style = MaterialTheme.typography.labelMedium)
                            Text(state.valorDolar, fontWeight = FontWeight.Bold)
                        }
                    }

                    state.errorIndicadores?.let { errorMsg ->
                        Text(
                            text = errorMsg,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            // Sección exclusiva para el perfil Administrador
            if (state.rolUsuario == "Administrador") {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Parámetros de Administrador",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = ivaInput,
                                onValueChange = { ivaInput = it },
                                label = { Text("IVA (%)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f)
                            )
                            Button(
                                onClick = {
                                    val valor = ivaInput.toDoubleOrNull()
                                    if (valor != null && viewModel.actualizarIva(valor)) {
                                        Toast.makeText(context, "IVA actualizado a $valor%", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, "Ingrese un porcentaje entre 0 y 100", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            ) {
                                Text("Guardar")
                            }
                        }
                    }
                }
            }

            Text(
                text = "Resumen Comercial",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                IndicadorCard(
                    titulo = "Clientes",
                    valor = state.totalClientes.toString(),
                    icono = Icons.Filled.Person,
                    modifier = Modifier.weight(1f)
                )
                IndicadorCard(
                    titulo = "Cotizaciones",
                    valor = state.totalCotizaciones.toString(),
                    icono = Icons.Filled.ReceiptLong,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                IndicadorCard(
                    titulo = "En Borrador",
                    valor = state.cotizacionesBorrador.toString(),
                    icono = Icons.Filled.ReceiptLong,
                    modifier = Modifier.weight(1f)
                )
                IndicadorCard(
                    titulo = "Enviadas",
                    valor = state.cotizacionesEnviadas.toString(),
                    icono = Icons.Filled.Send,
                    modifier = Modifier.weight(1f)
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

            Text(
                text = "Módulos de Gestión",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Button(
                onClick = onNavigateToClientes,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Filled.Person, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Gestionar Clientes")
            }

            Button(
                onClick = onNavigateToCotizaciones,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Filled.ReceiptLong, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Gestionar Cotizaciones")
            }

            Text(
                text = "Accesos Rápidos",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onNavigateToNuevoCliente,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Filled.Add, contentDescription = null)
                    Spacer(Modifier.width(4.dp))
                    Text("Cliente")
                }

                OutlinedButton(
                    onClick = onNavigateToNuevaCotizacion,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Filled.Add, contentDescription = null)
                    Spacer(Modifier.width(4.dp))
                    Text("Cotización")
                }
            }
        }
    }
}

@Composable
private fun IndicadorCard(
    titulo: String,
    valor: String,
    icono: ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Icon(
                imageVector = icono,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = valor,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = titulo,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}