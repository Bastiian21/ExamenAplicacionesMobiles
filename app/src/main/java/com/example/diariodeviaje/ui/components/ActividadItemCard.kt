package com.example.diariodeviaje.ui.components

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.diariodeviaje.data.model.Actividad
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActividadItemCard(
    actividad: Actividad,
    onClick: () -> Unit = {}
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column {
            if (!actividad.fotoUri.isNullOrBlank()) {
                Image(
                    painter = rememberAsyncImagePainter(Uri.parse(actividad.fotoUri)),
                    contentDescription = "Foto de la ruta",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentScale = ContentScale.Crop
                )
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = actividad.titulo,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                if (actividad.descripcion.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = actividad.descripcion,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                Divider(color = MaterialTheme.colorScheme.surfaceVariant)
                Spacer(modifier = Modifier.height(16.dp))

                if (!actividad.direccion.isNullOrBlank()) {
                    val partes = actividad.direccion.split(" -> ")

                    if (partes.size >= 2) {
                        RutaRow(
                            icon = Icons.Default.LocationOn,
                            color = Color(0xFF4CAF50),
                            label = "Inicio",
                            text = partes[0]
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        RutaRow(
                            icon = Icons.Default.Place,
                            color = Color(0xFFF44336),
                            label = "Fin",
                            text = partes[1]
                        )
                    } else {
                        Text(
                            text = actividad.direccion,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }


                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    StatBadge(
                        label = "Distancia",
                        value = "%.2f km".format(actividad.distanciaKm)
                    )
                    StatBadge(
                        label = "Tiempo",
                        value = formatearTiempo(actividad.tiempoSegundos)
                    )
                }
            }
        }
    }
}


@Composable
fun RutaRow(icon: ImageVector, color: Color, label: String, text: String) {
    Row(verticalAlignment = Alignment.Top) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = color,
            modifier = Modifier.size(20.dp).padding(top = 2.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = color
            )
            Text(
                text = text,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}


@Composable
fun StatBadge(label: String, value: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.outline
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
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

