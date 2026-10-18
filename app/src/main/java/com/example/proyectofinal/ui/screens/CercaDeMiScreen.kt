package com.example.proyectofinal.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.proyectofinal.viewmodel.CatalogoDestinos
import java.util.Locale

/**
 * Servicios basados en localización: solicita permisos, obtiene la ubicación
 * del equipo (GPS o red) y calcula el destino de TripGo más cercano.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CercaDeMiScreen(alVolver: () -> Unit) {
    val contexto = LocalContext.current

    var ubicacion by remember { mutableStateOf<Location?>(null) }
    var estado by remember {
        mutableStateOf("Pulsa «Obtener mi ubicación» para encontrar el destino más cercano.")
    }

    fun buscar() {
        estado = "Buscando tu ubicación..."
        obtenerUbicacion(contexto) { resultado ->
            if (resultado == null) {
                estado = "No se pudo obtener la ubicación. Activa el GPS e inténtalo de nuevo."
            } else {
                ubicacion = resultado
                estado = "Ubicación obtenida."
            }
        }
    }

    val lanzadorPermisos = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permisos ->
        if (permisos.values.any { it }) {
            buscar()
        } else {
            estado = "Sin permiso de ubicación no es posible calcular el destino más cercano."
        }
    }

    // Destinos ordenados por distancia a la ubicación actual.
    val cercanos = ubicacion?.let { u ->
        CatalogoDestinos.lista
            .map { it to distanciaKm(u.latitude, u.longitude, it.latitud, it.longitud) }
            .sortedBy { it.second }
    } ?: emptyList()

    Scaffold(
        topBar = {
            BarraTG(titulo = "Cerca de mí", alVolver = alVolver)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(FondoTG)
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Button(
                onClick = {
                    val concedido = ContextCompat.checkSelfPermission(
                        contexto, Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED ||
                            ContextCompat.checkSelfPermission(
                                contexto, Manifest.permission.ACCESS_COARSE_LOCATION
                            ) == PackageManager.PERMISSION_GRANTED

                    if (concedido) {
                        buscar()
                    } else {
                        lanzadorPermisos.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AzulTG)
            ) {
                Text("📍 Obtener mi ubicación")
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(text = estado, color = GrisTG)

            ubicacion?.let { u ->
                Text(
                    text = "Latitud: " + String.format(Locale.US, "%.5f", u.latitude) +
                            "  ·  Longitud: " + String.format(Locale.US, "%.5f", u.longitude),
                    color = TextoTG
                )
                TextButton(onClick = {
                    abrirMapa(contexto, "Mi ubicación", u.latitude, u.longitude)
                }) { Text("Ver mi ubicación en el mapa") }
            }

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(items = cercanos, key = { it.first.nombre }) { (destino, km) ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = destino.emoji + "  " + destino.nombre,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = TextoTG
                                )
                                Text(
                                    text = "A " + String.format(Locale.US, "%.0f", km) + " km",
                                    color = AzulTG
                                )
                            }
                            TextButton(onClick = {
                                abrirMapa(
                                    contexto, destino.nombre,
                                    destino.latitud, destino.longitud
                                )
                            }) { Text("Ver mapa") }
                        }
                    }
                }
            }
        }
    }
}

/** Distancia en kilómetros entre dos coordenadas (fórmula de Haversine). */
private fun distanciaKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val radioTierra = 6371.0
    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)
    val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
            Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
            Math.sin(dLon / 2) * Math.sin(dLon / 2)
    return radioTierra * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
}

/** Obtiene la última ubicación conocida o solicita una nueva actualización. */
@SuppressLint("MissingPermission")
private fun obtenerUbicacion(contexto: Context, alRecibir: (Location?) -> Unit) {
    val manager = contexto.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    val proveedor = when {
        manager.isProviderEnabled(LocationManager.GPS_PROVIDER) -> LocationManager.GPS_PROVIDER
        manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) -> LocationManager.NETWORK_PROVIDER
        else -> null
    }

    if (proveedor == null) {
        alRecibir(null)
        return
    }

    try {
        val ultima = manager.getLastKnownLocation(proveedor)
        if (ultima != null) {
            alRecibir(ultima)
            return
        }

        manager.requestLocationUpdates(
            proveedor,
            0L,
            0f,
            object : LocationListener {
                override fun onLocationChanged(location: Location) {
                    manager.removeUpdates(this)
                    alRecibir(location)
                }

                override fun onProviderEnabled(provider: String) {}

                override fun onProviderDisabled(provider: String) {}

                @Deprecated("Requerido en versiones antiguas de Android")
                override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
            }
        )
    } catch (e: SecurityException) {
        alRecibir(null)
    }
}
