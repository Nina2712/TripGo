package com.example.proyectofinal.ui.screens

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.util.Locale

/**
 * Sensores del dispositivo: acelerómetro, sensor de luz y sensor de proximidad.
 * Se registran al abrir la pantalla y se liberan al salir (DisposableEffect).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SensoresScreen(alVolver: () -> Unit) {
    val contexto = LocalContext.current
    val manager = remember {
        contexto.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    val sensorAcelerometro = remember { manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) }
    val sensorLuz = remember { manager.getDefaultSensor(Sensor.TYPE_LIGHT) }
    val sensorProximidad = remember { manager.getDefaultSensor(Sensor.TYPE_PROXIMITY) }

    var ejeX by remember { mutableStateOf(0f) }
    var ejeY by remember { mutableStateOf(0f) }
    var ejeZ by remember { mutableStateOf(0f) }
    var luz by remember { mutableStateOf<Float?>(null) }
    var proximidad by remember { mutableStateOf<Float?>(null) }

    DisposableEffect(Unit) {
        val oyente = object : SensorEventListener {
            override fun onSensorChanged(evento: SensorEvent) {
                when (evento.sensor.type) {
                    Sensor.TYPE_ACCELEROMETER -> {
                        ejeX = evento.values[0]
                        ejeY = evento.values[1]
                        ejeZ = evento.values[2]
                    }
                    Sensor.TYPE_LIGHT -> luz = evento.values[0]
                    Sensor.TYPE_PROXIMITY -> proximidad = evento.values[0]
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, precision: Int) {}
        }

        if (sensorAcelerometro != null) {
            manager.registerListener(oyente, sensorAcelerometro, SensorManager.SENSOR_DELAY_UI)
        }
        if (sensorLuz != null) {
            manager.registerListener(oyente, sensorLuz, SensorManager.SENSOR_DELAY_UI)
        }
        if (sensorProximidad != null) {
            manager.registerListener(oyente, sensorProximidad, SensorManager.SENSOR_DELAY_UI)
        }

        onDispose { manager.unregisterListener(oyente) }
    }

    val magnitud = Math.sqrt((ejeX * ejeX + ejeY * ejeY + ejeZ * ejeZ).toDouble())

    Scaffold(
        topBar = {
            BarraTG(titulo = "Sensores del dispositivo", alVolver = alVolver)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(FondoTG)
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "Valores en tiempo real de los sensores de tu equipo. " +
                        "En el emulador algunos sensores pueden no estar disponibles.",
                color = GrisTG
            )

            TarjetaSensor(
                titulo = "📳 Acelerómetro",
                disponible = sensorAcelerometro != null,
                progreso = (magnitud / 20.0).toFloat().coerceIn(0f, 1f),
                lineas = listOf(
                    "X: " + String.format(Locale.US, "%.2f", ejeX) + " m/s²",
                    "Y: " + String.format(Locale.US, "%.2f", ejeY) + " m/s²",
                    "Z: " + String.format(Locale.US, "%.2f", ejeZ) + " m/s²",
                    if (magnitud > 15.0) "¡Estás agitando el teléfono!" else "Teléfono estable"
                )
            )

            TarjetaSensor(
                titulo = "💡 Sensor de luz",
                disponible = sensorLuz != null,
                progreso = luz?.let {
                    (Math.log(1.0 + it) / Math.log(10001.0)).toFloat().coerceIn(0f, 1f)
                },
                lineas = listOf(
                    "Iluminación: " +
                            (luz?.let { String.format(Locale.US, "%.0f", it) + " lx" } ?: "sin datos"),
                    when {
                        luz == null -> "Esperando lectura..."
                        luz!! < 20f -> "Ambiente oscuro: ideal para modo noche."
                        luz!! < 1000f -> "Iluminación interior normal."
                        else -> "Mucha luz: sube el brillo de la pantalla."
                    }
                )
            )

            TarjetaSensor(
                titulo = "🖐️ Sensor de proximidad",
                disponible = sensorProximidad != null,
                progreso = proximidad?.let {
                    val maximo = (sensorProximidad?.maximumRange ?: 5f).coerceAtLeast(1f)
                    (1f - it / maximo).coerceIn(0f, 1f)
                },
                lineas = listOf(
                    "Distancia: " +
                            (proximidad?.let { String.format(Locale.US, "%.1f", it) + " cm" } ?: "sin datos"),
                    when {
                        proximidad == null -> "Esperando lectura..."
                        proximidad!! < 3f -> "Objeto cercano detectado."
                        else -> "Sin objetos cerca."
                    }
                )
            )
        }
    }
}

@Composable
private fun TarjetaSensor(
    titulo: String,
    disponible: Boolean,
    lineas: List<String>,
    progreso: Float? = null
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = titulo,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextoTG
            )
            if (disponible) {
                if (progreso != null) {
                    LinearProgressIndicator(
                        progress = { progreso },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(50)),
                        color = AzulTG,
                        trackColor = AzulClaroTG
                    )
                }
                lineas.forEach { Text(text = it, color = GrisTG) }
            } else {
                Text(
                    text = "Este equipo no tiene este sensor. Es normal en el emulador: " +
                            "usa los tres puntos (⋮) del emulador → Virtual sensors para simularlo.",
                    color = GrisTG
                )
            }
        }
    }
}
