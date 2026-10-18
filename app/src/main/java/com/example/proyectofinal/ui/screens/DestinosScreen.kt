package com.example.proyectofinal.ui.screens

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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectofinal.viewmodel.DestinosViewModel
import java.util.Locale

/**
 * Destinos: consume el servicio REST de clima con Retrofit y ofrece
 * accesos a Google Maps, a «Cerca de mí» (GPS) y a los sensores del equipo.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DestinosScreen(
    alVolver: () -> Unit,
    onCercaDeMi: () -> Unit,
    onSensores: () -> Unit,
    modeloVista: DestinosViewModel = viewModel()
) {
    val destinos by modeloVista.destinos.collectAsState()
    val cargando by modeloVista.cargando.collectAsState()
    val error by modeloVista.error.collectAsState()
    val contexto = LocalContext.current

    LaunchedEffect(Unit) {
        modeloVista.cargar()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Destinos y clima") },
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
                text = "Consulta el clima actual de cada destino antes de viajar.",
                color = GrisTG
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onCercaDeMi,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AzulTG)
                ) { Text("📍 Cerca de mí") }

                OutlinedButton(
                    onClick = onSensores,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) { Text("📳 Sensores") }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (cargando) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            error?.let { mensaje ->
                Text(text = mensaje, color = RojoTG)
                TextButton(onClick = { modeloVista.cargar() }) {
                    Text("Reintentar")
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item { VideoDestino() }

                items(items = destinos, key = { it.destino.nombre }) { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = item.destino.emoji, fontSize = 38.sp)

                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 12.dp)
                            ) {
                                Text(
                                    text = item.destino.nombre,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = TextoTG
                                )
                                Text(text = item.descripcion, color = GrisTG)
                                if (item.viento != null) {
                                    Text(
                                        text = "Viento: " +
                                                String.format(Locale.US, "%.1f", item.viento) +
                                                " km/h",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = GrisTG
                                    )
                                }
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = if (item.temperatura != null)
                                        String.format(Locale.US, "%.0f°C", item.temperatura)
                                    else "--",
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = AzulTG
                                )
                                TextButton(onClick = {
                                    abrirMapa(
                                        contexto,
                                        item.destino.nombre,
                                        item.destino.latitud,
                                        item.destino.longitud
                                    )
                                }) { Text("Ver mapa") }
                            }
                        }
                    }
                }
            }
        }
    }
}
