package com.example.proyectofinal.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.proyectofinal.data.DatabaseHelper
import com.example.proyectofinal.ui.model.Servicio
import com.example.proyectofinal.ui.model.TipoServicio
import com.example.proyectofinal.ui.screens.CercaDeMiScreen
import com.example.proyectofinal.ui.screens.DestinosScreen
import com.example.proyectofinal.ui.screens.HomeScreen
import com.example.proyectofinal.ui.screens.LoginScreen
import com.example.proyectofinal.ui.screens.MisReservasScreen
import com.example.proyectofinal.ui.screens.ProfileScreen
import com.example.proyectofinal.ui.screens.RegisterScreen
import com.example.proyectofinal.ui.screens.ReservaFormScreen
import com.example.proyectofinal.ui.screens.SensoresScreen
import com.example.proyectofinal.ui.screens.ServiciosScreen
import com.example.proyectofinal.ui.screens.VuelosScreen
import com.example.proyectofinal.viewmodel.AuthViewModel

@Composable
fun AppNavigation(
    authViewModel: AuthViewModel,
    dbHelper: DatabaseHelper,
    onFacebookLogin: () -> Unit
) {
    val navController = rememberNavController()

    val uiState by authViewModel.uiState.collectAsState()

    // Servicio elegido para reservar (vuelo, hotel, auto o tour).
    var servicioSeleccionado by remember { mutableStateOf<Servicio?>(null) }

    // Texto con el que se abre el catálogo de hoteles (desde el buscador o un destino del inicio).
    var busquedaHoteles by remember { mutableStateOf("") }

    // Navegación de la barra inferior: Inicio, Explorar, Reservas, Perfil.
    fun irAInicio() {
        navController.popBackStack(Routes.Home.route, false)
    }

    fun irATab(ruta: String) {
        navController.navigate(ruta) {
            popUpTo(Routes.Home.route) { inclusive = false }
            launchSingleTop = true
        }
    }

    fun cerrarSesion() {
        authViewModel.logout()
        navController.navigate(Routes.Login.route) {
            popUpTo(Routes.Home.route) { inclusive = true }
            launchSingleTop = true
        }
    }

    // La navegación depende del estado de autenticación.
    LaunchedEffect(uiState.user) {
        if (uiState.user != null) {
            navController.navigate(Routes.Home.route) {
                popUpTo(Routes.Login.route) {
                    inclusive = true
                }
                launchSingleTop = true
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = Routes.Login.route
    ) {
        composable(Routes.Login.route) {
            LoginScreen(
                uiState = uiState,
                onLogin = { email, password ->
                    authViewModel.login(email, password) {
                        // LaunchedEffect se encargará de navegar.
                    }
                },
                onFacebookLogin = onFacebookLogin,
                onGoRegister = {
                    navController.navigate(Routes.Register.route)
                }
            )
        }

        composable(Routes.Register.route) {
            RegisterScreen(
                uiState = uiState,
                onRegister = { email, password ->
                    authViewModel.register(email, password) {
                        // LaunchedEffect se encargará de navegar.
                    }
                },
                onGoLogin = {
                    navController.navigate(Routes.Login.route) {
                        popUpTo(Routes.Register.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable(Routes.Home.route) {
            HomeScreen(
                uiState = uiState,
                dbHelper = dbHelper,
                onVuelos = { navController.navigate(Routes.Vuelos.route) },
                onHoteles = {
                    busquedaHoteles = ""
                    navController.navigate(Routes.Hoteles.route)
                },
                onAutos = { navController.navigate(Routes.Autos.route) },
                onTours = { navController.navigate(Routes.Tours.route) },
                onDestinos = { navController.navigate(Routes.Destinos.route) },
                onReservas = { irATab(Routes.Reservas.route) },
                onPerfil = { irATab(Routes.Profile.route) },
                onBuscarHoteles = { texto ->
                    busquedaHoteles = texto
                    navController.navigate(Routes.Hoteles.route)
                }
            )
        }

        composable(Routes.Profile.route) {
            ProfileScreen(
                uiState = uiState,
                dbHelper = dbHelper,
                onInicio = { irAInicio() },
                onExplorar = { navController.navigate(Routes.Destinos.route) },
                onReservas = { irATab(Routes.Reservas.route) },
                onLogout = { cerrarSesion() }
            )
        }

        composable(Routes.Vuelos.route) {
            VuelosScreen(
                alVolver = { navController.popBackStack() },
                onReservar = { vuelo ->
                    val horas = vuelo.duracionMinutos / 60
                    val minutos = vuelo.duracionMinutos % 60
                    servicioSeleccionado = Servicio(
                        id = 0,
                        tipo = TipoServicio.VUELO,
                        nombre = vuelo.origen + " → " + vuelo.destino,
                        ciudad = vuelo.destino,
                        detalle = "Duración: " + horas + " h " + minutos + " min",
                        precio = vuelo.precio
                    )
                    navController.navigate(Routes.Reserva.route)
                }
            )
        }

        composable(Routes.Hoteles.route) {
            ServiciosScreen(
                tipo = TipoServicio.HOTEL,
                titulo = "Hoteles",
                subtitulo = "Encuentra tu alojamiento en el Perú.",
                dbHelper = dbHelper,
                busquedaInicial = busquedaHoteles,
                alVolver = { navController.popBackStack() },
                onReservar = { servicio ->
                    servicioSeleccionado = servicio
                    navController.navigate(Routes.Reserva.route)
                }
            )
        }

        composable(Routes.Autos.route) {
            ServiciosScreen(
                tipo = TipoServicio.AUTO,
                titulo = "Alquiler de autos",
                subtitulo = "Explora vehículos disponibles para tu viaje.",
                dbHelper = dbHelper,
                alVolver = { navController.popBackStack() },
                onReservar = { servicio ->
                    servicioSeleccionado = servicio
                    navController.navigate(Routes.Reserva.route)
                }
            )
        }

        composable(Routes.Tours.route) {
            ServiciosScreen(
                tipo = TipoServicio.TOUR,
                titulo = "Tours",
                subtitulo = "Descubre actividades y experiencias.",
                dbHelper = dbHelper,
                alVolver = { navController.popBackStack() },
                onReservar = { servicio ->
                    servicioSeleccionado = servicio
                    navController.navigate(Routes.Reserva.route)
                }
            )
        }

        composable(Routes.Reserva.route) {
            val servicio = servicioSeleccionado
            if (servicio != null) {
                ReservaFormScreen(
                    servicio = servicio,
                    nombreInicial = uiState.user?.displayName ?: "",
                    dbHelper = dbHelper,
                    onReservaExitosa = {
                        navController.navigate(Routes.Reservas.route) {
                            popUpTo(Routes.Home.route)
                        }
                    },
                    onVolver = { navController.popBackStack() }
                )
            }
        }

        composable(Routes.Destinos.route) {
            DestinosScreen(
                alVolver = { navController.popBackStack() },
                onCercaDeMi = { navController.navigate(Routes.CercaDeMi.route) },
                onSensores = { navController.navigate(Routes.Sensores.route) }
            )
        }

        composable(Routes.CercaDeMi.route) {
            CercaDeMiScreen(alVolver = { navController.popBackStack() })
        }

        composable(Routes.Sensores.route) {
            SensoresScreen(alVolver = { navController.popBackStack() })
        }

        composable(Routes.Reservas.route) {
            MisReservasScreen(
                dbHelper = dbHelper,
                onInicio = { irAInicio() },
                onExplorar = { navController.navigate(Routes.Destinos.route) },
                onPerfil = { irATab(Routes.Profile.route) }
            )
        }
    }
}
