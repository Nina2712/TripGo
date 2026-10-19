package com.example.proyectofinal.util

import android.content.Context

/** Guarda los servicios favoritos del usuario en el celular (SharedPreferences). */
object Favoritos {
    private const val ARCHIVO = "tripgo_favoritos"
    private const val CLAVE = "lista"

    fun clave(tipo: String, nombre: String) = "$tipo|$nombre"

    fun todos(contexto: Context): Set<String> =
        contexto.getSharedPreferences(ARCHIVO, Context.MODE_PRIVATE)
            .getStringSet(CLAVE, emptySet()) ?: emptySet()

    fun guardar(contexto: Context, claves: Set<String>) {
        contexto.getSharedPreferences(ARCHIVO, Context.MODE_PRIVATE)
            .edit()
            .putStringSet(CLAVE, HashSet(claves))
            .apply()
    }
}
