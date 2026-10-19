package com.example.trato_hecho.ui.navigation

sealed class Routes(val route: String) {
    object Home : Routes("home")
    object Clientes : Routes("clientes")
    object NuevoCliente : Routes("nuevo_cliente")
    object Cotizaciones : Routes("cotizaciones")
    object NuevaCotizacion : Routes("nueva_cotizacion")
}