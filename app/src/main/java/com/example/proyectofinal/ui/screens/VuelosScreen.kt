
package com.example.proyectofinal.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectofinal.ui.model.Vuelo
import com.example.proyectofinal.viewmodel.VueloViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

private val AzulTripGo = Color(0xFF1769FF)
private val FondoTripGo = Color(0xFFF5F8FF)
private val TextoTripGo = Color(0xFF172B4D)
private val GrisTripGo = Color(0xFF64748B)
private val AzulClaroTripGo = Color(0xFFE8F0FF)

@Composable
fun VuelosScreen(
    alVolver: () -> Unit,
    onReservar: (Vuelo) -> Unit = {},
    modeloVista: VueloViewModel = viewModel()
) {
    val vuelos by modeloVista.vuelos.collectAsState()
    val cargando by modeloVista.cargando.collectAsState()
    val error by modeloVista.error.collectAsState()
    val contexto = LocalContext.current

    var filtroDestino by remember { mutableStateOf("Todos") }
    var ordenPrecio by remember { mutableStateOf("Próximos") }
    val destinos = remember(vuelos) { vuelos.map { it.destino }.distinct().sorted() }
    val visibles = vuelos
        .filter { filtroDestino == "Todos" || it.destino == filtroDestino }
        .let {
            when (ordenPrecio) {
                "Más baratos" -> it.sortedBy { v -> v.precio }
                else -> it
            }
        }

    LaunchedEffect(Unit) {
        modeloVista.cargarVuelos()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoTripGo)
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        // Encabezado con la identidad de TripGo
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "✈ TripGo",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = AzulTripGo
            )

            TextButton(onClick = alVolver) {
                Text(
                    text = "← Volver",
                    color = AzulTripGo
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Vuelos disponibles",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = TextoTripGo
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Encuentra tu próximo destino",
            style = MaterialTheme.typography.bodyLarge,
            color = GrisTripGo
        )

        Spacer(modifier = Modifier.height(20.dp))

        when {
            cargando -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CircularProgressIndicator(color = AzulTripGo)
                        Text(
                            text = "Buscando vuelos...",
                            color = GrisTripGo
                        )
                    }
                }
            }

            error != null -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "No pudimos cargar los vuelos",
                        color = TextoTripGo,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = error ?: "",
                        color = GrisTripGo
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { modeloVista.cargarVuelos() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AzulTripGo
                        )
                    ) {
                        Text("Reintentar")
                    }
                }
            }

            vuelos.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No hay vuelos activos registrados.",
                        color = GrisTripGo
                    )
                }
            }

            else -> {
                FilaChipsTG(
                    opciones = listOf("Todos") + destinos,
                    seleccion = filtroDestino,
                    onSeleccion = { filtroDestino = it }
                )

                FilaChipsTG(
                    opciones = listOf("Próximos", "Más baratos"),
                    seleccion = ordenPrecio,
                    onSeleccion = { ordenPrecio = it }
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "${visibles.size} vuelos encontrados",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = TextoTripGo
                )

                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(visibles) { vuelo ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White
                            ),
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = 3.dp
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(18.dp),
                                verticalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                // Etiqueta superior
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .background(
                                                AzulClaroTripGo,
                                                RoundedCornerShape(8.dp)
                                            )
                                            .padding(
                                                horizontal = 10.dp,
                                                vertical = 6.dp
                                            )
                                    ) {
                                        Text(
                                            text = "✈ VUELO",
                                            color = AzulTripGo,
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    Box(
                                        modifier = Modifier
                                            .background(
                                                Color(0xFFE8F5E9),
                                                CircleShape
                                            )
                                            .padding(
                                                horizontal = 10.dp,
                                                vertical = 5.dp
                                            )
                                    ) {
                                        Text(
                                            text = "Disponible",
                                            color = Color(0xFF26733A),
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    }
                                }

                                // Ruta y horarios
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(
                                            text = vuelo.fechaHoraSalida
                                                ?.toDate()
                                                ?.let { formatearHora(it) }
                                                ?: "--:--",
                                            style = MaterialTheme.typography.headlineSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = TextoTripGo
                                        )

                                        Text(
                                            text = vuelo.origen,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = GrisTripGo
                                        )
                                    }

                                    Column(
                                        modifier = Modifier.weight(1.2f),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = formatearDuracion(
                                                vuelo.duracionMinutos
                                            ),
                                            style = MaterialTheme.typography.labelMedium,
                                            color = GrisTripGo
                                        )

                                        Spacer(modifier = Modifier.height(6.dp))

                                        Row(
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .height(1.dp)
                                                    .background(Color(0xFFCBD5E1))
                                            )

                                            Text(
                                                text = " ✈ ",
                                                color = AzulTripGo
                                            )

                                            Box(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .height(1.dp)
                                                    .background(Color(0xFFCBD5E1))
                                            )
                                        }

                                        Text(
                                            text = "Ruta directa*",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = GrisTripGo
                                        )
                                    }

                                    Column(
                                        modifier = Modifier.weight(1f),
                                        horizontalAlignment = Alignment.End
                                    ) {
                                        Text(
                                            text = vuelo.fechaHoraLlegada
                                                ?.toDate()
                                                ?.let { formatearHora(it) }
                                                ?: "--:--",
                                            style = MaterialTheme.typography.headlineSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = TextoTripGo
                                        )

                                        Text(
                                            text = vuelo.destino,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = GrisTripGo
                                        )
                                    }
                                }

                                // Fecha del viaje
                                vuelo.fechaHoraSalida?.toDate()?.let { fecha ->
                                    Text(
                                        text = "📅 ${formatearFecha(fecha)}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = GrisTripGo
                                    )
                                }

                                // Separador
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(1.dp)
                                        .background(Color(0xFFE2E8F0))
                                )

                                // Precio y botón
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "Precio de demostración",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = GrisTripGo
                                        )

                                        Text(
                                            text = "$${"%.2f".format(Locale.US, vuelo.precio)}",
                                            style = MaterialTheme.typography.headlineSmall,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = AzulTripGo
                                        )
                                    }

                                    Button(
                                        onClick = { onReservar(vuelo) },
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = AzulTripGo
                                        )
                                    ) {
                                        Text("Reservar")
                                    }
                                }
                            }
                        }
                    }
                }

                Text(
                    text = "* Ruta directa referencial. Catálogo ficticio para demostración.",
                    style = MaterialTheme.typography.labelSmall,
                    color = GrisTripGo,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

private fun formatearHora(fecha: Date): String {
    val formato = SimpleDateFormat("HH:mm", Locale("es", "PE"))
    formato.timeZone = TimeZone.getTimeZone("America/Lima")
    return formato.format(fecha)
}

private fun formatearFecha(fecha: Date): String {
    val formato = SimpleDateFormat("dd 'de' MMMM 'de' yyyy", Locale("es", "PE"))
    formato.timeZone = TimeZone.getTimeZone("America/Lima")
    return formato.format(fecha)
}

private fun formatearDuracion(minutos: Long): String {
    val horas = minutos / 60
    val minutosRestantes = minutos % 60

    return if (horas > 0) {
        "${horas} h ${minutosRestantes} min"
    } else {
        "$minutos min"
    }
}
