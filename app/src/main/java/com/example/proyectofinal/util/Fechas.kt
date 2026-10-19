package com.example.proyectofinal.util

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** Utilidades para validar fechas con formato AAAA-MM-DD (alquiler de autos). */
object Fechas {

    private const val DIA_EN_MS = 24L * 60 * 60 * 1000

    private fun formato(): SimpleDateFormat {
        val f = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        f.isLenient = false
        return f
    }

    /** Devuelve la fecha si el texto es válido y exacto; si no, null. */
    fun parsear(texto: String): Date? {
        val limpio = texto.trim()
        return try {
            val f = formato()
            val fecha = f.parse(limpio)
            if (fecha != null && f.format(fecha) == limpio) fecha else null
        } catch (e: ParseException) {
            null
        }
    }

    /** Devuelve un mensaje de error o null si el rango es válido. */
    fun validarRango(inicio: String, fin: String, permitirPasado: Boolean = false): String? {
        val fechaInicio = parsear(inicio)
            ?: return "La fecha de inicio debe tener el formato AAAA-MM-DD."
        val fechaFin = parsear(fin)
            ?: return "La fecha de fin debe tener el formato AAAA-MM-DD."

        if (!fechaFin.after(fechaInicio)) {
            return "La fecha de fin debe ser posterior a la de inicio."
        }
        if (!permitirPasado && inicio.trim() < formato().format(Date())) {
            return "La fecha de inicio no puede ser anterior a hoy."
        }
        return null
    }

    /** Número de días entre dos fechas válidas (0 si alguna no es válida). */
    fun dias(inicio: String, fin: String): Int {
        val a = parsear(inicio) ?: return 0
        val b = parsear(fin) ?: return 0
        return ((b.time - a.time) / DIA_EN_MS).toInt()
    }
}
