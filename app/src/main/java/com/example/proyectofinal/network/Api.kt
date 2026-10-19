package com.example.proyectofinal.network

import com.google.gson.annotations.SerializedName
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

// ---------- Modelos JSON ----------

data class ClimaRespuesta(
    val current: ClimaActual? = null
)

data class ClimaActual(
    @SerializedName("temperature_2m") val temperatura: Double? = null,
    @SerializedName("weather_code") val codigo: Int? = null,
    @SerializedName("wind_speed_10m") val viento: Double? = null
)

data class CambioRespuesta(
    val result: String? = null,
    val rates: Map<String, Double>? = null
)

// ---------- Servicios REST (Retrofit) ----------

/** Servicio REST 1: clima actual de un destino (Open-Meteo, sin clave). */
interface ClimaApi {
    @GET("v1/forecast")
    suspend fun obtenerClima(
        @Query("latitude") latitud: Double,
        @Query("longitude") longitud: Double,
        @Query("current") actual: String = "temperature_2m,weather_code,wind_speed_10m",
        @Query("timezone") zona: String = "auto"
    ): ClimaRespuesta
}

/** Servicio REST 2: tipo de cambio USD → PEN (open.er-api.com, sin clave). */
interface CambioApi {
    @GET("v6/latest/USD")
    suspend fun obtenerTasas(): CambioRespuesta
}

object RedApi {
    val clima: ClimaApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.open-meteo.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ClimaApi::class.java)
    }

    val cambio: CambioApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://open.er-api.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CambioApi::class.java)
    }
}

/** Traduce el código WMO de Open-Meteo a un texto en español. */
fun descripcionClima(codigo: Int?): String {
    return when (codigo) {
        null -> "Sin datos"
        0 -> "Despejado"
        1, 2, 3 -> "Parcialmente nublado"
        45, 48 -> "Niebla"
        in 51..57 -> "Llovizna"
        in 61..67 -> "Lluvia"
        in 71..77 -> "Nieve"
        in 80..82 -> "Chubascos"
        in 95..99 -> "Tormenta"
        else -> "Variable"
    }
}
