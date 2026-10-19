package com.example.proyectofinal.data

import android.content.Context
import android.util.Log
import com.example.proyectofinal.network.ConfigServidor
import com.example.proyectofinal.network.ReservaRemota
import com.example.proyectofinal.network.TripGoRed
import com.example.proyectofinal.ui.model.Reserva
import com.example.proyectofinal.ui.model.Servicio
import com.example.proyectofinal.ui.model.TipoServicio
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * Sincroniza SQLite con el servicio REST propio (PHP + MySQL).
 * SQLite sigue siendo la fuente local: si no hay internet o el servidor falla,
 * la app funciona igual y simplemente no se envía esa operación.
 */
object Sincronizador {

    private const val TAG = "TripGoSync"
    private val alcance = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private fun dispositivoId(contexto: Context): String {
        val prefs = contexto.applicationContext
            .getSharedPreferences("tripgo_sync", Context.MODE_PRIVATE)
        val existente = prefs.getString("dispositivo", null)
        if (existente != null) return existente
        val nuevo = UUID.randomUUID().toString()
        prefs.edit().putString("dispositivo", nuevo).apply()
        return nuevo
    }

    /** Crea o actualiza una reserva en el servidor. */
    fun enviarReserva(contexto: Context, reserva: Reserva?) {
        if (reserva == null || !ConfigServidor.configurado) return
        val dispositivo = dispositivoId(contexto)
        alcance.launch {
            try {
                TripGoRed.api.enviarReserva(
                    ReservaRemota(
                        accion = "guardar",
                        dispositivo = dispositivo,
                        idLocal = reserva.idReserva,
                        tipo = reserva.tipo,
                        servicio = reserva.servicio,
                        ciudad = reserva.ciudad,
                        precioUnitario = reserva.precioUnitario,
                        cantidad = reserva.cantidad,
                        clienteNombre = reserva.clienteNombre,
                        clienteDni = reserva.clienteDni,
                        fechaInicio = reserva.fechaInicio,
                        fechaFin = reserva.fechaFin
                    )
                )
            } catch (e: Exception) {
                Log.w(TAG, "No se pudo enviar la reserva: ${e.message}")
            }
        }
    }

    /** Elimina una reserva del servidor. */
    fun eliminarReserva(contexto: Context, idLocal: Int) {
        if (!ConfigServidor.configurado) return
        val dispositivo = dispositivoId(contexto)
        alcance.launch {
            try {
                TripGoRed.api.enviarReserva(
                    ReservaRemota(
                        accion = "eliminar",
                        dispositivo = dispositivo,
                        idLocal = idLocal
                    )
                )
            } catch (e: Exception) {
                Log.w(TAG, "No se pudo eliminar la reserva: ${e.message}")
            }
        }
    }

    /** Descarga el catálogo del portal administrativo y actualiza SQLite. */
    fun descargarCatalogo(dbHelper: DatabaseHelper) {
        if (!ConfigServidor.configurado) return
        alcance.launch {
            try {
                val remoto = TripGoRed.api.catalogo()
                for (tipo in listOf(TipoServicio.HOTEL, TipoServicio.AUTO, TipoServicio.TOUR)) {
                    val lista = remoto
                        .filter { it.tipo == tipo.name }
                        .map {
                            Servicio(
                                id = it.id,
                                tipo = tipo,
                                nombre = it.nombre,
                                ciudad = it.ciudad,
                                detalle = it.detalle,
                                precio = it.precio
                            )
                        }
                    dbHelper.reemplazarCatalogo(tipo, lista)
                }
            } catch (e: Exception) {
                Log.w(TAG, "No se pudo descargar el catálogo: ${e.message}")
            }
        }
    }
}
