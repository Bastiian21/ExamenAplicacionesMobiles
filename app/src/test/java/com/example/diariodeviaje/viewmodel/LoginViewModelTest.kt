package com.example.diariodeviaje.viewmodel

import com.example.diariodeviaje.data.local.UserPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var userPreferences: UserPreferences
    private lateinit var viewModel: LoginViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        userPreferences = mock(UserPreferences::class.java)
        viewModel = LoginViewModel(userPreferences)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun login_EstadoInicial_EsVacio() {
        val estado = viewModel.uiState.value
        assertEquals("", estado.email)
        assertEquals("", estado.contrasena)
        assertNull(estado.errorMensaje)
    }

    @Test
    fun login_CamposVacios_MuestraError() = runTest {
        viewModel.onEmailChange("")
        viewModel.onContrasenaChange("")
        viewModel.login()

        val estado = viewModel.uiState.value
        assertEquals("Email y contraseña no pueden estar vacíos", estado.errorMensaje)
    }

    @Test
    fun login_EmailInvalido_MuestraErrorFormato() = runTest {
        viewModel.onEmailChange("correo_malo")
        viewModel.onContrasenaChange("123456")
        viewModel.login()

        val estado = viewModel.uiState.value
        assertEquals("El formato del email no es válido", estado.errorMensaje)
    }

    @Test
    fun login_CredencialesValidas_LoginExitoso() = runTest {
        viewModel.onEmailChange("usuario@test.com")
        viewModel.onContrasenaChange("password123")
        viewModel.login()

        testDispatcher.scheduler.advanceUntilIdle()

        val estado = viewModel.uiState.value
        assertTrue(estado.loginExitoso)
        assertNull(estado.errorMensaje)
    }

    @Test
    fun login_LimpiarError_ErrorEsNulo() {
        viewModel.login()
        viewModel.onErrorMessageShown()
        val estado = viewModel.uiState.value
        assertNull(estado.errorMensaje)
    }
}