package com.example.proyectofinal.ui.model


data class ReservaAuto(
    val autoId: String = "",
    val usuarioId: String = "",
    val fechaInicio: String = "",
    val fechaFin: String = "",
    val precioTotal: Double = 0.0,
    val estado: String = "CONFIRMADA"
)