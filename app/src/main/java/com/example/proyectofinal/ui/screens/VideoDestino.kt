package com.example.proyectofinal.ui.screens

import android.net.Uri
import android.widget.MediaController
import android.widget.VideoView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.proyectofinal.R

/**
 * Tema 11 del manual (Multimedia: sonido y video).
 * Reproduce un video local guardado en res/raw usando VideoView, que internamente
 * utiliza MediaPlayer, con MediaController para los controles de reproducción.
 *
 * Para cambiar el video: reemplaza app/src/main/res/raw/video_destinos.mp4
 * por otro archivo mp4 con el mismo nombre.
 */
@Composable
fun VideoDestino(modifier: Modifier = Modifier) {
    val contexto = LocalContext.current
    val uri = remember {
        Uri.parse("android.resource://${contexto.packageName}/${R.raw.video_destinos}")
    }
    val vista = remember { VideoView(contexto) }

    DisposableEffect(Unit) {
        onDispose {
            vista.stopPlayback()
        }
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = "🎬 Video de presentación",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextoTG
            )
            Text(
                text = "Toca el video para ver los controles de reproducción.",
                color = GrisTG,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 2.dp, bottom = 10.dp)
            )
            AndroidView(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(12.dp)),
                factory = {
                    vista.apply {
                        val controles = MediaController(contexto)
                        controles.setAnchorView(this)
                        setMediaController(controles)
                        setVideoURI(uri)
                        setOnPreparedListener { mp ->
                            mp.isLooping = false
                            seekTo(1)
                        }
                    }
                }
            )
        }
    }
}
