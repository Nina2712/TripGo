package com.example.proyectofinal.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectofinal.repository.VueloRepositorio
import com.example.proyectofinal.ui.model.Vuelo
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class VueloViewModel : ViewModel() {

    private val repositorio = VueloRepositorio()

    private val _vuelos = MutableStateFlow<List<Vuelo>>(emptyList())
    val vuelos: StateFlow<List<Vuelo>> = _vuelos.asStateFlow()

    private val _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun cargarVuelos() {
        viewModelScope.launch {
            _cargando.value = true
            _error.value = null

            try {
                _vuelos.value = repositorio.obtenerVuelos()
            } catch (e: Exception) {
                _error.value = e.localizedMessage
                    ?: "No se pudieron cargar los vuelos."
            } finally {
                _cargando.value = false
            }
        }
    }
}
