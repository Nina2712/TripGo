package com.example.proyectofinal.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectofinal.network.RedApi
import com.example.proyectofinal.network.descripcionClima
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class Destino(
    val nombre: String,
    val emoji: String,
    val latitud: Double,
    val longitud: Double
)

object CatalogoDestinos {
    val lista = listOf(
        Destino("Cusco", "🏔️", -13.5320, -71.9675),
        Destino("Arequipa", "🌋", -16.4090, -71.5375),
        Destino("Ica", "🏖️", -14.0678, -75.7286),
        Destino("Nazca", "🛩️", -14.8300, -74.9390),
        Destino("Piura", "🌴", -5.1945, -80.6328),
        Destino("Iquitos", "🌿", -3.7437, -73.2516),
        Destino("Lima", "🏙️", -12.0464, -77.0428)
    )
}

data class DestinoClima(
    val destino: Destino,
    val temperatura: Double?,
    val descripcion: String,
    val viento: Double?
)

/** Consume el servicio REST de clima (Retrofit) en segundo plano con corrutinas. */
class DestinosViewModel : ViewModel() {

    private val _destinos = MutableStateFlow<List<DestinoClima>>(emptyList())
    val destinos: StateFlow<List<DestinoClima>> = _destinos.asStateFlow()

    private val _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun cargar() {
        viewModelScope.launch {
            _cargando.value = true
            _error.value = null

            try {
                val resultado = coroutineScope {
                    CatalogoDestinos.lista.map { destino ->
                        async {
                            try {
                                val r = RedApi.clima.obtenerClima(
                                    destino.latitud,
                                    destino.longitud
                                )
                                DestinoClima(
                                    destino = destino,
                                    temperatura = r.current?.temperatura,
                                    descripcion = descripcionClima(r.current?.codigo),
                                    viento = r.current?.viento
                                )
                            } catch (e: Exception) {
                                DestinoClima(destino, null, "Sin datos", null)
                            }
                        }
                    }.awaitAll()
                }

                _destinos.value = resultado

                if (resultado.all { it.temperatura == null }) {
                    _error.value =
                        "No se pudo obtener el clima. Revisa tu conexión a Internet."
                }
            } finally {
                _cargando.value = false
            }
        }
    }
}
