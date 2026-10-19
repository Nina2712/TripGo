package com.example.proyectofinal.ui.screens

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyectofinal.R
import com.example.proyectofinal.ui.model.Servicio
import com.example.proyectofinal.ui.model.TipoServicio
import com.example.proyectofinal.ui.theme.BordeSuave
import java.text.Normalizer

// ---------------------------------------------------------------------------
// Barras
// ---------------------------------------------------------------------------

/** Barra superior estándar: título en negrita y flecha de regreso opcional. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun BarraTG(titulo: String, alVolver: (() -> Unit)? = null) {
    TopAppBar(
        title = {
            Text(text = titulo, fontWeight = FontWeight.Bold, color = TextoTG)
        },
        navigationIcon = {
            if (alVolver != null) {
                IconButton(onClick = alVolver) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver",
                        tint = TextoTG
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
    )
}

/** Barra de navegación inferior compartida por Inicio, Explorar, Reservas y Perfil. */
@Composable
internal fun BarraInferiorTG(
    actual: String,
    onInicio: () -> Unit,
    onExplorar: () -> Unit,
    onReservas: () -> Unit,
    onPerfil: () -> Unit
) {
    val colores = NavigationBarItemDefaults.colors(
        selectedIconColor = AzulTG,
        selectedTextColor = AzulTG,
        indicatorColor = AzulClaroTG,
        unselectedIconColor = GrisTG,
        unselectedTextColor = GrisTG
    )

    NavigationBar(containerColor = Color.White) {
        NavigationBarItem(
            selected = actual == "inicio",
            onClick = onInicio,
            icon = { Icon(Icons.Default.Home, contentDescription = null) },
            label = { Text("Inicio") },
            colors = colores
        )
        NavigationBarItem(
            selected = actual == "explorar",
            onClick = onExplorar,
            icon = { Icon(Icons.Default.Place, contentDescription = null) },
            label = { Text("Explorar") },
            colors = colores
        )
        NavigationBarItem(
            selected = actual == "reservas",
            onClick = onReservas,
            icon = { Icon(Icons.Default.DateRange, contentDescription = null) },
            label = { Text("Reservas") },
            colors = colores
        )
        NavigationBarItem(
            selected = actual == "perfil",
            onClick = onPerfil,
            icon = { Icon(Icons.Default.Person, contentDescription = null) },
            label = { Text("Perfil") },
            colors = colores
        )
    }
}

// ---------------------------------------------------------------------------
// Búsqueda y filtros
// ---------------------------------------------------------------------------

@Composable
internal fun CampoBusquedaTG(
    valor: String,
    onCambio: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    onBuscar: (() -> Unit)? = null
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onCambio,
        placeholder = { Text(placeholder, color = GrisTG) },
        leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = null, tint = AzulTG)
        },
        trailingIcon = {
            if (valor.isNotEmpty()) {
                IconButton(onClick = { onCambio("") }) {
                    Icon(
                        Icons.Default.Clear,
                        contentDescription = "Borrar búsqueda",
                        tint = GrisTG
                    )
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { onBuscar?.invoke() }),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedBorderColor = AzulTG,
            unfocusedBorderColor = BordeSuave
        ),
        modifier = modifier.fillMaxWidth()
    )
}

/** Fila horizontal de chips para filtrar u ordenar. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun FilaChipsTG(
    opciones: List<String>,
    seleccion: String,
    onSeleccion: (String) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 2.dp)
    ) {
        items(items = opciones, key = { it }) { opcion ->
            FilterChip(
                selected = opcion == seleccion,
                onClick = { onSeleccion(opcion) },
                label = { Text(opcion) },
                shape = RoundedCornerShape(50),
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = Color.White,
                    labelColor = TextoTG,
                    selectedContainerColor = AzulTG,
                    selectedLabelColor = Color.White
                )
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Estados
// ---------------------------------------------------------------------------

@Composable
internal fun EstadoVacioTG(emoji: String, titulo: String, mensaje: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 40.dp, horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .background(AzulClaroTG, RoundedCornerShape(50)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = emoji, fontSize = 34.sp)
        }
        Spacer(Modifier.height(14.dp))
        Text(
            text = titulo,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = TextoTG,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = mensaje,
            style = MaterialTheme.typography.bodyMedium,
            color = GrisTG,
            textAlign = TextAlign.Center
        )
    }
}

// ---------------------------------------------------------------------------
// Imágenes de servicios
// ---------------------------------------------------------------------------

internal fun normalizar(texto: String): String {
    val sinTildes = Normalizer.normalize(texto.lowercase(), Normalizer.Form.NFD)
        .replace(Regex("\\p{InCombiningDiacriticalMarks}+"), "")
    return sinTildes.replace(Regex("[^a-z0-9]+"), "_").trim('_')
}

/**
 * Busca la foto de un servicio. Devuelve 0 si no hay foto.
 *
 * Para añadir una foto SIN tocar el código, guarda el archivo en res/drawable con el nombre:
 *   hoteles → hotel_<nombre>      ej. hotel_palacio_del_inka.jpg
 *   autos   → auto_<nombre>       ej. auto_toyota_corolla.jpg
 *   tours   → tour_<nombre>       ej. tour_machu_picchu_clasico.jpg
 * (minúsculas, sin tildes y con guion bajo en vez de espacios)
 */
