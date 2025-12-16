package com.example.diariodeviaje.ui.screens

import android.Manifest
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.diariodeviaje.navigation.AppScreen
import com.example.diariodeviaje.viewmodel.SaveViewModel
import kotlinx.coroutines.flow.collectLatest
import java.io.File
import java.util.concurrent.TimeUnit

private fun getTempImageUri(context: Context): Uri {
    val file = File.createTempFile(
        "temp_image_b_trax",
        ".jpg",
        context.externalCacheDir
    )
    val authority = "${context.packageName}.provider"
    return FileProvider.getUriForFile(
        context,
        authority,
        file
    )
}

private fun formatearTiempo(segundos: Long): String {
    val horas = TimeUnit.SECONDS.toHours(segundos)
    val minutos = TimeUnit.SECONDS.toMinutes(segundos) % 60
    val segs = segundos % 60
    return if (horas > 0) {
        String.format("%02d:%02d:%02d", horas, minutos, segs)
    } else {
        String.format("%02d:%02d", minutos, segs)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SaveScreen(
    navController: NavController,
    viewModel: SaveViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }

    val lanzadorCamara = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { exito ->
        if (exito && tempCameraUri != null) {
            viewModel.onFotoChange(tempCameraUri.toString())
        }
    }

    val lanzadorPermisoCamara = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val newUri = getTempImageUri(context)
            tempCameraUri = newUri
            lanzadorCamara.launch(newUri)
        } else {
            Toast.makeText(context, "Se necesita permiso de cámara para fotos", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(key1 = true) {
        viewModel.navegacionExitosa.collectLatest {
            Toast.makeText(context, "Actividad guardada con éxito!", Toast.LENGTH_SHORT).show()
            navController.popBackStack(AppScreen.Main.route, inclusive = false)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Guardar Actividad") })
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Distancia: %.2f km".format(viewModel.distanciaKm),
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = "Tiempo: ${formatearTiempo(viewModel.tiempoSegundos)}",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Inicio: ${uiState.direccionInicio}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Fin: ${uiState.direccionFin}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary
            )


            Spacer(modifier = Modifier.height(24.dp))
            Divider()
            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = uiState.titulo,
                onValueChange = { viewModel.onTituloChange(it) },
                label = { Text("Título de la Actividad") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = uiState.descripcion,
                onValueChange = { viewModel.onDescripcionChange(it) },
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    lanzadorPermisoCamara.launch(Manifest.permission.CAMERA)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (uiState.fotoUri == null) "Añadir Foto (Cámara)" else "Cambiar Foto")
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (uiState.fotoUri != null) {
                Image(
                    painter = rememberAsyncImagePainter(Uri.parse(uiState.fotoUri)),
                    contentDescription = "Foto de la actividad",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    viewModel.guardarActividad()
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = uiState.titulo.isNotBlank()
            ) {
                Text("Guardar Actividad")
            }
        }
    }
}