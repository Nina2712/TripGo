package com.example.proyectofinal.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyectofinal.data.DatabaseHelper
import com.example.proyectofinal.ui.model.AuthUiState

@Composable
fun ProfileScreen(
    uiState: AuthUiState,
    dbHelper: DatabaseHelper,
    onInicio: () -> Unit,
    onExplorar: () -> Unit,
    onReservas: () -> Unit,
    onLogout: () -> Unit
) {
    val user = uiState.user
    val nombre = user?.displayName?.takeIf { it.isNotBlank() }
        ?: user?.email?.substringBefore("@")
        ?: "Viajero"
    val correo = user?.email?.takeIf { it.isNotBlank() } ?: "Sin correo registrado"
    val totalReservas = remember { dbHelper.obtenerReservas().size }

    var confirmarSalida by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = FondoTG,
        topBar = { BarraTG(titulo = "Mi perfil") },
        bottomBar = {
            BarraInferiorTG(
                actual = "perfil",
                onInicio = onInicio,
                onExplorar = onExplorar,
                onReservas = onReservas,
                onPerfil = {}
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(92.dp)
                    .background(degradadoDe(com.example.proyectofinal.ui.model.TipoServicio.HOTEL), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = nombre.take(1).uppercase(),
                    fontSize = 40.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
            }

            Spacer(Modifier.height(14.dp))

            Text(
                text = nombre,
                style = MaterialTheme.typography.titleLarge,
                color = TextoTG
            )
            Text(
                text = correo,
                style = MaterialTheme.typography.bodyMedium,
                color = GrisTG
            )

            Spacer(Modifier.height(22.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column {
                    FilaPerfil(
                        emoji = "🎫",
                        titulo = "Mis reservas",
                        detalle = if (totalReservas == 1) "1 reserva" else "$totalReservas reservas",
                        onClick = onReservas
                    )
                    FilaPerfil(
                        emoji = "🌦️",
                        titulo = "Destinos y clima",
                        detalle = "Explora el Perú",
                        onClick = onExplorar
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            OutlinedButton(
                onClick = { confirmarSalida = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Cerrar sesión", color = RojoTG, fontWeight = FontWeight.Bold)
            }

            StatusMessage(uiState = uiState)

            Spacer(Modifier.height(20.dp))

            Text(
                text = "TripGo · versión 1.0",
                style = MaterialTheme.typography.bodySmall,
                color = GrisTG
            )
        }
    }

    if (confirmarSalida) {
        AlertDialog(
            onDismissRequest = { confirmarSalida = false },
            title = { Text("Cerrar sesión") },
            text = { Text("¿Seguro que deseas salir de tu cuenta?") },
            confirmButton = {
                TextButton(onClick = {
                    confirmarSalida = false
                    onLogout()
                }) { Text("Salir", color = RojoTG) }
            },
            dismissButton = {
                TextButton(onClick = { confirmarSalida = false }) { Text("Cancelar") }
            }
        )
    }
}

@Composable
private fun FilaPerfil(
    emoji: String,
    titulo: String,
    detalle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .background(AzulClaroTG, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(text = emoji, fontSize = 20.sp)
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = titulo,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextoTG
            )
            Text(text = detalle, style = MaterialTheme.typography.bodyMedium, color = GrisTG)
        }
        Text(text = "›", fontSize = 24.sp, color = GrisTG)
    }
}
