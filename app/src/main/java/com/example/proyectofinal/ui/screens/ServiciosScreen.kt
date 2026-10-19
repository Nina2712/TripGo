package com.example.proyectofinal.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyectofinal.data.DatabaseHelper
import com.example.proyectofinal.ui.model.Servicio
import com.example.proyectofinal.ui.model.TipoServicio
import com.example.proyectofinal.util.Favoritos

private const val TODOS = "Todos"
private const val SOLO_FAVORITOS = "❤ Favoritos"
private const val ORDEN_RELEVANCIA = "Relevancia"
private const val ORDEN_MENOR = "Menor precio"
private const val ORDEN_MAYOR = "Mayor precio"

/**
 * Pantalla reutilizable para los catálogos de hoteles, autos y tours.
 * Buscador, filtros por ciudad, orden por precio y favoritos.
 */
@Composable
fun ServiciosScreen(
    tipo: TipoServicio,
    titulo: String,
    subtitulo: String,
    dbHelper: DatabaseHelper,
    alVolver: () -> Unit,
    onReservar: (Servicio) -> Unit,
    busquedaInicial: String = ""
) {
    val contexto = LocalContext.current

    var busqueda by remember(busquedaInicial) { mutableStateOf(busquedaInicial) }
    var ciudadFiltro by remember { mutableStateOf(TODOS) }
    var orden by remember { mutableStateOf(ORDEN_RELEVANCIA) }
    val favoritos = remember { Favoritos.todos(contexto).toMutableStateList() }

    val lista = remember(tipo) { dbHelper.obtenerServicios(tipo) }
    val ciudades = remember(lista) { lista.map { it.ciudad }.distinct().sorted() }

    val filtrada = lista
        .filter {
            busqueda.isBlank() ||
                    it.nombre.contains(busqueda, ignoreCase = true) ||
                    it.ciudad.contains(busqueda, ignoreCase = true) ||
                    it.detalle.contains(busqueda, ignoreCase = true)
        }
        .filter {
            when (ciudadFiltro) {
                TODOS -> true
                SOLO_FAVORITOS -> Favoritos.clave(tipo.name, it.nombre) in favoritos
                else -> it.ciudad == ciudadFiltro
            }
        }
        .let {
            when (orden) {
                ORDEN_MENOR -> it.sortedBy { s -> s.precio }
                ORDEN_MAYOR -> it.sortedByDescending { s -> s.precio }
                else -> it
            }
        }

    Scaffold(
        containerColor = FondoTG,
        topBar = { BarraTG(titulo = titulo, alVolver = alVolver) }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Column {
                    Text(
                        text = subtitulo,
                        style = MaterialTheme.typography.bodyLarge,
                        color = GrisTG
                    )
                    Spacer(Modifier.height(12.dp))
                    CampoBusquedaTG(
                        valor = busqueda,
                        onCambio = { busqueda = it },
                        placeholder = "Buscar por nombre o ciudad"
                    )
                    Spacer(Modifier.height(10.dp))
                    FilaChipsTG(
                        opciones = listOf(TODOS, SOLO_FAVORITOS) + ciudades,
                        seleccion = ciudadFiltro,
                        onSeleccion = { ciudadFiltro = it }
                    )
                    Spacer(Modifier.height(4.dp))
                    FilaChipsTG(
                        opciones = listOf(ORDEN_RELEVANCIA, ORDEN_MENOR, ORDEN_MAYOR),
                        seleccion = orden,
                        onSeleccion = { orden = it }
                    )
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = "${filtrada.size} ${tipo.plural} disponibles",
                        style = MaterialTheme.typography.bodyMedium,
                        color = GrisTG
                    )
                }
            }

            items(items = filtrada, key = { it.id }) { servicio ->
                val clave = Favoritos.clave(tipo.name, servicio.nombre)
                TarjetaServicio(
                    servicio = servicio,
                    esFavorito = clave in favoritos,
                    onFavorito = {
                        if (clave in favoritos) favoritos.remove(clave) else favoritos.add(clave)
                        Favoritos.guardar(contexto, favoritos.toSet())
                    },
                    onReservar = onReservar,
                    modifier = Modifier.animateItem()
                )
            }

            if (filtrada.isEmpty()) {
                item {
                    EstadoVacioTG(
                        emoji = if (ciudadFiltro == SOLO_FAVORITOS) "🤍" else "🔎",
                        titulo = if (ciudadFiltro == SOLO_FAVORITOS) {
                            "Aún no tienes favoritos"
                        } else {
                            "No encontramos resultados"
                        },
                        mensaje = if (ciudadFiltro == SOLO_FAVORITOS) {
                            "Toca el corazón de un ${tipo.etiqueta.lowercase()} para guardarlo aquí."
                        } else {
                            "Prueba con otra palabra o cambia los filtros."
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun TarjetaServicio(
    servicio: Servicio,
    esFavorito: Boolean,
    onFavorito: () -> Unit,
    onReservar: (Servicio) -> Unit,
    modifier: Modifier = Modifier
) {
    val tipo = servicio.tipo
    val estrellas = estrellasDe(servicio.detalle)

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            ImagenServicio(servicio = servicio, alto = 190.dp) {
                // Etiqueta con la ciudad
                Row(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(12.dp)
                        .background(Color.White.copy(alpha = 0.92f), RoundedCornerShape(50))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = emojiCiudad(servicio.ciudad), fontSize = 14.sp)
                    Spacer(Modifier.size(6.dp))
                    Text(
                        text = servicio.ciudad,
                        style = MaterialTheme.typography.labelLarge,
                        color = AzulTG
                    )
                }

                // Botón de favorito
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                        .shadow(2.dp, CircleShape)
                        .size(38.dp)
                        .background(Color.White, CircleShape)
                        .clickable { onFavorito() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (esFavorito) Icons.Default.Favorite
                        else Icons.Default.FavoriteBorder,
                        contentDescription = if (esFavorito) "Quitar de favoritos"
                        else "Guardar en favoritos",
                        tint = if (esFavorito) RojoTG else GrisTG,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = servicio.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextoTG
                )

                if (estrellas > 0) {
                    Spacer(Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        repeat(estrellas) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = Color(0xFFF59E0B),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(Modifier.size(6.dp))
                        Text(
                            text = "$estrellas estrellas",
                            style = MaterialTheme.typography.bodySmall,
                            color = GrisTG
                        )
                    }
                } else if (servicio.detalle.isNotBlank()) {
                    Text(
                        text = servicio.detalle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = GrisTG
                    )
                }

                Spacer(Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Precio por ${tipo.unidad}",
                            style = MaterialTheme.typography.bodySmall,
                            color = GrisTG
                        )
                        Text(
                            text = formatoPrecio(tipo, servicio.precio),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = AzulTG
                        )
                    }

                    Button(
                        onClick = { onReservar(servicio) },
                        shape = RoundedCornerShape(14.dp),
                        contentPadding = PaddingValues(horizontal = 22.dp, vertical = 12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AzulTG)
                    ) {
                        Text("Reservar", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
