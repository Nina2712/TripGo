package com.example.proyectofinal.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.proyectofinal.data.DatabaseHelper
import com.example.proyectofinal.network.RedApi
import com.example.proyectofinal.ui.model.Servicio
import com.example.proyectofinal.ui.model.TipoServicio
import com.example.proyectofinal.util.Fechas
import com.example.proyectofinal.data.Sincronizador
import com.example.proyectofinal.util.NotificacionHelper
import com.example.proyectofinal.util.RecordatorioReservaJob

/**
 * Formulario de reserva para cualquier servicio (vuelo, hotel, auto o tour).
 * - Valida nombre, DNI (8 dígitos) y cantidad.
 * - En autos se piden fechas de inicio y fin (AAAA-MM-DD) y se evitan cruces.
 * - Consume el servicio REST de tipo de cambio para mostrar el total en otra moneda.
 * - Guarda la reserva en SQLite, emite un sonido y muestra una notificación.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservaFormScreen(
    servicio: Servicio,
    nombreInicial: String,
    dbHelper: DatabaseHelper,
    onReservaExitosa: () -> Unit,
    onVolver: () -> Unit
) {
    val contexto = LocalContext.current
    val tipo = servicio.tipo
    val esAuto = tipo == TipoServicio.AUTO

    var nombre by remember { mutableStateOf(nombreInicial) }
    var dni by remember { mutableStateOf("") }
    var cantidadTexto by remember { mutableStateOf("1") }
    var fechaInicio by remember { mutableStateOf("") }
    var fechaFin by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf("") }

    // Servicio REST: tipo de cambio USD -> PEN (se consulta en segundo plano).
    var tasaPen by remember { mutableStateOf<Double?>(null) }
    LaunchedEffect(Unit) {
        tasaPen = try {
            RedApi.cambio.obtenerTasas().rates?.get("PEN")
        } catch (e: Exception) {
            null
        }
    }

    val errorFechas: String? =
        if (esAuto) Fechas.validarRango(fechaInicio, fechaFin) else null

    val cantidad: Int = when {
        esAuto && errorFechas == null -> Fechas.dias(fechaInicio, fechaFin)
        esAuto -> 0
        else -> cantidadTexto.toIntOrNull() ?: 0
    }
    val total = servicio.precio * cantidad

    // Equivalencia en la otra moneda con el tipo de cambio obtenido.
    val equivalencia: String? = tasaPen?.let { tasa ->
        if (tipo == TipoServicio.VUELO) {
            "≈ S/ " + String.format(java.util.Locale.US, "%.2f", total * tasa)
        } else {
            "≈ US$ " + String.format(java.util.Locale.US, "%.2f", total / tasa)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Reservar ${tipo.etiqueta.lowercase()}") })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(FondoTG)
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = tipo.emoji + "  " + servicio.nombre,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextoTG
                    )
                    Text(text = "Destino: ${servicio.ciudad}", color = GrisTG)
                    if (servicio.detalle.isNotBlank()) {
                        Text(text = servicio.detalle, color = GrisTG)
                    }
                    Text(
                        text = "Precio por ${tipo.unidad}: " +
                                formatoPrecio(tipo, servicio.precio),
                        color = AzulTG,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre completo") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = dni,
                onValueChange = {
                    if (it.all(Char::isDigit) && it.length <= 8) dni = it
                },
                label = { Text("DNI (8 dígitos)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            if (esAuto) {
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
                if (errorFechas == null) {
                    Text(text = "Días de alquiler: $cantidad", color = GrisTG)
                }
            } else {
                OutlinedTextField(
                    value = cantidadTexto,
                    onValueChange = {
                        if (it.all(Char::isDigit) && it.length <= 2) cantidadTexto = it
                    },
                    label = { Text("Cantidad de ${tipo.unidades}") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }

            Text(
                text = "Total: " + formatoPrecio(tipo, total),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextoTG
            )

            if (equivalencia != null && total > 0) {
                Text(
                    text = "$equivalencia (tipo de cambio en línea)",
                    color = GrisTG,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            if (mensaje.isNotBlank()) {
                Text(text = mensaje, color = RojoTG)
            }

            Button(
                onClick = {
                    when {
                        nombre.isBlank() ->
                            mensaje = "Ingresa el nombre del cliente."

                        dni.length != 8 ->
                            mensaje = "El DNI debe tener 8 dígitos."

                        esAuto && errorFechas != null ->
                            mensaje = errorFechas ?: ""

                        esAuto && dbHelper.existeCruceAuto(
                            servicio.nombre, servicio.ciudad,
                            fechaInicio.trim(), fechaFin.trim()
                        ) ->
                            mensaje = "El auto ya está reservado para esas fechas."

                        cantidad < 1 || cantidad > 30 ->
                            mensaje = "La cantidad debe estar entre 1 y 30."

                        else -> {
                            val guardada = dbHelper.insertarReserva(
                                servicio = servicio,
                                clienteNombre = nombre.trim(),
                                clienteDni = dni,
                                cantidad = cantidad,
                                fechaInicio = if (esAuto) fechaInicio.trim() else "",
                                fechaFin = if (esAuto) fechaFin.trim() else ""
                            )
                            if (guardada) {
                                Toast.makeText(
                                    contexto,
                                    "Reserva guardada correctamente.",
                                    Toast.LENGTH_SHORT
                                ).show()
                                NotificacionHelper.sonidoConfirmacion()
                                NotificacionHelper.mostrar(
                                    contexto,
                                    "Reserva registrada",
                                    tipo.etiqueta + ": " + servicio.nombre +
                                            " · Total " + formatoPrecio(tipo, total)
                                )
                                Sincronizador.enviarReserva(
                                    contexto,
                                    dbHelper.obtenerReserva(dbHelper.ultimoIdReserva())
                                )
                                RecordatorioReservaJob.programar(
                                    contexto,
                                    tipo.etiqueta + " " + servicio.nombre
                                )
                                onReservaExitosa()
                            } else {
                                mensaje = "No se pudo guardar la reserva."
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AzulTG)
            ) {
                Text("Confirmar reserva")
            }

            OutlinedButton(
                onClick = onVolver,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Cancelar")
            }
        }
    }
}
