package com.example.diariodeviaje.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.diariodeviaje.data.network.ActividadNetwork
import com.example.diariodeviaje.data.network.BackendClient

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(navController: NavController, nombre: String, email: String) {

    var actividadesRemotas by remember { mutableStateOf<List<ActividadNetwork>>(emptyList()) }
    var cargando by remember { mutableStateOf(true) }
    val context = LocalContext.current

    LaunchedEffect(email) {
        try {
            actividadesRemotas = BackendClient.service.obtenerActividadesDeUsuario(email)
        } catch (e: Exception) {
            Toast.makeText(context, "Error de conexión", Toast.LENGTH_SHORT).show()
        } finally {
            cargando = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Perfil de $nombre") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(MaterialTheme.colorScheme.primary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(50.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = nombre, style = MaterialTheme.typography.headlineMedium)
            Text(text = email, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Historial de Rutas (Nube)",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )

            if (cargando) {
                CircularProgressIndicator()
            } else if (actividadesRemotas.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    Text("Este usuario aún no ha subido rutas.")
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(actividadesRemotas) { red ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    Toast.makeText(context, "Descripción: ${red.descripcion}", Toast.LENGTH_LONG).show()
                                }
                        ) {
                            Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                                Text(
                                    text = red.titulo,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = red.lugar, style = MaterialTheme.typography.bodySmall)
                                Spacer(modifier = Modifier.height(8.dp))
                                Row {
                                    Text(
                                        text = "Distancia: ${red.distancia}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Text(
                                        text = "Tiempo: ${red.tiempo}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}