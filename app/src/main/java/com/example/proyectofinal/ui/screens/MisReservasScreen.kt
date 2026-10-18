package com.example.proyectofinal.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
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
import com.example.proyectofinal.data.Sincronizador
import com.example.proyectofinal.ui.model.Reserva
import com.example.proyectofinal.util.Fechas

/**
 * Pantalla «Mis reservas»: lista las reservas guardadas en SQLite con
 * búsqueda, modificación y eliminación.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MisReservasScreen(
    dbHelper: DatabaseHelper,
    alVolver: () -> Unit
) {
    val contexto = LocalContext.current
    var busqueda by remember { mutableStateOf("") }
    var recarga by remember { mutableStateOf(0) }
    var editando by remember { mutableStateOf<Reserva?>(null) }
    var eliminando by remember { mutableStateOf<Reserva?>(null) }

    val reservas = remember(busqueda, recarga) {
        dbHelper.obtenerReservas(busqueda)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis reservas") },
                navigationIcon = {
                    TextButton(onClick = alVolver) { Text("← Volver") }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(FondoTG)
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = busqueda,
                onValueChange = { busqueda = it },
                label = { Text("Buscar por cliente, DNI, servicio o ciudad") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "${reservas.size} reservas registradas",
                style = MaterialTheme.typography.bodyMedium,
                color = GrisTG
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(items = reservas, key = { it.idReserva }) { reserva ->
                    TarjetaReserva(
                        reserva = reserva,
                        onEditar = { editando = reserva },
                        onEliminar = { eliminando = reserva }
                    )
                }

                if (reservas.isEmpty()) {
                    item {
                        Text(
                            text = "Aún no tienes reservas. Reserva un vuelo, hotel, auto o tour desde el inicio.",
                            modifier = Modifier.padding(16.dp),
                            color = GrisTG
                        )
                    }
                }
            }
        }
    }

    editando?.let { reserva ->
        EditarReservaDialog(
            reserva = reserva,
            dbHelper = dbHelper,
            onCancelar = { editando = null },
            onGuardar = { nombre, dni, cantidad, inicio, fin ->
                dbHelper.actualizarReserva(
                    reserva.idReserva, nombre, dni, cantidad, inicio, fin
                )
                Sincronizador.enviarReserva(
                    contexto, dbHelper.obtenerReserva(reserva.idReserva)
                )
                editando = null
                recarga++
            }
        )
    }

    eliminando?.let { reserva ->
        AlertDialog(
            onDismissRequest = { eliminando = null },
            title = { Text("Eliminar reserva") },
            text = {
                Text("¿Deseas eliminar la reserva de ${reserva.clienteNombre} (${reserva.servicio})?")
            },
            confirmButton = {
                TextButton(onClick = {
                    dbHelper.eliminarReserva(reserva.idReserva)
                    Sincronizador.eliminarReserva(contexto, reserva.idReserva)
                    eliminando = null
                    recarga++
                }) { Text("Eliminar", color = RojoTG) }
            },
            dismissButton = {
                TextButton(onClick = { eliminando = null }) { Text("Cancelar") }
            }
        )
    }
}

@Composable
private fun TarjetaReserva(
    reserva: Reserva,
    onEditar: () -> Unit,
    onEliminar: () -> Unit
) {
    val tipo = tipoDe(reserva.tipo)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = tipo.emoji + "  " + reserva.servicio,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextoTG
            )
            Text(text = "Destino: ${reserva.ciudad}", color = GrisTG)
            Text(text = "Cliente: ${reserva.clienteNombre}", color = TextoTG)
            Text(text = "DNI: ${reserva.clienteDni}", color = GrisTG)
            if (reserva.fechaInicio.isNotBlank()) {
                Text(
                    text = "Del ${reserva.fechaInicio} al ${reserva.fechaFin}",
                    color = GrisTG
                )
            }
            Text(
                text = "Cantidad: ${reserva.cantidad} " +
                        (if (reserva.cantidad == 1) tipo.unidad else tipo.unidades),
                color = GrisTG
            )
            Text(
                text = "Total: " + formatoPrecio(tipo, reserva.precioTotal),
                fontWeight = FontWeight.ExtraBold,
                color = AzulTG
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onEditar) { Text("Editar") }
                TextButton(onClick = onEliminar) { Text("Eliminar", color = RojoTG) }
            }
        }
    }
}

@Composable
private fun EditarReservaDialog(
    reserva: Reserva,
    dbHelper: DatabaseHelper,
    onCancelar: () -> Unit,
    onGuardar: (String, String, Int, String, String) -> Unit
) {
    val esAuto = reserva.tipo == "AUTO"

    var nombre by remember(reserva.idReserva) { mutableStateOf(reserva.clienteNombre) }
    var dni by remember(reserva.idReserva) { mutableStateOf(reserva.clienteDni) }
    var cantidadTexto by remember(reserva.idReserva) { mutableStateOf(reserva.cantidad.toString()) }
    var inicio by remember(reserva.idReserva) { mutableStateOf(reserva.fechaInicio) }
    var fin by remember(reserva.idReserva) { mutableStateOf(reserva.fechaFin) }
    var error by remember(reserva.idReserva) { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text("Editar reserva") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre completo") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = dni,
                    onValueChange = {
                        if (it.all(Char::isDigit) && it.length <= 8) dni = it
                    },
                    label = { Text("DNI (8 dígitos)") },
                    singleLine = true
                )
                if (esAuto) {
                    OutlinedTextField(
                        value = inicio,
                        onValueChange = { inicio = it },
                        label = { Text("Inicio (AAAA-MM-DD)") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = fin,
                        onValueChange = { fin = it },
                        label = { Text("Fin (AAAA-MM-DD)") },
                        singleLine = true
                    )
                } else {
                    OutlinedTextField(
                        value = cantidadTexto,
                        onValueChange = {
                            if (it.all(Char::isDigit) && it.length <= 2) cantidadTexto = it
                        },
                        label = { Text("Cantidad") },
                        singleLine = true
                    )
                }
                if (error.isNotBlank()) {
                    Text(text = error, color = RojoTG)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val errorFechas =
                    if (esAuto) Fechas.validarRango(inicio, fin, permitirPasado = true) else null
                val cantidad =
                    if (esAuto) Fechas.dias(inicio, fin) else (cantidadTexto.toIntOrNull() ?: 0)

                when {
                    nombre.isBlank() -> error = "Ingresa el nombre del cliente."
                    dni.length != 8 -> error = "El DNI debe tener 8 dígitos."
                    esAuto && errorFechas != null -> error = errorFechas ?: ""
                    esAuto && dbHelper.existeCruceAuto(
                        reserva.servicio, reserva.ciudad,
                        inicio.trim(), fin.trim(), reserva.idReserva
                    ) -> error = "El auto ya está reservado para esas fechas."
                    cantidad < 1 || cantidad > 30 -> error = "La cantidad debe estar entre 1 y 30."
                    else -> onGuardar(
                        nombre.trim(), dni, cantidad,
                        if (esAuto) inicio.trim() else "",
                        if (esAuto) fin.trim() else ""
                    )
                }
            }) { Text("Guardar") }
        },
        dismissButton = {
            TextButton(onClick = onCancelar) { Text("Cancelar") }
        }
    )
}
