package com.example.proyectofinal.ui.screens

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyectofinal.data.DatabaseHelper
import com.example.proyectofinal.ui.model.AuthUiState
import com.example.proyectofinal.ui.model.Servicio
import com.example.proyectofinal.ui.model.TipoServicio

private data class DestinoPopular(val ciudad: String, val hotel: Servicio, val desde: Double)

@Composable
fun HomeScreen(
    uiState: AuthUiState,
    dbHelper: DatabaseHelper,
    onVuelos: () -> Unit,
    onHoteles: () -> Unit,
    onAutos: () -> Unit,
    onTours: () -> Unit,
    onDestinos: () -> Unit,
    onReservas: () -> Unit,
    onPerfil: () -> Unit,
    onBuscarHoteles: (String) -> Unit
) {
    val user = uiState.user
    val nombre = user?.displayName
        ?.substringBefore(" ")
        ?.takeIf { it.isNotBlank() }
        ?: user?.email?.substringBefore("@")?.takeIf { it.isNotBlank() }
        ?: "viajero"

    var busqueda by remember { mutableStateOf("") }

    // La cabecera es azul oscuro: los íconos de la barra de estado se ven en blanco.
    val vista = LocalView.current
    DisposableEffect(Unit) {
        val ventana = (vista.context as? Activity)?.window
        val control = ventana?.let { WindowCompat.getInsetsController(it, vista) }
        control?.isAppearanceLightStatusBars = false
        onDispose { control?.isAppearanceLightStatusBars = true }
    }

    // Destinos populares: el hotel más barato de cada ciudad (datos reales del catálogo).
    val contexto = LocalContext.current
    val destinos = remember {
        dbHelper.obtenerServicios(TipoServicio.HOTEL)
            .groupBy { it.ciudad }
            .map { (ciudad, hoteles) ->
                // La portada usa un hotel con foto (si hay); el precio es el más bajo de la ciudad.
                val conFoto = hoteles.filter { recursoImagen(contexto, it) != 0 }
                val portada = conFoto.ifEmpty { hoteles }.minByOrNull { it.precio }!!
                DestinoPopular(ciudad, portada, hoteles.minOf { it.precio })
            }
    }
    val totalReservas = remember { dbHelper.obtenerReservas().size }

    Scaffold(
        containerColor = FondoTG,
        bottomBar = {
            BarraInferiorTG(
                actual = "inicio",
                onInicio = {},
                onExplorar = onDestinos,
                onReservas = onReservas,
                onPerfil = onPerfil
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // ---------- Cabecera con degradado ----------
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(listOf(Color(0xFF0B3FA8), AzulTG)),
                        RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
                    )
                    .statusBarsPadding()
                    .padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "✈ TripGo",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )

                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f))
                            .clickable { onPerfil() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = nombre.take(1).uppercase(),
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(Modifier.height(22.dp))

                Text(
                    text = "¡Hola, $nombre! 👋",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
                Text(
                    text = "¿A dónde quieres ir hoy?",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White.copy(alpha = 0.85f)
                )

                Spacer(Modifier.height(18.dp))

                CampoBusquedaTG(
                    valor = busqueda,
                    onCambio = { busqueda = it },
                    placeholder = "Busca un hotel o una ciudad",
                    onBuscar = { onBuscarHoteles(busqueda.trim()) }
                )
            }

            Spacer(Modifier.height(22.dp))

            // ---------- Servicios ----------
            Text(
                text = "Servicios de viaje",
                style = MaterialTheme.typography.titleLarge,
                color = TextoTG,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AccesoServicio(TipoServicio.VUELO, "Vuelos", Color(0xFFE8F0FF), onVuelos, Modifier.weight(1f))
                AccesoServicio(TipoServicio.HOTEL, "Hoteles", Color(0xFFEDE9FE), onHoteles, Modifier.weight(1f))
                AccesoServicio(TipoServicio.AUTO, "Autos", Color(0xFFFFEDD5), onAutos, Modifier.weight(1f))
                AccesoServicio(TipoServicio.TOUR, "Tours", Color(0xFFD1FAE5), onTours, Modifier.weight(1f))
            }

            Spacer(Modifier.height(26.dp))

            // ---------- Destinos populares ----------
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Destinos populares",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextoTG
                )
                Text(
                    text = "Ver hoteles",
                    style = MaterialTheme.typography.labelLarge,
                    color = AzulTG,
                    modifier = Modifier.clickable { onHoteles() }
                )
            }
            Spacer(Modifier.height(12.dp))
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(items = destinos, key = { it.ciudad }) { destino ->
                    TarjetaDestino(destino) { onBuscarHoteles(destino.ciudad) }
                }
            }

            Spacer(Modifier.height(26.dp))

            // ---------- Accesos rápidos ----------
            Column(
                modifier = Modifier.padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                BannerAcceso(
                    emoji = "🎫",
                    titulo = "Mis reservas",
                    detalle = when (totalReservas) {
                        0 -> "Aún no tienes reservas"
                        1 -> "Tienes 1 reserva registrada"
                        else -> "Tienes $totalReservas reservas registradas"
                    },
                    onClick = onReservas
                )
                BannerAcceso(
                    emoji = "🌦️",
                    titulo = "Destinos y clima",
                    detalle = "Consulta el clima y descubre qué hay cerca de ti",
                    onClick = onDestinos
                )
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun AccesoServicio(
    tipo: TipoServicio,
    titulo: String,
    fondo: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(fondo, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = tipo.emoji, fontSize = 24.sp)
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text = titulo,
                style = MaterialTheme.typography.labelLarge,
                color = TextoTG
            )
        }
    }
}

@Composable
private fun TarjetaDestino(destino: DestinoPopular, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(170.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(22.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        ImagenServicio(servicio = destino.hotel, alto = 220.dp) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.65f)),
                            startY = 200f
                        )
                    )
            )
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(14.dp)
            ) {
                Text(
                    text = emojiCiudad(destino.ciudad) + " " + destino.ciudad,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
                Text(
                    text = "Hoteles desde " + formatoPrecio(TipoServicio.HOTEL, destino.desde),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
        }
    }
}

@Composable
private fun BannerAcceso(
    emoji: String,
    titulo: String,
    detalle: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .background(AzulClaroTG, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = emoji, fontSize = 22.sp)
            }
            Column {
                Text(
                    text = titulo,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextoTG
                )
                Text(
                    text = detalle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = GrisTG
                )
            }
        }
    }
}
