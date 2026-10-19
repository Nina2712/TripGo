package com.example.proyectofinal.ui.model

/** Tipos de servicio de viaje que ofrece TripGo. */
enum class TipoServicio(
    val etiqueta: String,
    val plural: String,
    val unidad: String,
    val unidades: String,
    val moneda: String,
    val emoji: String
) {
    VUELO("Vuelo", "vuelos", "pasajero", "pasajeros", "$", "✈️"),
    HOTEL("Hotel", "hoteles", "noche", "noches", "S/", "🏨"),
    AUTO("Auto", "autos", "día", "días", "S/", "🚗"),
    TOUR("Tour", "tours", "persona", "personas", "S/", "🗺️")
}

/** Elemento reservable: vuelo, hotel, auto o tour. */
data class Servicio(
    val id: Int,
    val tipo: TipoServicio,
    val nombre: String,
    val ciudad: String,
    val detalle: String,
    val precio: Double
)

/** Reserva guardada en SQLite. */
data class Reserva(
    val idReserva: Int,
    val tipo: String,
    val servicio: String,
    val ciudad: String,
    val precioUnitario: Double,
    val cantidad: Int,
    val clienteNombre: String,
    val clienteDni: String,
    val precioTotal: Double,
    val fechaInicio: String = "",
    val fechaFin: String = ""
)
