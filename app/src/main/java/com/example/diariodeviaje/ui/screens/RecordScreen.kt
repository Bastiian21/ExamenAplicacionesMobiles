package com.example.diariodeviaje.ui.screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.diariodeviaje.viewmodel.EstadoGrabacion
import com.example.diariodeviaje.viewmodel.RecordViewModel
import java.util.concurrent.TimeUnit

@Composable
fun RecordScreen(
    viewModel: RecordViewModel,
    onNavigate: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.navEvents.collect { route ->
            onNavigate(route)
        }
    }

    val permisos = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    var tienePermiso by remember { mutableStateOf(false) }

    val lanzadorPermisos = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permisosMap ->
        tienePermiso = permisosMap[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
    }

    LaunchedEffect(key1 = true) {
        lanzadorPermisos.launch(permisos)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (tienePermiso) {

            Text(
                text = "Distancia",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "%.2f km".format(uiState.distanciaMetros / 1000),
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Tiempo",
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = formatearTiempo(uiState.tiempoSegundos),
                fontSize = 64.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(32.dp))

            ControlesGrabacion(
                estado = uiState.estado,
                onIniciar = { viewModel.iniciarGrabacion() },
                onPausar = { viewModel.pausarGrabacion() },
                onReanudar = { viewModel.reanudarGrabacion() },
                onDetener = { viewModel.detenerGrabacion() }
            )
        } else {
            Text(
                "B-Trax necesita permiso de ubicación para grabar tus actividades. " +
                        "Por favor, acepta el permiso cuando aparezca.",
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun ControlesGrabacion(
    estado: EstadoGrabacion,
    onIniciar: () -> Unit,
    onPausar: () -> Unit,
    onReanudar: () -> Unit,
    onDetener: () -> Unit
) {
    when (estado) {
        EstadoGrabacion.DETENIDO -> {
            Button(onClick = onIniciar, modifier = Modifier.fillMaxWidth()) {
                Text("Iniciar Grabación")
            }
        }
        EstadoGrabacion.GRABANDO -> {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(onClick = onPausar) {
                    Text("Pausar")
                }
                Button(onClick = onDetener) {
                    Text("Finalizar")
                }
            }
        }
        EstadoGrabacion.PAUSADO -> {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(onClick = onReanudar) {
                    Text("Reanudar")
                }
                Button(onClick = onDetener) {
                    Text("Finalizar")
                }
            }
        }
    }
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