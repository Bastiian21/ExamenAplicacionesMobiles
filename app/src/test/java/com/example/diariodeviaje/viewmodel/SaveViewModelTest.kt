package com.example.diariodeviaje.viewmodel

import androidx.lifecycle.SavedStateHandle
import com.example.diariodeviaje.data.repository.ActividadRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.kotlin.any

@OptIn(ExperimentalCoroutinesApi::class)
class SaveViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: ActividadRepository
    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var viewModel: SaveViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = mock(ActividadRepository::class.java)
        savedStateHandle = SavedStateHandle()

        viewModel = SaveViewModel(repository, savedStateHandle)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun guardar_TituloVacio_NoLlamaAlRepositorio() = runTest {
        viewModel.onTituloChange("")

        viewModel.guardarActividad()

        testDispatcher.scheduler.advanceUntilIdle()

        verify(repository, never()).insert(any())
    }

    @Test
    fun guardar_TituloValido_LlamaAlRepositorio() = runTest {
        viewModel.onTituloChange("Ruta al Parque")
        viewModel.guardarActividad()
        testDispatcher.scheduler.advanceUntilIdle()

        verify(repository).insert(any())
    }

    @Test
    fun actualizarTitulo_CambiaElEstado() {
        viewModel.onTituloChange("Nuevo Título")
        assertEquals("Nuevo Título", viewModel.uiState.value.titulo)
    }
}