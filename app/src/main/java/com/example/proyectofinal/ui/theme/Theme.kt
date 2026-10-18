package com.example.proyectofinal.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val EsquemaTripGo = lightColorScheme(
    primary = AzulMarca,
    onPrimary = Color.White,
    primaryContainer = AzulClaro,
    onPrimaryContainer = AzulOscuro,
    secondary = AzulOscuro,
    tertiary = Verde,
    background = FondoApp,
    onBackground = TextoPrincipal,
    surface = Color.White,
    onSurface = TextoPrincipal,
    surfaceVariant = AzulClaro,
    onSurfaceVariant = TextoSecundario,
    outline = BordeSuave,
    error = Rojo
)

/**
 * Tema de TripGo. Se usa siempre la paleta de marca (sin colores dinámicos
 * del sistema) para que la app se vea igual en todos los celulares.
 */
@Composable
fun ProyectoFinalTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = EsquemaTripGo,
        typography = Typography,
        content = content
    )
}
