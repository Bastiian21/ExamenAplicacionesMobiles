package com.example.diariodeviaje.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.diariodeviaje.ui.components.ActividadItemCard
import com.example.diariodeviaje.viewmodel.ProfileViewModel

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onLogout: () -> Unit,
    onActividadClick: (Long) -> Unit
) {
    val actividades by viewModel.misActividades.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("Mi Perfil", style = MaterialTheme.typography.headlineMedium)
        }

        Button(
            onClick = {
                viewModel.cerrarSesion()
                onLogout()
            },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Text("Cerrar Sesión", color = Color.White)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Mis Rutas Guardadas",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (actividades.isEmpty()) {
                item {
                    Text("Aún no tienes rutas guardadas.", color = Color.Gray)
                }
            } else {
                items(actividades) { actividad ->
                    ActividadItemCard(
                        actividad = actividad,
                        onClick = { onActividadClick(actividad.id) }
                    )
                }
            }
        }
    }
}