package com.example.proyectofinal.ui.navigation

sealed class Routes(val route: String) {
    data object Login : Routes("login")
    data object Register : Routes("register")
    data object Profile : Routes("profile")
    data object Home : Routes("home")
    data object Hoteles : Routes("hoteles")
    data object Autos : Routes("autos")
    data object Tours : Routes("tours")
    data object Reserva : Routes("reserva")
    data object Reservas : Routes("reservas")
    data object Destinos : Routes("destinos")
    data object CercaDeMi : Routes("cerca_de_mi")
    data object Sensores : Routes("sensores")

    object Vuelos {
        const val route = "vuelos"
    }
}