internal fun recursoImagen(contexto: Context, servicio: Servicio): Int {
    val slug = servicio.tipo.name.lowercase() + "_" + normalizar(servicio.nombre)
    val id = contexto.resources.getIdentifier(slug, "drawable", contexto.packageName)
    if (id != 0) return id

    if (servicio.tipo == TipoServicio.HOTEL) {
        return when (servicio.nombre.lowercase()) {
            "palacio del inka" -> R.drawable.palacio_del_inka_cusco
            "casa andina premium" -> R.drawable.casa_andina_arequipa
            "aranwa paracas resort" -> R.drawable.arawa_paracas_ica
            "hotel dm nazca" -> R.drawable.hotel_dm_nazca
            "hotel hilton iquitos" -> R.drawable.hilton_iquitos
            "hotel costa del sol" -> R.drawable.costa_del_sol_piura
            else -> 0
        }
    }
    return 0
}

internal fun degradadoDe(tipo: TipoServicio): Brush {
    val colores = when (tipo) {
        TipoServicio.VUELO -> listOf(Color(0xFF1769FF), Color(0xFF5B9BFF))
        TipoServicio.HOTEL -> listOf(Color(0xFF0B3FA8), Color(0xFF1769FF))
        TipoServicio.AUTO -> listOf(Color(0xFFEA580C), Color(0xFFFB923C))
        TipoServicio.TOUR -> listOf(Color(0xFF047857), Color(0xFF34D399))
    }
    return Brush.linearGradient(colores)
}

/** Foto del servicio; si no existe muestra un degradado de color con el emoji del tipo. */
@Composable
internal fun ImagenServicio(
    servicio: Servicio,
    alto: Dp,
    modifier: Modifier = Modifier,
    contenido: @Composable androidx.compose.foundation.layout.BoxScope.() -> Unit = {}
) {
    val contexto = androidx.compose.ui.platform.LocalContext.current
    val recurso = androidx.compose.runtime.remember(servicio.tipo, servicio.nombre) {
        recursoImagen(contexto, servicio)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(alto)
            .background(degradadoDe(servicio.tipo))
    ) {
        if (recurso != 0) {
            Image(
                painter = painterResource(id = recurso),
                contentDescription = servicio.nombre,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Text(
                text = servicio.tipo.emoji,
                fontSize = 64.sp,
                modifier = Modifier.align(Alignment.Center)
            )
        }
        contenido()
    }
}

internal fun emojiCiudad(ciudad: String): String {
    return when (ciudad.lowercase()) {
        "cusco" -> "🏔️"
        "arequipa" -> "🌋"
        "ica" -> "🏖️"
        "nazca" -> "🛩️"
        "piura" -> "🌴"
        "iquitos" -> "🌿"
        "lima" -> "🏙️"
        "puno" -> "🛶"
        "trujillo" -> "🏛️"
        else -> "📍"
    }
}

/** Número de estrellas leído del detalle («Hotel 5 estrellas» → 5). 0 si no aplica. */
internal fun estrellasDe(detalle: String): Int {
    val m = Regex("(\\d)\\s*estrella", RegexOption.IGNORE_CASE).find(detalle)
    return m?.groupValues?.get(1)?.toIntOrNull()?.coerceIn(0, 5) ?: 0
}
