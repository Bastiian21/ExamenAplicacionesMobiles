package com.example.diariodeviaje.viewmodel

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.diariodeviaje.data.local.UserPreferences
import com.example.diariodeviaje.data.network.BackendClient
import com.example.diariodeviaje.data.network.LoginRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginUiState(
    val email: String = "",
    val contrasena: String = "",
    val errorMensaje: String? = null,
    val loginExitoso: Boolean = false
)

class LoginViewModel(private val userPreferences: UserPreferences) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email) }
    }

    fun onPasswordChange(pass: String) {
        _uiState.update { it.copy(contrasena = pass) }
    }

    fun login() {
        if (_uiState.value.email.isBlank() || _uiState.value.contrasena.isBlank()) {
            _uiState.update { it.copy(errorMensaje = "Campos vacíos") }
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(_uiState.value.email).matches()) {
            _uiState.update { it.copy(errorMensaje = "Email inválido") }
            return
        }

        viewModelScope.launch {
            try {
                val response = BackendClient.service.login(
                    LoginRequest(_uiState.value.email, _uiState.value.contrasena)
                )

                userPreferences.saveUser(response.email, response.id)

                _uiState.update { it.copy(loginExitoso = true, errorMensaje = null) }

            } catch (e: Exception) {
                _uiState.update { it.copy(errorMensaje = "Error: Credenciales incorrectas o sin conexión") }
            }
        }
    }
}