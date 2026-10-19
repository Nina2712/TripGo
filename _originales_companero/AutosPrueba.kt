package com.example.proyectofinal.ui.model

// Lista temporal de vehículos para demostración.
// Estos datos no se guardan en Firebase.

object AutosPrueba {

    val lista = listOf(
        Auto(
            id = "auto1",
            nombre = "Toyota Corolla",
            modelo = "2025",
            precioPorDia = 150.0,
            activo = true
        ),
        Auto(
            id = "auto2",
            nombre = "Hyundai Tucson",
            modelo = "2025",
            precioPorDia = 220.0,
            activo = true
        ),
        Auto(
            id = "auto3",
            nombre = "Kia Rio",
            modelo = "2024",
            precioPorDia = 120.0,
            activo = true
        ),
        Auto(
            id = "auto4",
            nombre = "Toyota RAV4",
            modelo = "2025",
            precioPorDia = 250.0,
            activo = true
        )
    )
}