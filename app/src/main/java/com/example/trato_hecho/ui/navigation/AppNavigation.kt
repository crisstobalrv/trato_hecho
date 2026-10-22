package com.example.trato_hecho.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.trato_hecho.data.local.TratoHechoDatabase
import com.example.trato_hecho.data.repository.ClienteRepository
import com.example.trato_hecho.ui.screens.ListaClientesScreen
import com.example.trato_hecho.ui.screens.NuevoClienteScreen
import com.example.trato_hecho.ui.viewmodel.ListaClientesViewModelFactory
import com.example.trato_hecho.ui.viewmodel.NuevoClienteViewModelFactory

@Composable
fun AppNavigation() {
    val navController = rememberNavController()


    // 1. Obtenemos el contexto y la BD una sola vez para toda la navegación
    val context = LocalContext.current
    val database = TratoHechoDatabase.getDatabase(context)
    val repository = ClienteRepository(database.clienteDao())

    // 2. Cambiamos el inicio a la Lista de Clientes
    NavHost(navController = navController, startDestination = Routes.Clientes.route) {

        composable(Routes.Home.route) {
            PlaceholderScreen(titulo = "Dashboard / Home")
        }

        composable(Routes.Clientes.route) {
            // Instanciamos el ViewModel de la Lista
            val viewModelFactory = ListaClientesViewModelFactory(repository)
            val viewModel: com.example.trato_hecho.ui.viewmodel.ListaClientesViewModel = viewModel(factory = viewModelFactory)

            ListaClientesScreen(
                viewModel = viewModel,
                onNavigateToNuevoCliente = {
                    navController.navigate(Routes.NuevoCliente.route)
                }
            )
        }

        composable(Routes.NuevoCliente.route) {
            // Instanciamos el ViewModel del Formulario
            val viewModelFactory = NuevoClienteViewModelFactory(repository)
            val viewModel: com.example.trato_hecho.ui.viewmodel.NuevoClienteViewModel = viewModel(factory = viewModelFactory)

            NuevoClienteScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() } // Agregamos esto para poder volver a la lista
            )
        }

        composable(Routes.Cotizaciones.route) {
            PlaceholderScreen(titulo = "Lista de Cotizaciones")
        }

        composable(Routes.NuevaCotizacion.route) {
            PlaceholderScreen(titulo = "Armar Nueva Cotización")
        }
    }
}

// Pantalla temporal para probar que la navegación no falla
@Composable
fun PlaceholderScreen(titulo: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = titulo)
    }
}