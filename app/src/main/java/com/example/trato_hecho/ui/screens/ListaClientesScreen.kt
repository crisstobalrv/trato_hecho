package com.example.trato_hecho.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RequestQuote
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.trato_hecho.ui.components.TratoHechoTopBar
import com.example.trato_hecho.ui.viewmodel.ListaClientesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaClientesScreen(
    viewModel: ListaClientesViewModel,
    onNavigateToNuevoCliente: () -> Unit,
    onNavigateToNuevaCotizacion: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val clientes by viewModel.clientes.collectAsState()

    Scaffold(
        topBar = {
            TratoHechoTopBar(
                titulo = "Gestión de Clientes",
                onNavigateBack = onNavigateBack,
                actions = {
                    IconButton(onClick = onNavigateToNuevaCotizacion) {
                        Icon(Icons.Filled.RequestQuote, contentDescription = "Ir a Cotizaciones")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToNuevoCliente) {
                Icon(Icons.Filled.Add, contentDescription = "Agregar Cliente")
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
                text = "Clientes Registrados",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            if (clientes.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay clientes registrados.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(clientes) { cliente ->
                        Card(
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
                                    imageVector = Icons.Filled.Person,
                                    contentDescription = null,
                                    modifier = Modifier.size(40.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = cliente.nombre, fontWeight = FontWeight.Bold)
                                    Text(text = "RUT: ${cliente.rut}", style = MaterialTheme.typography.bodyMedium)
                                    Text(text = cliente.correo, style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}