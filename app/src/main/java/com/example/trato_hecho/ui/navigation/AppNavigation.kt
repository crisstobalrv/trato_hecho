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
import com.example.trato_hecho.ui.screens.NuevoClienteScreen
import com.example.trato_hecho.ui.viewmodel.NuevoClienteViewModelFactory

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.NuevoCliente.route) {

        composable(Routes.Home.route) {
            // Reemplazaremos esto con HomeScreen() más adelante
            PlaceholderScreen(titulo = "Dashboard / Home")
        }

        composable(Routes.Clientes.route) {
            PlaceholderScreen(titulo = "Lista de Clientes")
        }

        composable(Routes.NuevoCliente.route) {
            // 1. Obtenemos el contexto de Android
            val context = LocalContext.current

            // 2. Instanciamos la base de datos y el repositorio
            val database = TratoHechoDatabase.getDatabase(context)
            val repository = ClienteRepository(database.clienteDao())

            // 3. Creamos el ViewModel usando nuestra Factory
            val viewModelFactory = NuevoClienteViewModelFactory(repository)
            val viewModel: com.example.trato_hecho.ui.viewmodel.NuevoClienteViewModel = viewModel(factory = viewModelFactory)

            // 4. Se lo pasamos a la pantalla
            NuevoClienteScreen(viewModel = viewModel)
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