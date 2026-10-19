package com.example.proyectofinal.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.proyectofinal.ui.model.Reserva
import com.example.proyectofinal.ui.model.Servicio
import com.example.proyectofinal.ui.model.TipoServicio

/**
 * Base de datos local SQLite de TripGo.
 * Tablas: hoteles, autos, tours (catálogos) y reservas.
 */
class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, "Turismo.db", null, 3) {

    override fun onCreate(db: SQLiteDatabase) {
        crearCatalogo(db)

        db.execSQL(
            "CREATE TABLE reservas (" +
                    "idReserva INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "tipo TEXT NOT NULL, " +
                    "servicio TEXT NOT NULL, " +
                    "ciudad TEXT NOT NULL, " +
                    "precioUnitario REAL NOT NULL, " +
                    "cantidad INTEGER NOT NULL, " +
                    "clienteNombre TEXT NOT NULL, " +
                    "clienteDni TEXT NOT NULL, " +
                    "precioTotal REAL NOT NULL, " +
                    "fechaInicio TEXT NOT NULL DEFAULT '', " +
                    "fechaFin TEXT NOT NULL DEFAULT '')"
        )
    }

    /** Crea y llena las tablas de catálogo (hoteles, autos y tours). */
    private fun crearCatalogo(db: SQLiteDatabase) {
        for (tabla in listOf("hoteles", "autos", "tours")) {
            db.execSQL(
                "CREATE TABLE $tabla (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "nombre TEXT NOT NULL, " +
                        "ciudad TEXT NOT NULL, " +
                        "detalle TEXT NOT NULL DEFAULT '', " +
                        "precio REAL NOT NULL)"
            )
        }

        sembrar(
            db, "hoteles", listOf(
                arrayOf<Any>("Palacio del Inka", "Cusco", "Hotel 5 estrellas", 520.0),
                arrayOf<Any>("Casa Andina Premium", "Arequipa", "Hotel 4 estrellas", 310.0),
                arrayOf<Any>("Aranwa Paracas Resort", "Ica", "Resort frente al mar", 480.0),
                arrayOf<Any>("Hotel DM Nazca", "Nazca", "Hotel 3 estrellas", 190.0),
                arrayOf<Any>("Hotel Hilton Iquitos", "Iquitos", "Hotel 4 estrellas", 410.0),
                arrayOf<Any>("Hotel Costa del Sol", "Piura", "Hotel de playa", 280.0),
                arrayOf<Any>("Costa Verde Suites", "Lima", "Hotel 5 estrellas", 460.0),
                arrayOf<Any>("Barranco Boutique Hotel", "Lima", "Hotel 4 estrellas", 340.0),
                arrayOf<Any>("Hostal Inti Wasi", "Cusco", "Hotel 3 estrellas", 160.0),
                arrayOf<Any>("Casa del Valle Sagrado", "Cusco", "Hotel 4 estrellas", 350.0),
                arrayOf<Any>("Posada del Misti", "Arequipa", "Hotel 3 estrellas", 170.0),
                arrayOf<Any>("Lago Titicaca Lodge", "Puno", "Hotel 4 estrellas", 290.0),
                arrayOf<Any>("Bahía Paracas Hostal", "Ica", "Hotel 3 estrellas", 210.0),
                arrayOf<Any>("Amazon River Lodge", "Iquitos", "Lodge en la selva", 380.0),
                arrayOf<Any>("Máncora Surf Hotel", "Piura", "Hotel de playa", 240.0),
                arrayOf<Any>("Chan Chan Inn", "Trujillo", "Hotel 3 estrellas", 150.0)
            )
        )

        sembrar(
            db, "autos", listOf(
                arrayOf<Any>("Toyota Corolla", "Lima", "Modelo 2025", 150.0),
                arrayOf<Any>("Hyundai Tucson", "Cusco", "Modelo 2025 · SUV", 220.0),
                arrayOf<Any>("Kia Rio", "Arequipa", "Modelo 2024", 120.0),
                arrayOf<Any>("Toyota RAV4", "Lima", "Modelo 2025 · SUV", 250.0),
                arrayOf<Any>("Toyota Hilux", "Arequipa", "Camioneta 4x4", 280.0),
                arrayOf<Any>("Nissan Kicks", "Ica", "SUV compacta", 190.0),
                arrayOf<Any>("Suzuki Swift", "Lima", "Modelo 2024 · económico", 110.0),
                arrayOf<Any>("Hyundai Accent", "Lima", "Sedán 2025", 125.0),
                arrayOf<Any>("Mitsubishi L200", "Lima", "Camioneta 4x4", 270.0),
                arrayOf<Any>("Nissan Frontier", "Cusco", "Camioneta 4x4", 300.0),
                arrayOf<Any>("Chevrolet Sail", "Arequipa", "Sedán 2024", 105.0),
                arrayOf<Any>("Kia Sportage", "Piura", "Modelo 2025 · SUV", 210.0),
                arrayOf<Any>("Toyota Yaris", "Trujillo", "Modelo 2024", 115.0),
                arrayOf<Any>("Toyota Fortuner", "Puno", "SUV 7 asientos", 260.0),
                arrayOf<Any>("Hyundai Creta", "Ica", "SUV compacta", 175.0)
            )
        )

        sembrar(
            db, "tours", listOf(
                arrayOf<Any>("Machu Picchu Clásico", "Cusco", "2 días", 480.0),
                arrayOf<Any>("Cañón del Colca", "Arequipa", "2 días", 320.0),
                arrayOf<Any>("Islas Ballestas", "Ica", "4 horas", 90.0),
                arrayOf<Any>("Sobrevuelo Líneas de Nazca", "Nazca", "1 hora", 260.0),
                arrayOf<Any>("Amazonía Profunda", "Iquitos", "3 días", 650.0),
                arrayOf<Any>("Playas de Máncora", "Piura", "Full day", 150.0),
                arrayOf<Any>("Valle Sagrado Full Day", "Cusco", "1 día", 180.0),
                arrayOf<Any>("Montaña de Siete Colores", "Cusco", "Full day", 140.0),
                arrayOf<Any>("Islas Flotantes de los Uros", "Puno", "4 horas", 85.0),
                arrayOf<Any>("City Tour Lima Colonial", "Lima", "3 horas", 70.0),
                arrayOf<Any>("Circuito Mágico del Agua", "Lima", "2 horas", 45.0),
                arrayOf<Any>("Oasis de Huacachina", "Ica", "3 horas", 120.0),
                arrayOf<Any>("Monasterio de Santa Catalina", "Arequipa", "2 horas", 60.0),
                arrayOf<Any>("Reserva Nacional de Paracas", "Ica", "3 horas", 95.0),
                arrayOf<Any>("Chan Chan y Huanchaco", "Trujillo", "Medio día", 110.0),
                arrayOf<Any>("Avistamiento de ballenas", "Piura", "3 horas", 200.0)
            )
        )
    }

