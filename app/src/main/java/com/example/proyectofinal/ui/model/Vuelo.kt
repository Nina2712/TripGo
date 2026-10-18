package com.example.proyectofinal.ui.model

import com.google.firebase.Timestamp

data class Vuelo(
    val origen: String = "",
    val destino: String = "",
    val precio: Double = 0.0,
    val fechaHoraSalida: Timestamp? = null,
    val fechaHoraLlegada: Timestamp? = null,
    val duracionMinutos: Long = 0,
    val activo: Boolean = true
)