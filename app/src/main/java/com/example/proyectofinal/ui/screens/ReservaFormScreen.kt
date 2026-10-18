package com.example.proyectofinal.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyectofinal.data.DatabaseHelper
import com.example.proyectofinal.data.Sincronizador
import com.example.proyectofinal.network.RedApi
import com.example.proyectofinal.ui.model.Servicio
import com.example.proyectofinal.ui.model.TipoServicio
import com.example.proyectofinal.ui.theme.BordeSuave
import com.example.proyectofinal.util.Fechas
import com.example.proyectofinal.util.NotificacionHelper
import com.example.proyectofinal.util.RecordatorioReservaJob
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * Formulario de reserva para cualquier servicio (vuelo, hotel, auto o tour).
 * - Valida nombre, DNI (8 dígitos) y cantidad.
 * - En autos se eligen fechas de inicio y fin con calendario y se evitan cruces.
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
    var cantidadElegida by remember { mutableStateOf(1) }
    var fechaInicio by remember { mutableStateOf("") }
    var fechaFin by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf("") }
    var calendario by remember { mutableStateOf<String?>(null) } // "inicio" | "fin" | null

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
        if (esAuto && fechaInicio.isNotBlank() && fechaFin.isNotBlank())
            Fechas.validarRango(fechaInicio, fechaFin) else null

    val cantidad: Int = when {
        esAuto && fechaInicio.isNotBlank() && fechaFin.isNotBlank() && errorFechas == null ->
            Fechas.dias(fechaInicio, fechaFin)
        esAuto -> 0
        else -> cantidadElegida
    }
    val total = servicio.precio * cantidad

    // Equivalencia en la otra moneda con el tipo de cambio obtenido.
    val equivalencia: String? = tasaPen?.let { tasa ->
        if (tipo == TipoServicio.VUELO) {
            "≈ S/ " + String.format(Locale.US, "%.2f", total * tasa)
        } else {
            "≈ US$ " + String.format(Locale.US, "%.2f", total / tasa)
        }
    }

    val colorCampos = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = Color.White,
        unfocusedContainerColor = Color.White,
        focusedBorderColor = AzulTG,
        unfocusedBorderColor = BordeSuave
    )

    Scaffold(
        containerColor = FondoTG,
        topBar = { BarraTG(titulo = "Reservar ${tipo.etiqueta.lowercase()}", alVolver = onVolver) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // ---------- Resumen del servicio ----------
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column {
                    if (tipo != TipoServicio.VUELO) {
                        ImagenServicio(servicio = servicio, alto = 150.dp)
                    }
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = if (tipo == TipoServicio.VUELO) tipo.emoji + "  " + servicio.nombre
                            else servicio.nombre,
                            style = MaterialTheme.typography.titleLarge,
                            color = TextoTG
                        )
                        Text(
                            text = emojiCiudad(servicio.ciudad) + " " + servicio.ciudad,
                            color = GrisTG
                        )
                        if (servicio.detalle.isNotBlank()) {
                            Text(text = servicio.detalle, color = GrisTG)
                        }
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = "Precio por ${tipo.unidad}: " +
                                    formatoPrecio(tipo, servicio.precio),
                            color = AzulTG,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // ---------- Datos del cliente ----------
            Text(
                text = "Datos del cliente",
                style = MaterialTheme.typography.titleMedium,
                color = TextoTG
            )

            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre completo") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = colorCampos,
                singleLine = true
            )

            OutlinedTextField(
                value = dni,
                onValueChange = {
                    if (it.all(Char::isDigit) && it.length <= 8) dni = it
                },
                label = { Text("DNI (8 dígitos)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = colorCampos,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )

            // ---------- Fechas o cantidad ----------
            Text(
                text = if (esAuto) "Fechas de alquiler" else "Cantidad de ${tipo.unidades}",
                style = MaterialTheme.typography.titleMedium,
                color = TextoTG
            )

            if (esAuto) {
                OutlinedButton(
                    onClick = { calendario = "inicio" },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(
                        text = "📅  Inicio: " + fechaInicio.ifBlank { "elegir fecha" },
                        color = TextoTG
                    )
                }
                OutlinedButton(
                    onClick = { calendario = "fin" },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(
                        text = "📅  Fin: " + fechaFin.ifBlank { "elegir fecha" },
                        color = TextoTG
                    )
                }
                if (errorFechas != null) {
                    Text(text = errorFechas, color = RojoTG)
                } else if (cantidad > 0) {
                    Text(text = "Días de alquiler: $cantidad", color = GrisTG)
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = { if (cantidadElegida > 1) cantidadElegida-- },
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.size(52.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) { Text("−", fontSize = 24.sp) }

                    Text(
                        text = cantidadElegida.toString(),
                        style = MaterialTheme.typography.headlineSmall,
                        color = TextoTG,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )

                    Button(
                        onClick = { if (cantidadElegida < 30) cantidadElegida++ },
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.size(52.dp),
                        contentPadding = PaddingValues(0.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AzulTG)
                    ) { Text("+", fontSize = 24.sp) }
                }
            }

            // ---------- Total ----------
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = AzulClaroTG)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Total a pagar", color = GrisTG)
                    Text(
                        text = formatoPrecio(tipo, total),
                        style = MaterialTheme.typography.headlineMedium,
                        color = AzulTG
                    )
                    if (equivalencia != null && total > 0) {
                        Text(
                            text = "$equivalencia (tipo de cambio en línea)",
                            color = GrisTG,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            if (mensaje.isNotBlank()) {
                Text(
                    text = mensaje,
                    color = RojoTG,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFFDECEC), RoundedCornerShape(12.dp))
                        .padding(12.dp)
                )
            }

            Button(
                onClick = {
                    when {
                        nombre.isBlank() ->
                            mensaje = "Ingresa el nombre del cliente."

                        dni.length != 8 ->
                            mensaje = "El DNI debe tener 8 dígitos."

                        esAuto && (fechaInicio.isBlank() || fechaFin.isBlank()) ->
                            mensaje = "Elige las fechas de inicio y fin del alquiler."

                        esAuto && errorFechas != null ->
                            mensaje = errorFechas

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
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AzulTG)
            ) {
                Text("Confirmar reserva", fontWeight = FontWeight.Bold)
            }

            TextButton(
                onClick = onVolver,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cancelar", color = GrisTG)
            }
        }
    }

    // ---------- Calendario ----------
    calendario?.let { cual ->
        val hoyUtc = remember { hoyEnUtc() }
        val minimo = if (cual == "fin") {
            val inicio = fechaInicio.takeIf { it.isNotBlank() }?.let { aMillisUtc(it) }
            (inicio ?: hoyUtc) + 24L * 60 * 60 * 1000
        } else {
            hoyUtc
        }
        val estado = rememberDatePickerState(
            selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean = utcTimeMillis >= minimo
            }
        )

        DatePickerDialog(
            onDismissRequest = { calendario = null },
            confirmButton = {
                TextButton(onClick = {
                    estado.selectedDateMillis?.let { ms ->
                        val texto = deMillisUtc(ms)
                        if (cual == "inicio") {
                            fechaInicio = texto
                            // Si el fin quedó antes del nuevo inicio, se vuelve a pedir.
                            if (fechaFin.isNotBlank() && fechaFin <= texto) fechaFin = ""
                        } else {
                            fechaFin = texto
                        }
                    }
                    calendario = null
                }) { Text("Aceptar") }
            },
            dismissButton = {
                TextButton(onClick = { calendario = null }) { Text("Cancelar") }
            }
        ) {
            DatePicker(state = estado)
        }
    }
}

private fun formatoUtc(): SimpleDateFormat {
    val f = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    f.timeZone = TimeZone.getTimeZone("UTC")
    return f
}

/** Medianoche UTC del día de hoy (según la fecha local), como usa el DatePicker. */
private fun hoyEnUtc(): Long {
    val hoyLocal = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
    return aMillisUtc(hoyLocal) ?: 0L
}

private fun aMillisUtc(texto: String): Long? =
    try {
        formatoUtc().parse(texto)?.time
    } catch (e: Exception) {
        null
    }

private fun deMillisUtc(ms: Long): String = formatoUtc().format(Date(ms))