    /**
     * Al subir de versión solo se renueva el catálogo (hoteles, autos y tours).
     * Las reservas del usuario NO se borran.
     */
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        for (tabla in listOf("hoteles", "autos", "tours")) {
            db.execSQL("DROP TABLE IF EXISTS $tabla")
        }
        crearCatalogo(db)
    }

    private fun sembrar(db: SQLiteDatabase, tabla: String, datos: List<Array<Any>>) {
        for (fila in datos) {
            val valores = ContentValues().apply {
                put("nombre", fila[0] as String)
                put("ciudad", fila[1] as String)
                put("detalle", fila[2] as String)
                put("precio", fila[3] as Double)
            }
            db.insert(tabla, null, valores)
        }
    }

    // ---------- Catálogos ----------

    fun obtenerServicios(tipo: TipoServicio): List<Servicio> {
        val tabla = when (tipo) {
            TipoServicio.HOTEL -> "hoteles"
            TipoServicio.AUTO -> "autos"
            TipoServicio.TOUR -> "tours"
            TipoServicio.VUELO -> return emptyList()
        }

        val lista = mutableListOf<Servicio>()
        readableDatabase.rawQuery(
            "SELECT id, nombre, ciudad, detalle, precio FROM $tabla ORDER BY id",
            null
        ).use { cursor ->
            while (cursor.moveToNext()) {
                lista.add(
                    Servicio(
                        id = cursor.getInt(0),
                        tipo = tipo,
                        nombre = cursor.getString(1),
                        ciudad = cursor.getString(2),
                        detalle = cursor.getString(3),
                        precio = cursor.getDouble(4)
                    )
                )
            }
        }
        return lista
    }

    // ---------- Reservas (registro, búsqueda, modificación y borrado) ----------

    fun insertarReserva(
        servicio: Servicio,
        clienteNombre: String,
        clienteDni: String,
        cantidad: Int,
        fechaInicio: String = "",
        fechaFin: String = ""
    ): Boolean {
        val valores = ContentValues().apply {
            put("fechaInicio", fechaInicio)
            put("fechaFin", fechaFin)
            put("tipo", servicio.tipo.name)
            put("servicio", servicio.nombre)
            put("ciudad", servicio.ciudad)
            put("precioUnitario", servicio.precio)
            put("cantidad", cantidad)
            put("clienteNombre", clienteNombre)
            put("clienteDni", clienteDni)
            put("precioTotal", servicio.precio * cantidad)
        }
        return writableDatabase.insert("reservas", null, valores) != -1L
    }

    fun obtenerReservas(filtro: String = ""): List<Reserva> {
        val like = "%" + filtro.trim() + "%"
        val lista = mutableListOf<Reserva>()

        readableDatabase.rawQuery(
            "SELECT idReserva, tipo, servicio, ciudad, precioUnitario, cantidad, " +
                    "clienteNombre, clienteDni, precioTotal, fechaInicio, fechaFin FROM reservas " +
                    "WHERE clienteNombre LIKE ? OR clienteDni LIKE ? " +
                    "OR servicio LIKE ? OR ciudad LIKE ? " +
                    "ORDER BY idReserva DESC",
            arrayOf(like, like, like, like)
        ).use { cursor ->
            while (cursor.moveToNext()) {
                lista.add(
                    Reserva(
                        idReserva = cursor.getInt(0),
                        tipo = cursor.getString(1),
                        servicio = cursor.getString(2),
                        ciudad = cursor.getString(3),
                        precioUnitario = cursor.getDouble(4),
                        cantidad = cursor.getInt(5),
                        clienteNombre = cursor.getString(6),
                        clienteDni = cursor.getString(7),
                        precioTotal = cursor.getDouble(8),
                        fechaInicio = cursor.getString(9),
                        fechaFin = cursor.getString(10)
                    )
                )
            }
        }
        return lista
    }

    fun actualizarReserva(
        idReserva: Int,
        clienteNombre: String,
        clienteDni: String,
        cantidad: Int,
        fechaInicio: String = "",
        fechaFin: String = ""
    ): Boolean {
        val db = writableDatabase
        var precioUnitario = 0.0
        var existe = false

        db.rawQuery(
            "SELECT precioUnitario FROM reservas WHERE idReserva = ?",
            arrayOf(idReserva.toString())
        ).use { cursor ->
            if (cursor.moveToFirst()) {
                precioUnitario = cursor.getDouble(0)
                existe = true
            }
        }
        if (!existe) return false

        val valores = ContentValues().apply {
            put("clienteNombre", clienteNombre)
            put("clienteDni", clienteDni)
            put("cantidad", cantidad)
            put("fechaInicio", fechaInicio)
            put("fechaFin", fechaFin)
            put("precioTotal", precioUnitario * cantidad)
        }
        return db.update(
            "reservas", valores, "idReserva = ?", arrayOf(idReserva.toString())
        ) > 0
    }

    /** Indica si ya existe una reserva del mismo auto que se cruce con las fechas dadas. */
    fun existeCruceAuto(
        servicio: String,
        ciudad: String,
        inicio: String,
        fin: String,
        excluirId: Int = 0
    ): Boolean {
        var hay = false
        readableDatabase.rawQuery(
            "SELECT COUNT(*) FROM reservas WHERE tipo = 'AUTO' AND servicio = ? " +
                    "AND ciudad = ? AND idReserva <> ? AND fechaInicio < ? AND fechaFin > ?",
            arrayOf(servicio, ciudad, excluirId.toString(), fin, inicio)
        ).use { cursor ->
            if (cursor.moveToFirst()) hay = cursor.getInt(0) > 0
        }
        return hay
    }

    /** Devuelve el id de la última reserva guardada (0 si no hay ninguna). */
    fun ultimoIdReserva(): Int {
        readableDatabase.rawQuery("SELECT MAX(idReserva) FROM reservas", null).use { cursor ->
            if (cursor.moveToFirst()) return cursor.getInt(0)
        }
        return 0
    }

    fun obtenerReserva(idReserva: Int): Reserva? {
        return obtenerReservas("").firstOrNull { it.idReserva == idReserva }
    }

    /** Reemplaza el catálogo local de un tipo con el que llega del servidor. */
    fun reemplazarCatalogo(tipo: TipoServicio, servicios: List<Servicio>) {
        val tabla = when (tipo) {
            TipoServicio.HOTEL -> "hoteles"
            TipoServicio.AUTO -> "autos"
            TipoServicio.TOUR -> "tours"
            TipoServicio.VUELO -> return
        }
        if (servicios.isEmpty()) return

        val db = writableDatabase
        db.beginTransaction()
        try {
            db.delete(tabla, null, null)
            for (s in servicios) {
                val valores = ContentValues().apply {
                    put("nombre", s.nombre)
                    put("ciudad", s.ciudad)
                    put("detalle", s.detalle)
                    put("precio", s.precio)
                }
                db.insert(tabla, null, valores)
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    fun eliminarReserva(idReserva: Int): Boolean {
        return writableDatabase.delete(
            "reservas", "idReserva = ?", arrayOf(idReserva.toString())
        ) > 0
    }
}
