package com.example.trato_hecho.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.trato_hecho.data.local.TratoHechoDatabase
import com.example.trato_hecho.data.repository.ClienteRepository
import com.example.trato_hecho.data.repository.CotizacionRepository
import com.example.trato_hecho.ui.screens.HomeScreen
import com.example.trato_hecho.ui.screens.ListaClientesScreen
import com.example.trato_hecho.ui.screens.ListaCotizacionesScreen
import com.example.trato_hecho.ui.screens.LoginScreen
import com.example.trato_hecho.ui.screens.NuevaCotizacionScreen
import com.example.trato_hecho.ui.screens.NuevoClienteScreen
import com.example.trato_hecho.ui.viewmodel.CotizacionViewModel
import com.example.trato_hecho.ui.viewmodel.CotizacionViewModelFactory
import com.example.trato_hecho.ui.viewmodel.HomeViewModel
import com.example.trato_hecho.ui.viewmodel.HomeViewModelFactory
import com.example.trato_hecho.ui.viewmodel.ListaClientesViewModel
import com.example.trato_hecho.ui.viewmodel.ListaClientesViewModelFactory
import com.example.trato_hecho.ui.viewmodel.ListaCotizacionesViewModel
import com.example.trato_hecho.ui.viewmodel.ListaCotizacionesViewModelFactory
import com.example.trato_hecho.ui.viewmodel.LoginViewModel
import com.example.trato_hecho.ui.viewmodel.NuevoClienteViewModel
import com.example.trato_hecho.ui.viewmodel.NuevoClienteViewModelFactory

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current

    val database = TratoHechoDatabase.getDatabase(context)
    val clienteRepository = ClienteRepository(database.clienteDao())
    val cotizacionRepository = CotizacionRepository(database.cotizacionDao())
    val indicadoresRepository = com.example.trato_hecho.data.repository.IndicadoresRepository()


    NavHost(navController = navController, startDestination = Routes.Login.route) {

        // 0. Login con selector de rol
        composable(Routes.Login.route) {
            val viewModel: LoginViewModel = viewModel()

            LoginScreen(
                viewModel = viewModel,
                onLoginSuccess = {
                    navController.navigate(Routes.Home.route) {
                        popUpTo(Routes.Login.route) { inclusive = true }
                    }
                }
            )
        }

        // 1. Dashboard / Home
        composable(Routes.Home.route) {
            val viewModelFactory = HomeViewModelFactory(clienteRepository, cotizacionRepository, indicadoresRepository)
            val viewModel: HomeViewModel = viewModel(factory = viewModelFactory)

            HomeScreen(
                viewModel = viewModel,
                onNavigateToClientes = {
                    navController.navigate(Routes.Clientes.route)
                },
                onNavigateToNuevoCliente = {
                    navController.navigate(Routes.NuevoCliente.route)
                },
                onNavigateToCotizaciones = {
                    navController.navigate(Routes.Cotizaciones.route)
                },
                onNavigateToNuevaCotizacion = {
                    navController.navigate(Routes.NuevaCotizacion.route)
                },
                onLogout = {
                    navController.navigate(Routes.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // 2. Lista de Clientes
        composable(Routes.Clientes.route) {
            val viewModelFactory = ListaClientesViewModelFactory(clienteRepository)
            val viewModel: ListaClientesViewModel = viewModel(factory = viewModelFactory)

            ListaClientesScreen(
                viewModel = viewModel,
                onNavigateToNuevoCliente = {
                    navController.navigate(Routes.NuevoCliente.route)
                },
                onNavigateToNuevaCotizacion = {
                    navController.navigate(Routes.Cotizaciones.route)
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // 3. Formulario Nuevo Cliente
        composable(Routes.NuevoCliente.route) {
            val viewModelFactory = NuevoClienteViewModelFactory(clienteRepository)
            val viewModel: NuevoClienteViewModel = viewModel(factory = viewModelFactory)

            NuevoClienteScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // 4. Lista de Cotizaciones
        composable(Routes.Cotizaciones.route) {
            val viewModelFactory = ListaCotizacionesViewModelFactory(cotizacionRepository)
            val viewModel: ListaCotizacionesViewModel = viewModel(factory = viewModelFactory)

            ListaCotizacionesScreen(
                viewModel = viewModel,
                onNavigateToNuevaCotizacion = {
                    navController.navigate(Routes.NuevaCotizacion.route)
                },
                onNavigateToEditarCotizacion = { id ->
                    navController.navigate("${Routes.NuevaCotizacion.route}?cotizacionId=$id")
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // 5. Formulario Nueva / Editar Cotización
        composable(
            route = "${Routes.NuevaCotizacion.route}?cotizacionId={cotizacionId}",
            arguments = listOf(
                navArgument("cotizacionId") {
                    type = NavType.IntType
                    defaultValue = -1
                }
            )
        ) { backStackEntry ->
            val cotizacionId = backStackEntry.arguments?.getInt("cotizacionId") ?: -1
            val viewModelFactory = CotizacionViewModelFactory(cotizacionRepository)
            val viewModel: CotizacionViewModel = viewModel(factory = viewModelFactory)

            NuevaCotizacionScreen(
                viewModel = viewModel,
                cotizacionId = cotizacionId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}