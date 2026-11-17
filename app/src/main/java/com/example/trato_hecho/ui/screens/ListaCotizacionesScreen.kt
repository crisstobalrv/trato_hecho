package com.example.trato_hecho.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.trato_hecho.data.model.CotizacionEntity
import com.example.trato_hecho.ui.components.TratoHechoTopBar
import com.example.trato_hecho.ui.viewmodel.ListaCotizacionesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaCotizacionesScreen(
    viewModel: ListaCotizacionesViewModel,
    onNavigateToNuevaCotizacion: () -> Unit,
    onNavigateToEditarCotizacion: (Int) -> Unit,
    onNavigateBack: () -> Unit
) {
    val cotizaciones by viewModel.cotizaciones.collectAsState()
    val context = LocalContext.current

    var cotizacionAEnviar by remember { mutableStateOf<CotizacionEntity?>(null) }

    cotizacionAEnviar?.let { cotizacion ->
        AlertDialog(
            onDismissRequest = { cotizacionAEnviar = null },
            title = { Text("Confirmar envío") },
            text = {
                Text("¿Estás seguro que quieres enviar esta cotización al cliente RUT ${cotizacion.clienteRut}? Una vez enviada no podrá editarse.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.marcarComoEnviada(cotizacion)
                        Toast.makeText(context, "Cotización marcada como Enviada", Toast.LENGTH_SHORT).show()
                        cotizacionAEnviar = null
                    }
                ) {
                    Text("Sí, enviar")
                }
            },
            dismissButton = {
                TextButton(onClick = { cotizacionAEnviar = null }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TratoHechoTopBar(
                titulo = "Cotizaciones",
                onNavigateBack = onNavigateBack
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToNuevaCotizacion) {
                Icon(Icons.Filled.Add, contentDescription = "Nueva Cotización")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                text = "Presupuestos Registrados",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            if (cotizaciones.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay cotizaciones registradas.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(cotizaciones) { cotizacion ->
                        val esBorrador = cotizacion.estado == "Borrador"

                        Card(
                            onClick = {
                                if (esBorrador) {
                                    onNavigateToEditarCotizacion(cotizacion.id)
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Las cotizaciones enviadas no se pueden editar",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.ReceiptLong,
                                    contentDescription = null,
                                    modifier = Modifier.size(40.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = "Cliente RUT: ${cotizacion.clienteRut}", fontWeight = FontWeight.Bold)
                                    Text(text = "Total: ${cotizacion.total} ${cotizacion.moneda}", style = MaterialTheme.typography.bodyMedium)
                                    Text(
                                        text = "Fecha: ${cotizacion.fecha} | Estado: ${cotizacion.estado}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = if (!esBorrador) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    if (esBorrador) {
                                        Text(
                                            text = "Toca la tarjeta para editar",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.secondary
                                        )
                                    }
                                }

                                if (esBorrador) {
                                    FilledTonalButton(
                                        onClick = { cotizacionAEnviar = cotizacion }
                                    ) {
                                        Icon(Icons.Filled.Send, contentDescription = "Enviar", modifier = Modifier.size(16.dp))
                                        Spacer(Modifier.width(4.dp))
                                        Text("Enviar")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}