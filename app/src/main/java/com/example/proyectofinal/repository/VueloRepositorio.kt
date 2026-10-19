package com.example.proyectofinal.repository

import com.example.proyectofinal.ui.model.Vuelo
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.Calendar

/**
 * Vuelos: se leen de Firestore (colección «vuelos») y se completan con un catálogo
 * de demostración con fechas próximas, así la lista siempre tiene varias opciones
 * y la app sigue funcionando aunque no haya internet.
 */
class VueloRepositorio {

    private val baseDatos = FirebaseFirestore.getInstance()

    suspend fun obtenerVuelos(): List<Vuelo> {
        val remotos = try {
            baseDatos
                .collection("vuelos")
                .whereEqualTo("activo", true)
                .get()
                .await()
                .documents
                .mapNotNull { it.toObject(Vuelo::class.java) }
        } catch (e: Exception) {
            emptyList()
        }

        // Evita repetir rutas que ya vienen del servidor.
        val claves = remotos.map { it.origen + it.destino }.toSet()
        val demo = catalogoDemostracion().filter { (it.origen + it.destino) !in claves }

        return (remotos + demo).sortedBy { it.fechaHoraSalida?.seconds ?: Long.MAX_VALUE }
    }

    private data class Ruta(
        val origen: String,
        val destino: String,
        val precio: Double,
        val minutos: Long,
        val diasDesdeHoy: Int,
        val hora: Int,
        val minuto: Int
    )

    private fun catalogoDemostracion(): List<Vuelo> {
        val rutas = listOf(
            Ruta("Lima", "Cusco", 89.0, 90, 3, 6, 30),
            Ruta("Lima", "Arequipa", 79.0, 100, 4, 8, 15),
            Ruta("Lima", "Iquitos", 119.0, 120, 5, 7, 0),
            Ruta("Lima", "Piura", 95.0, 110, 6, 10, 45),
            Ruta("Lima", "Trujillo", 69.0, 70, 7, 9, 20),
            Ruta("Lima", "Juliaca", 109.0, 105, 8, 5, 50),
            Ruta("Lima", "Tarapoto", 99.0, 95, 9, 13, 10),
            Ruta("Lima", "Pucallpa", 105.0, 85, 10, 11, 30),
            Ruta("Cusco", "Lima", 92.0, 90, 11, 15, 40),
            Ruta("Arequipa", "Lima", 82.0, 100, 12, 17, 5),
            Ruta("Cusco", "Arequipa", 74.0, 60, 13, 12, 25),
            Ruta("Iquitos", "Lima", 125.0, 120, 14, 14, 0)
        )

        return rutas.map { r ->
            val salida = Calendar.getInstance().apply {
                add(Calendar.DAY_OF_YEAR, r.diasDesdeHoy)
                set(Calendar.HOUR_OF_DAY, r.hora)
                set(Calendar.MINUTE, r.minuto)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            val llegada = (salida.clone() as Calendar).apply {
                add(Calendar.MINUTE, r.minutos.toInt())
            }
            Vuelo(
                origen = r.origen,
                destino = r.destino,
                precio = r.precio,
                fechaHoraSalida = Timestamp(salida.time),
                fechaHoraLlegada = Timestamp(llegada.time),
                duracionMinutos = r.minutos,
                activo = true
            )
        }
    }
}
