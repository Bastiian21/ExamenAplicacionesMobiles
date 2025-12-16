package com.example.diariodeviaje.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.diariodeviaje.data.network.BackendClient
import com.example.diariodeviaje.data.network.ComentarioRequest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SoporteScreen(navController: NavController) {
    var mensaje by remember { mutableStateOf("") }
    var enviando by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Soporte Técnico") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver atrás")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(
                text = "¿Tienes algún inconveniente? Escríbenos aquí.",
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = mensaje,
                onValueChange = { mensaje = it },
                label = { Text("Mensaje") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (mensaje.isBlank()) {
                        Toast.makeText(context, "Escribe algo primero", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    enviando = true
                    scope.launch {
                        try {
                            val comentario = ComentarioRequest(usuario = "Alumno Duoc", mensaje = mensaje)
                            BackendClient.service.enviarComentario(comentario)

                            Toast.makeText(context, "¡Enviado con éxito al Servidor!", Toast.LENGTH_LONG).show()
                            mensaje = ""
                        } catch (e: Exception) {
                            Toast.makeText(context, "Error: Revisa que el backend esté corriendo", Toast.LENGTH_LONG).show()
                        } finally {
                            enviando = false
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !enviando
            ) {
                if (enviando) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Enviar a Servidor")
                }
            }
        }
    }
}