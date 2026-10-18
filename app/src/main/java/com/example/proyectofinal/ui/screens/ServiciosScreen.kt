package com.example.proyectofinal.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyectofinal.R
import com.example.proyectofinal.data.DatabaseHelper
import com.example.proyectofinal.ui.model.Servicio
import com.example.proyectofinal.ui.model.TipoServicio

/**
 * Pantalla reutilizable para los catálogos de hoteles, autos y tours.
 * Lista personalizada (LazyColumn) con buscador y botón de reserva.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiciosScreen(
    tipo: TipoServicio,
    titulo: String,
    subtitulo: String,
    dbHelper: DatabaseHelper,
    alVolver: () -> Unit,
    onReservar: (Servicio) -> Unit
) {
    var busqueda by remember { mutableStateOf("") }

    val lista = remember(tipo) { dbHelper.obtenerServicios(tipo) }

    val filtrada = lista.filter {
        it.nombre.contains(busqueda, ignoreCase = true) ||
                it.ciudad.contains(busqueda, ignoreCase = true) ||
                it.detalle.contains(busqueda, ignoreCase = true)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(titulo) },
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
            Text(
                text = subtitulo,
                style = MaterialTheme.typography.bodyLarge,
                color = GrisTG
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = busqueda,
                onValueChange = { busqueda = it },
                label = { Text("Buscar por nombre o ciudad") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "${filtrada.size} ${tipo.plural} disponibles",
                style = MaterialTheme.typography.bodyMedium,
                color = GrisTG
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(items = filtrada, key = { it.id }) { servicio ->
                    TarjetaServicio(servicio = servicio, onReservar = onReservar)
                }

                if (filtrada.isEmpty()) {
                    item {
                        Text(
                            text = "No se encontraron resultados.",
                            modifier = Modifier.padding(16.dp),
                            color = GrisTG
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TarjetaServicio(
    servicio: Servicio,
    onReservar: (Servicio) -> Unit
) {
    val tipo = servicio.tipo

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = androidx.compose.ui.graphics.Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {

            if (tipo == TipoServicio.HOTEL) {
                Image(
                    painter = painterResource(id = imagenHotel(servicio.nombre)),
                    contentDescription = servicio.nombre,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .background(AzulClaroTG, RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = tipo.emoji, fontSize = 52.sp)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = emojiCiudad(servicio.ciudad) + "  " + servicio.ciudad,
                style = MaterialTheme.typography.labelLarge,
                color = AzulTG
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = servicio.nombre,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextoTG
            )

            if (servicio.detalle.isNotBlank()) {
                Text(
                    text = servicio.detalle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = GrisTG
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Precio por ${tipo.unidad}",
                        style = MaterialTheme.typography.bodySmall,
                        color = GrisTG
                    )
                    Text(
                        text = formatoPrecio(tipo, servicio.precio),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = AzulTG
                    )
                }

                Button(
                    onClick = { onReservar(servicio) },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AzulTG)
                ) {
                    Text("Reservar")
                }
            }
        }
    }
}

private fun emojiCiudad(ciudad: String): String {
    return when (ciudad.lowercase()) {
        "cusco" -> "🏔️"
        "arequipa" -> "🌋"
        "ica" -> "🏖️"
        "nazca" -> "🛩️"
        "piura" -> "🌴"
        "iquitos" -> "🌿"
        "lima" -> "🏙️"
        else -> "📍"
    }
}

private fun imagenHotel(nombre: String): Int {
    return when (nombre.lowercase()) {
        "palacio del inka" -> R.drawable.palacio_del_inka_cusco
        "casa andina premium" -> R.drawable.casa_andina_arequipa
        "aranwa paracas resort" -> R.drawable.arawa_paracas_ica
        "hotel dm nazca" -> R.drawable.hotel_dm_nazca
        "hotel hilton iquitos" -> R.drawable.hilton_iquitos
        "hotel costa del sol" -> R.drawable.costa_del_sol_piura
        else -> R.drawable.palacio_del_inka_cusco
    }
}
