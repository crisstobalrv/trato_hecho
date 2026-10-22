package com.example.trato_hecho.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.trato_hecho.ui.components.ValidatedTextField
import com.example.trato_hecho.ui.viewmodel.NuevoClienteViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevoClienteScreen(
    viewModel: NuevoClienteViewModel = viewModel(),
    onNavigateBack: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    // --- LANZADORES NATIVOS (Cámara y GPS) ---
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        viewModel.onFotoTomada(bitmap)
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) cameraLauncher.launch()
        else coroutineScope.launch { snackbarHostState.showSnackbar("Permiso de cámara denegado") }
    }

    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineLocation = permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false)
        val coarseLocation = permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false)

        if (fineLocation || coarseLocation) {
            @SuppressLint("MissingPermission")
            val task = fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
            task.addOnSuccessListener { location ->
                if (location != null) viewModel.onUbicacionObtenida(location.latitude, location.longitude)
                else coroutineScope.launch { snackbarHostState.showSnackbar("No se pudo obtener la ubicación. Enciende el GPS.") }
            }
        } else {
            coroutineScope.launch { snackbarHostState.showSnackbar("Permiso de ubicación denegado") }
        }
    }

    // --- INTERFAZ VISUAL ---
    Scaffold(
        topBar = {
            // NUEVO: Barra superior con botón físico para retroceder
            TopAppBar(
                title = { Text("Registrar Cliente") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Volver a la lista")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ValidatedTextField(
                value = state.rut,
                onValueChange = { viewModel.onRutChanged(it) },
                label = "RUT del Cliente",
                errorMessage = state.rutError
            )

            Spacer(modifier = Modifier.height(8.dp))

            ValidatedTextField(
                value = state.nombre,
                onValueChange = { viewModel.onNombreChanged(it) },
                label = "Nombre o Razón Social",
                errorMessage = state.nombreError
            )

            Spacer(modifier = Modifier.height(8.dp))

            ValidatedTextField(
                value = state.correo,
                onValueChange = { viewModel.onCorreoChanged(it) },
                label = "Correo Electrónico",
                errorMessage = state.correoError
            )

            // --- SECCIÓN DE RECURSOS NATIVOS ---
            Spacer(modifier = Modifier.height(16.dp))

            // Previsualización de la foto si existe
            state.fotoBitmap?.let { bitmap ->
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "Foto del cliente",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(120.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .padding(bottom = 16.dp)
                )
            }

            OutlinedButton(
                onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Filled.CameraAlt, contentDescription = "Cámara")
                Spacer(Modifier.width(8.dp))
                Text(if (state.fotoBitmap == null) "Tomar Foto del Local" else "Cambiar Foto")
            }

            Spacer(modifier = Modifier.height(8.dp))

            AnimatedVisibility(
                visible = state.latitud != null && state.longitud != null,
                enter = fadeIn() + expandVertically()
            ) {
                Text(
                    text = "📍 Lat: ${state.latitud} | Lon: ${state.longitud}",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            OutlinedButton(
                onClick = {
                    locationPermissionLauncher.launch(
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Filled.LocationOn, contentDescription = "GPS")
                Spacer(Modifier.width(8.dp))
                Text(if (state.latitud == null) "Obtener Ubicación GPS" else "Actualizar Ubicación")
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- BOTÓN DE GUARDADO ---
            Button(
                onClick = {
                    viewModel.guardarCliente(
                        onSuccess = {
                            // NUEVO: Usamos Toast para que el mensaje sobreviva y navegamos inmediatamente
                            Toast.makeText(context, "Cliente guardado exitosamente", Toast.LENGTH_SHORT).show()
                            onNavigateBack()
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