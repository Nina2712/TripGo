package com.example.proyectofinal.network

import com.google.gson.annotations.SerializedName
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

/**
 * Configuración del servicio REST propio de TripGo (PHP + MySQL, carpeta backend_php).
 * Cambia estos dos valores cuando publiques el backend en tu hosting.
 *
 * - URL_BASE: carpeta api/ de tu hosting, terminada en «/». Con XAMPP en la PC y el
 *   emulador de Android Studio sería "http://10.0.2.2/tripgo/api/".
 * - CLAVE_API: debe ser igual a API_KEY de backend_php/config.php.
 */
object ConfigServidor {
    const val URL_BASE = "https://TU-DOMINIO.example/tripgo/api/"
    const val CLAVE_API = "CAMBIA_ESTA_CLAVE"

    /** Mientras no cambies la URL, la app trabaja solo con SQLite y no llama al servidor. */
    val configurado: Boolean
        get() = !URL_BASE.contains("TU-DOMINIO")
}

// ---------- Modelos JSON ----------

data class ServicioRemoto(
    val id: Int,
    val tipo: String,
    val nombre: String,
    val ciudad: String,
    val detalle: String,
    val precio: Double
)

data class ReservaRemota(
    val accion: String,
    val dispositivo: String,
    @SerializedName("id_local") val idLocal: Int,
    val tipo: String = "",
    val servicio: String = "",
    val ciudad: String = "",
    @SerializedName("precio_unitario") val precioUnitario: Double = 0.0,
    val cantidad: Int = 0,
    @SerializedName("cliente_nombre") val clienteNombre: String = "",
    @SerializedName("cliente_dni") val clienteDni: String = "",
    @SerializedName("fecha_inicio") val fechaInicio: String = "",
    @SerializedName("fecha_fin") val fechaFin: String = ""
)

data class RespuestaServidor(
    val ok: Boolean = false,
    val error: String? = null
)

// ---------- Servicio REST propio (Retrofit) ----------

interface TripGoApi {
    @GET("servicios.php")
    suspend fun catalogo(
        @Header("X-Api-Key") clave: String = ConfigServidor.CLAVE_API
    ): List<ServicioRemoto>

    @POST("reservas.php")
    suspend fun enviarReserva(
        @Body cuerpo: ReservaRemota,
        @Header("X-Api-Key") clave: String = ConfigServidor.CLAVE_API
    ): RespuestaServidor
}

object TripGoRed {
    val api: TripGoApi by lazy {
        Retrofit.Builder()
            .baseUrl(ConfigServidor.URL_BASE)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TripGoApi::class.java)
    }
}
