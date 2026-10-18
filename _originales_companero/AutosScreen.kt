package com.example.proyectofinal.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.proyectofinal.ui.model.Auto
import com.example.proyectofinal.viewmodel.AutosViewModel

import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.proyectofinal.R

@Composable
fun AutosScreen(
    viewModel: AutosViewModel,
    alVolver: () -> Unit
) {
    val autos by viewModel.autos.collectAsState()
    val cargando by viewModel.cargando.collectAsState()
    val error by viewModel.error.collectAsState()
    val mensajeReserva by viewModel.mensajeReserva.collectAsState()

    var fechaInicio by remember { mutableStateOf("") }
    var fechaFin by remember { mutableStateOf("") }
    var autoSeleccionado by remember { mutableStateOf<Auto?>(null) }
    var precioCalculado by remember { mutableStateOf<Double?>(null) }
    var mensajeCalculo by remember { mutableStateOf<String?>(null) }

    fun calcularPrecio() {
        val formato = java.text.SimpleDateFormat(
            "yyyy-MM-dd",
            java.util.Locale.US
        ).apply {
            isLenient = false
        }

        try {
            val inicio = formato.parse(fechaInicio.trim())
                ?: throw IllegalArgumentException()

            val fin = formato.parse(fechaFin.trim())
                ?: throw IllegalArgumentException()

            require(formato.format(inicio) == fechaInicio.trim()) {
                "La fecha inicial debe tener el formato AAAA-MM-DD."
            }

            require(formato.format(fin) == fechaFin.trim()) {
                "La fecha final debe tener el formato AAAA-MM-DD."
            }

            require(fin.after(inicio)) {
                "La fecha final debe ser posterior a la inicial."
            }

            val auto = autoSeleccionado
                ?: throw IllegalArgumentException(
                    "Primero selecciona un vehículo."
                )

            val calendarioInicio = java.util.Calendar.getInstance().apply {
                time = inicio
            }

            val calendarioFin = java.util.Calendar.getInstance().apply {
                time = fin
            }

            val dias = (
                    (calendarioFin.timeInMillis -
                            calendarioInicio.timeInMillis) /
                            (24L * 60 * 60 * 1000)
                    ).toInt()

            require(dias > 0) {
                "El alquiler debe durar al menos un día."
            }

            precioCalculado = auto.precioPorDia * dias
            mensajeCalculo = "Duración: $dias días."
        } catch (e: Exception) {
            precioCalculado = null
            mensajeCalculo = e.message
                ?: "Revisa las fechas y el vehículo seleccionado."
        }
    }


    LaunchedEffect(Unit) {
        viewModel.cargarAutos()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "🚗 Alquiler de autos",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text("Selecciona un vehículo y las fechas del alquiler.")

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = fechaInicio,
            onValueChange = { fechaInicio = it },
            label = { Text("Fecha de inicio (AAAA-MM-DD)") },
            placeholder = { Text("2026-10-20") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = fechaFin,
            onValueChange = { fechaFin = it },
            label = { Text("Fecha de fin (AAAA-MM-DD)") },
            placeholder = { Text("2026-10-23") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Button(
            onClick = { calcularPrecio() },
            modifier = Modifier.fillMaxWidth(),
            enabled = autoSeleccionado != null
        ) {
            Text("Calcular precio")
        }

        mensajeCalculo?.let { mensaje ->
            Text(
                text = mensaje,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        precioCalculado?.let { precio ->
            Text(
                text = "Precio total: S/ %.2f".format(precio),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (cargando) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(8.dp))
        }

        error?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error
            )
        }

        mensajeReserva?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.primary
            )
        }

        if (autos.isEmpty() && !cargando) {
            Text("No hay vehículos disponibles para mostrar.")
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(autos) { auto ->

                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        // Imagen del vehículo
                        Image(
                            painter = painterResource(
                                id = when (auto.id) {
                                    "auto1" -> R.drawable.toyota_corolla
                                    "auto2" -> R.drawable.hyundai_tucson
                                    "auto3" -> R.drawable.kia_rio
                                    "auto4" -> R.drawable.toyota_rav4
                                    else -> R.drawable.toyota_corolla
                                }
                            ),
                            contentDescription = auto.nombre,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp),
                            contentScale = ContentScale.Crop
                        )

                        // Información del vehículo
                        Column(
                            modifier = Modifier.padding(12.dp)
                        ) {

                            Text(
                                text = auto.nombre,
                                style = MaterialTheme.typography.titleLarge
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "Modelo: ${auto.modelo}"
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "S/ %.2f por día".format(auto.precioPorDia),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Botones
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {

                                // Seleccionar vehículo
                                Button(
                                    onClick = {
                                        autoSeleccionado = auto
                                        precioCalculado = null
                                        mensajeCalculo = null
                                    }
                                ) {
                                    Text(
                                        if (autoSeleccionado?.id == auto.id) {
                                            "Seleccionado"
                                        } else {
                                            "Seleccionar"
                                        }
                                    )
                                }

                                // Calcular alquiler
                                Button(
                                    enabled = autoSeleccionado?.id == auto.id,
                                    onClick = {
                                        calcularPrecio()
                                    }
                                ) {
                                    Text("Calcular alquiler")
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = alVolver,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Volver al inicio")
        }
    }
}