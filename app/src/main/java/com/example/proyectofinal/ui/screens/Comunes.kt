package com.example.proyectofinal.ui.screens

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.ui.graphics.Color
import com.example.proyectofinal.ui.model.TipoServicio
import java.util.Locale

internal val AzulTG = Color(0xFF1769FF)
internal val FondoTG = Color(0xFFF5F8FF)
internal val TextoTG = Color(0xFF172B4D)
internal val GrisTG = Color(0xFF64748B)
internal val AzulClaroTG = Color(0xFFE8F0FF)
internal val RojoTG = Color(0xFFC53030)

/** Da formato al precio con la moneda del tipo de servicio. */
internal fun formatoPrecio(tipo: TipoServicio, valor: Double): String {
    return tipo.moneda + " " + String.format(Locale.US, "%.2f", valor)
}

internal fun tipoDe(nombre: String): TipoServicio {
    return TipoServicio.values().firstOrNull { it.name == nombre } ?: TipoServicio.HOTEL
}

/** Abre la ubicación en Google Maps (o en el navegador si no hay aplicación de mapas). */
internal fun abrirMapa(contexto: Context, nombre: String, latitud: Double, longitud: Double) {
    val uri = Uri.parse("geo:$latitud,$longitud?q=$latitud,$longitud(" + Uri.encode(nombre) + ")")
    try {
        contexto.startActivity(Intent(Intent.ACTION_VIEW, uri))
    } catch (e: ActivityNotFoundException) {
        contexto.startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://www.google.com/maps/search/?api=1&query=$latitud,$longitud")
            )
        )
    }
}
