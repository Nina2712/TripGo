
package com.example.proyectofinal.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.proyectofinal.ui.model.AuthUiState

private val AzulTripGoHome = Color(0xFF1769FF)
private val FondoTripGoHome = Color(0xFFF5F8FF)

@Composable
fun HomeScreen(
    uiState: AuthUiState,
    onVuelos: () -> Unit,
    onHoteles: () -> Unit,
    onAutos: () -> Unit,
    onTours: () -> Unit,
    onDestinos: () -> Unit,
    onReservas: () -> Unit,
    onLogout: () -> Unit
) {
    val user = uiState.user
    val nombre = user?.displayName
        ?.substringBefore(" ")
        ?.takeIf { it.isNotBlank() }
        ?: "viajero"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoTripGoHome)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "✈ TripGo",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = AzulTripGoHome
            )

            TextButton(onClick = onLogout) {
                Text("Salir")
            }
        }

        Spacer(Modifier.height(20.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(
                containerColor = AzulTripGoHome
            )
        ) {
            Column(Modifier.padding(22.dp)) {
                Text(
                    text = "¡Hola, $nombre! 👋",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = "¿A dónde quieres ir hoy?",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White
                )

                Spacer(Modifier.height(12.dp))

                Button(
                    onClick = onDestinos,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Explorar destinos")
                }
            }
        }

        Spacer(Modifier.height(26.dp))

        Text(
            text = "Servicios de viaje",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(12.dp))

        ServicioCard(
            emoji = "✈️",
            titulo = "Vuelos",
            descripcion = "Consulta vuelos y precios",
            onClick = onVuelos
        )

        ServicioCard(
            emoji = "🏨",
            titulo = "Hoteles",
            descripcion = "Encuentra tu alojamiento",
            onClick = onHoteles
        )

        ServicioCard(
            emoji = "🚗",
            titulo = "Alquiler de autos",
            descripcion = "Explora vehículos disponibles",
            onClick = onAutos
        )

        ServicioCard(
            emoji = "🗺️",
            titulo = "Tours",
            descripcion = "Descubre actividades y experiencias",
            onClick = onTours
        )

        Spacer(Modifier.height(18.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onReservas() },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Column(Modifier.padding(18.dp)) {
                Text(
                    text = "🎫 Mis reservas",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(4.dp))

                Text("Consulta tus reservas y su estado.")
            }
        }

        Spacer(Modifier.height(20.dp))

        Text(
            text = "Tu próxima aventura te espera.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
    }
}

@Composable
private fun ServicioCard(
    emoji: String,
    titulo: String,
    descripcion: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = emoji,
                style = MaterialTheme.typography.headlineMedium
            )

            Column {
                Text(
                    text = titulo,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    text = descripcion,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
        }
    }
}
