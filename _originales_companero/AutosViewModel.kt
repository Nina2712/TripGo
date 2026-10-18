package com.example.proyectofinal.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectofinal.repository.AutosRepositorio
import com.example.proyectofinal.ui.model.Auto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AutosViewModel: ViewModel() {
    private val repositorio = AutosRepositorio()

    // Lista de autos disponibles.
    private val _autos = MutableStateFlow<List<Auto>>(emptyList())
    val autos: StateFlow<List<Auto>> = _autos.asStateFlow()

    // Estado de carga.
    private val _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando.asStateFlow()

    // Mensajes de error.
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // Mensajes de reserva.
    private val _mensajeReserva = MutableStateFlow<String?>(null)
    val mensajeReserva: StateFlow<String?> =
        _mensajeReserva.asStateFlow()

    // Cargar los autos desde Firestore.
    fun cargarAutos() {
        _cargando.value = true
        _error.value = null

        // Mostrar los vehículos temporales.
        _autos.value = com.example.proyectofinal.ui.model.AutosPrueba.lista

        _cargando.value = false
    }

    // Solicitar la reserva de un auto.
    fun reservarAuto(
        autoId: String,
        fechaInicio: String,
        fechaFin: String
    ) {
        viewModelScope.launch {
            _error.value = null
            _mensajeReserva.value = null
            _cargando.value = true

            try {
                repositorio.reservarAuto(
                    autoId = autoId,
                    fechaInicio = fechaInicio,
                    fechaFin = fechaFin
                )

                _mensajeReserva.value = "¡Reserva registrada correctamente!"
            } catch (e: Exception) {
                _error.value =
                    e.localizedMessage ?: "No se pudo registrar la reserva."
            } finally {
                _cargando.value = false
            }
        }
    }
}