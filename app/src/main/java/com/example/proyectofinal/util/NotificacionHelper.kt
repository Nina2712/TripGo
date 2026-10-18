package com.example.proyectofinal.util

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat

/** Notificación local y sonido de confirmación al registrar una reserva. */
object NotificacionHelper {

    private const val CANAL_ID = "reservas_tripgo"

    fun mostrar(contexto: Context, titulo: String, mensaje: String) {
        if (Build.VERSION.SDK_INT >= 33 &&
            ContextCompat.checkSelfPermission(
                contexto, Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val manager =
            contexto.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= 26) {
            manager.createNotificationChannel(
                NotificationChannel(
                    CANAL_ID,
                    "Reservas de TripGo",
                    NotificationManager.IMPORTANCE_DEFAULT
                )
            )
        }

        val notificacion = NotificationCompat.Builder(contexto, CANAL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(titulo)
            .setContentText(mensaje)
            .setStyle(NotificationCompat.BigTextStyle().bigText(mensaje))
            .setAutoCancel(true)
            .build()

        manager.notify(System.currentTimeMillis().toInt(), notificacion)
    }

    fun sonidoConfirmacion() {
        try {
            ToneGenerator(AudioManager.STREAM_NOTIFICATION, 80)
                .startTone(ToneGenerator.TONE_PROP_ACK, 250)
        } catch (e: Exception) {
            // Si el dispositivo no permite reproducir el tono, se ignora.
        }
    }
}
