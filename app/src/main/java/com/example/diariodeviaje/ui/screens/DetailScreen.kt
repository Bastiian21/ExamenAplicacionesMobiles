package com.example.diariodeviaje.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.example.diariodeviaje.viewmodel.DetailViewModel
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    navController: NavController,
    viewModel: DetailViewModel
) {
    val actividad by viewModel.actividad.collectAsState()
    val context = LocalContext.current

    Configuration.getInstance().load(
        context,
        context.getSharedPreferences("osmdroid", 0)
    )
    Configuration.getInstance().userAgentValue = "BTraxApp/1.0"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(actividad?.titulo ?: "Detalle de Ruta") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            actividad?.let { act ->

                AndroidView(
                    factory = {
                        MapView(it).apply {
                            setTileSource(TileSourceFactory.MAPNIK)
                            setMultiTouchControls(true)
                        }
                    },
                    update = { mapView ->
                        mapView.overlays.clear()

                        if (!act.rutaCompleta.isNullOrBlank()) {
                            val lineaReal = org.osmdroid.views.overlay.Polyline()
                            val puntos = act.rutaCompleta.split(";").mapNotNull { par ->
                                val coords = par.split(",")
                                if (coords.size == 2) {
                                    try {
                                        GeoPoint(coords[0].toDouble(), coords[1].toDouble())
                                    } catch (e: Exception) { null }
                                } else null
                            }

                            lineaReal.setPoints(puntos)
                            lineaReal.color = android.graphics.Color.BLUE
                            lineaReal.width = 15f

                            mapView.overlays.add(lineaReal)
                        }

                        val puntoInicio = GeoPoint(act.latInicio, act.lonInicio)
                        val puntoFin = GeoPoint(act.latFin, act.lonFin)

                        val markerInicio = Marker(mapView)
                        markerInicio.position = puntoInicio
                        markerInicio.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        markerInicio.title = "Inicio"
                        mapView.overlays.add(markerInicio)

                        if (act.latInicio != act.latFin || act.lonInicio != act.lonFin) {
                            val markerFin = Marker(mapView)
                            markerFin.position = puntoFin
                            markerFin.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                            markerFin.title = "Fin"
                            mapView.overlays.add(markerFin)
                        }

                        val box = org.osmdroid.util.BoundingBox.fromGeoPoints(
                            listOf(puntoInicio, puntoFin)
                        )

                        mapView.post {
                            mapView.zoomToBoundingBox(box, true, 150)
                        }
                        mapView.invalidate()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(350.dp)
                )

                Column(modifier = Modifier.padding(24.dp)) {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        DetailStatItem(
                            icon = Icons.Default.Info,
                            label = "Distancia",
                            value = "%.2f km".format(act.distanciaKm)
                        )
                        DetailStatItem(
                            icon = Icons.Default.Timer,
                            label = "Tiempo",
                            value = formatearTiempo(act.tiempoSegundos)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    Divider()
                    Spacer(modifier = Modifier.height(24.dp))

                    if (!act.direccion.isNullOrBlank()) {
                        val partes = act.direccion.split(" -> ")
                        if (partes.size >= 2) {
                            DetailLocationRow(
                                icon = Icons.Default.LocationOn,
                                color = Color(0xFF4CAF50),
                                label = "Punto de Partida",
                                text = partes[0]
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            DetailLocationRow(
                                icon = Icons.Default.Place,
                                color = Color(0xFFF44336),
                                label = "Punto de Llegada",
                                text = partes[1]
                            )
                        } else {
                            DetailLocationRow(
                                icon = Icons.Default.Place,
                                color = MaterialTheme.colorScheme.primary,
                                label = "Ubicación",
                                text = act.direccion
                            )
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                    }

                    if (act.descripcion.isNotBlank()) {
                        Text(
                            text = "Notas del recorrido",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.Top) {
                            Icon(
                                imageVector = Icons.Default.Description,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp).padding(top=2.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = act.descripcion,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DetailStatItem(icon: ImageVector, label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(32.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.outline
        )
    }
}

@Composable
fun DetailLocationRow(icon: ImageVector, color: Color, label: String, text: String) {
    Row(verticalAlignment = Alignment.Top) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = color,
            modifier = Modifier.size(24.dp).padding(top = 2.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = color,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
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