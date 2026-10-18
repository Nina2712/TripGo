package com.example.proyectofinal.util

import android.app.job.JobInfo
import android.app.job.JobParameters
import android.app.job.JobScheduler
import android.app.job.JobService
import android.content.ComponentName
import android.content.Context

/**
 * Proceso en segundo plano con JobScheduler (Unidad 3 y 5 del curso).
 * Tras confirmar una reserva se programa un recordatorio que el sistema
 * ejecuta más tarde, aunque la app esté cerrada.
 */
class RecordatorioReservaJob : JobService() {

    override fun onStartJob(params: JobParameters?): Boolean {
        val detalle = params?.extras?.getString(EXTRA_DETALLE) ?: "tu reserva"
        NotificacionHelper.mostrar(
            applicationContext,
            "Recordatorio de TripGo",
            "Revisa los detalles de $detalle en «Mis reservas»."
        )
        // El trabajo terminó; no queda nada ejecutándose en otro hilo.
        return false
    }

    override fun onStopJob(params: JobParameters?): Boolean = false

    companion object {
        private const val ID_TRABAJO = 1001
        private const val EXTRA_DETALLE = "detalle"
        private const val ESPERA_MS = 60_000L

        fun programar(contexto: Context, detalle: String) {
            val planificador =
                contexto.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
            val extras = android.os.PersistableBundle().apply {
                putString(EXTRA_DETALLE, detalle)
            }
            val trabajo = JobInfo.Builder(
                ID_TRABAJO,
                ComponentName(contexto, RecordatorioReservaJob::class.java)
            )
                .setMinimumLatency(ESPERA_MS)
                .setExtras(extras)
                .build()
            planificador.schedule(trabajo)
        }
    }
}
