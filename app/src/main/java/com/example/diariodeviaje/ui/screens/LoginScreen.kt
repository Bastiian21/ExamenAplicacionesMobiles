package com.example.diariodeviaje.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.diariodeviaje.R
import com.example.diariodeviaje.data.network.BackendClient
import com.example.diariodeviaje.data.network.LoginRequest
import com.example.diariodeviaje.navigation.AppScreen
import com.example.diariodeviaje.viewmodel.LoginViewModel
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(navController: NavController, viewModel: LoginViewModel) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text("Bienvenido a B-Trax", style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Correo Electrónico") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Contraseña") }, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                scope.launch {
                    try {
                        val request = LoginRequest(email, password)
                        val usuario = BackendClient.service.login(request)

                        Toast.makeText(context, "Hola, ${usuario.nombre}", Toast.LENGTH_SHORT).show()


                        navController.navigate(AppScreen.Main.route) {
                            popUpTo(AppScreen.Login.route) { inclusive = true }
                        }
                    } catch (e: Exception) {
                        Toast.makeText(context, "Credenciales incorrectas", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Iniciar Sesión")
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = { navController.navigate(AppScreen.Register.route) }) {
            Text("¿No tienes cuenta? Regístrate aquí")
        }
    }
}