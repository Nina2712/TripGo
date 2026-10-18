package com.example.proyectofinal.repository


import com.example.proyectofinal.ui.model.Auto
import com.example.proyectofinal.ui.model.ReservaAuto
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Calendar

class AutosRepositorio {

    private val baseDatos = FirebaseFirestore.getInstance()
    private val autenticacion = FirebaseAuth.getInstance()

    // Obtener los autos activos de Firestore.
    suspend fun obtenerAutos(): List<Auto> {

        val resultado = baseDatos
            .collection("autos")
            .whereEqualTo("activo", true)
            .get()
            .await()

        return resultado.documents.mapNotNull { documento ->
            documento.toObject(Auto::class.java)?.copy(
                id = documento.id
            )
        }
    }

    // Convertir y validar las fechas con formato yyyy-MM-dd.
    private fun convertirFecha(fecha: String): java.util.Date {
        val formato = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        formato.isLenient = false

        val resultado = formato.parse(fecha)
            ?: throw IllegalArgumentException("La fecha no es válida.")

        // Comprobar que el texto corresponda exactamente a la fecha.
        require(formato.format(resultado) == fecha) {
            "Usa el formato de fecha yyyy-MM-dd."
        }

        return resultado
    }

    // Reservar un vehículo.
    suspend fun reservarAuto(
        autoId: String,
        fechaInicio: String,
        fechaFin: String
    ) {

        val usuario = autenticacion.currentUser
            ?: throw IllegalStateException(
                "Debes iniciar sesión para reservar."
            )

        val inicio = convertirFecha(fechaInicio)
        val fin = convertirFecha(fechaFin)

        require(fin.after(inicio)) {
            "La fecha de fin debe ser posterior a la fecha de inicio."
        }

        val autoRef = baseDatos
            .collection("autos")
            .document(autoId)

        val reservasRef = baseDatos
            .collection("reservas_autos")

        val reservaRef = reservasRef.document()

        // Consultar las reservas existentes ANTES de la transacción.
        // Esto permite compilar, pero por sí solo no evita dos reservas
        // simultáneas. La comprobación definitiva requiere protección
        // en el servidor o un diseño de bloqueo validado.
        val reservasExistentes = reservasRef
            .whereEqualTo("autoId", autoId)
            .get()
            .await()

        val existeCruce = reservasExistentes.documents.any { documento ->

            val inicioExistente =
                documento.getString("fechaInicio") ?: ""

            val finExistente =
                documento.getString("fechaFin") ?: ""

            val estado =
                documento.getString("estado") ?: ""

            estado == "CONFIRMADA" &&
                    inicioExistente < fechaFin &&
                    finExistente > fechaInicio
        }

        require(!existeCruce) {
            "El auto ya está reservado para esas fechas."
        }

        // Calcular los días de alquiler.
        val calendarioInicio = Calendar.getInstance().apply {
            time = inicio
        }

        val calendarioFin = Calendar.getInstance().apply {
            time = fin
        }

        val inicioDia = calendarioInicio.get(Calendar.DAY_OF_YEAR)
        val finDia = calendarioFin.get(Calendar.DAY_OF_YEAR)

        val dias = (
                (calendarioFin.timeInMillis - calendarioInicio.timeInMillis) /
                        (24L * 60 * 60 * 1000)
                ).toInt()

        require(dias > 0) {
            "La duración debe ser de al menos un día."
        }

        // Leer y guardar la reserva mediante una transacción.
        baseDatos.runTransaction { transaccion ->

            val autoSnapshot = transaccion.get(autoRef)

            require(autoSnapshot.exists()) {
                "El vehículo seleccionado no existe."
            }

            require(autoSnapshot.getBoolean("activo") == true) {
                "Este vehículo no está disponible."
            }

            val precioPorDia =
                autoSnapshot.getDouble("precioPorDia") ?: 0.0

            require(precioPorDia > 0) {
                "El precio diario del vehículo no es válido."
            }

            val versionActual =
                autoSnapshot.getLong("reservationVersion") ?: 0L

            val reserva = ReservaAuto(
                autoId = autoId,
                usuarioId = usuario.uid,
                fechaInicio = fechaInicio,
                fechaFin = fechaFin,
                precioTotal = precioPorDia * dias,
                estado = "CONFIRMADA"
            )

            // Actualizar el vehículo para provocar conflicto entre
            // transacciones que intenten modificar el mismo documento.
            transaccion.set(
                autoRef,
                mapOf("reservationVersion" to versionActual + 1),
                SetOptions.merge()
            )

            transaccion.set(reservaRef, reserva)

            null
        }.await()
    }
}