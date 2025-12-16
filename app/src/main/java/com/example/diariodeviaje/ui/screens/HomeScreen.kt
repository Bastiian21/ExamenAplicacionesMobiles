package com.example.diariodeviaje.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.diariodeviaje.data.model.Actividad
import com.example.diariodeviaje.data.network.UsuarioResponse
import com.example.diariodeviaje.ui.components.ActividadItemCard
import com.example.diariodeviaje.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel,
    navController: NavController,
    onActividadClick: (Long) -> Unit = {},
    onSoporteClick: () -> Unit
) {
    val actividades by homeViewModel.viajes.collectAsState()
    val usuarios by homeViewModel.usuarios.collectAsState()

    var textoBusqueda by remember { mutableStateOf("") }

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            OutlinedTextField(
                value = textoBusqueda,
                onValueChange = {
                    textoBusqueda = it
                    homeViewModel.filtrar(it)
                },
                label = { Text("Buscar deportistas o rutas...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = CircleShape
            )

            Button(
                onClick = onSoporteClick,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Text("Contactar Soporte (Backend Java)")
            }

            Spacer(modifier = Modifier.height(8.dp))

            HomeScreenContent(
                modifier = Modifier.weight(1f),
                actividades = actividades,
                usuarios = usuarios,
                onActividadClick = onActividadClick,
                onUsuarioClick = { nombre, email ->
                    navController.navigate("user_profile/$nombre/$email")
                }
            )
        }
    }
}

@Composable
fun HomeScreenContent(
    modifier: Modifier = Modifier,
    actividades: List<Actividad>,
    usuarios: List<UsuarioResponse>,
    onActividadClick: (Long) -> Unit,
    onUsuarioClick: (String, String) -> Unit
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (usuarios.isNotEmpty()) {
            item {
                Text(
                    text = "Deportistas Encontrados (Backend)",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            items(usuarios) { usuario ->
                UsuarioItemCard(
                    usuario = usuario,
                    onClick = { onUsuarioClick(usuario.nombre, usuario.email) }
                )
            }
            item {
                Divider(modifier = Modifier.padding(vertical = 8.dp))
            }
        }

        item {
            Text(
                text = "Rutas Locales",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        if (actividades.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(20.dp), contentAlignment = Alignment.Center) {
                    Text("No hay rutas registradas.", color = Color.Gray)
                }
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

@Composable
fun UsuarioItemCard(
    usuario: UsuarioResponse,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(MaterialTheme.colorScheme.primary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, contentDescription = null, tint = Color.White)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = usuario.nombre, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(text = usuario.email, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}