package com.example.trato_hecho

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.trato_hecho.ui.theme.Trato_hechoTheme
import com.example.trato_hecho.ui.navigation.AppNavigation

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Trato_hechoTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // El Box aplica el padding del sistema para que las pantallas
                    // no queden ocultas detrás de la barra de notificaciones.
                    Box(modifier = Modifier.padding(innerPadding)) {
                        AppNavigation()
                    }
                }
            }
        }
    }
}