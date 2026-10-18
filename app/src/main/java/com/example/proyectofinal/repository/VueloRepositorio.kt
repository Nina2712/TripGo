package com.example.proyectofinal.repository


import com.example.proyectofinal.ui.model.Vuelo
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class VueloRepositorio {

    private val baseDatos = FirebaseFirestore.getInstance()

    suspend fun obtenerVuelos(): List<Vuelo> {
        val resultado = baseDatos
            .collection("vuelos")
            .whereEqualTo("activo", true)
            .get()
            .await()

        return resultado.documents.mapNotNull { documento ->
            documento.toObject(Vuelo::class.java)
        }
    }
}
