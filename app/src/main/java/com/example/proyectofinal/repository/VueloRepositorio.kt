package com.example.proyectofinal.repository

import com.example.proyectofinal.ui.model.Vuelo
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.Calendar

class VueloRepositorio {

    private val baseDatos = FirebaseFirestore.getInstance()

    /**
     * Vuelos de Firestore (colección "vuelos") más una oferta base local para que
     * siempre haya opciones, también sin internet. Si Firestore ya tiene una ruta,
     * no se repite la local.
     */
    suspend fun obtenerVuelos(): List<Vuelo> {
        val remotos = try {
            baseDatos
                .collection("vuelos")
                .whereEqualTo("activo", true)
                .get()
                .await()
                .documents
                .mapNotNull { documento -> documento.toObject(Vuelo::class.java) }
        } catch (e: Exception) {
            emptyList()
        }

        val rutasRemotas = remotos
            .map { it.origen.trim().lowercase() to it.destino.trim().lowercase() }
            .toSet()

        val locales = ofertaLocal().filter {
            (it.origen.lowercase() to it.destino.lowercase()) !in rutasRemotas
        }

        return (remotos + locales).sortedBy { it.fechaHoraSalida?.seconds ?: Long.MAX_VALUE }
    }

    private fun salida(dias: Int, hora: Int, minuto: Int): Timestamp {
        val c = Calendar.getInstance()
        c.add(Calendar.DAY_OF_YEAR, dias)
        c.set(Calendar.HOUR_OF_DAY, hora)
        c.set(Calendar.MINUTE, minuto)
        c.set(Calendar.SECOND, 0)
        c.set(Calendar.MILLISECOND, 0)
        return Timestamp(c.time)
    }

    private fun vuelo(
        origen: String,
        destino: String,
        precio: Double,
        minutos: Long,
        dias: Int,
        hora: Int,
        minuto: Int
    ): Vuelo {
        val salida = salida(dias, hora, minuto)
        val llegada = Timestamp(salida.seconds + minutos * 60, 0)
        return Vuelo(
            origen = origen,
            destino = destino,
            precio = precio,
            fechaHoraSalida = salida,
            fechaHoraLlegada = llegada,
            duracionMinutos = minutos,
            activo = true
        )
    }

    private fun ofertaLocal(): List<Vuelo> = listOf(
        vuelo("Lima", "Cusco", 89.0, 90, 1, 6, 30),
        vuelo("Lima", "Arequipa", 79.0, 105, 1, 8, 0),
        vuelo("Lima", "Iquitos", 120.0, 120, 1, 9, 15),
        vuelo("Lima", "Piura", 98.0, 105, 1, 11, 30),
        vuelo("Cusco", "Lima", 92.0, 90, 2, 15, 10),
        vuelo("Arequipa", "Lima", 81.0, 105, 2, 17, 45),
        vuelo("Lima", "Puerto Maldonado", 130.0, 110, 2, 13, 0),
        vuelo("Lima", "Juliaca", 105.0, 110, 2, 6, 0),
        vuelo("Lima", "Trujillo", 65.0, 75, 3, 7, 20),
        vuelo("Cusco", "Arequipa", 70.0, 60, 3, 10, 30)
    )
}
