package com.example.sistema_solar_crud

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.game_store_mobile.modelo.Videojuego
import com.example.game_store_mobile.modelo.CatalogoVideojuego

class SQLiteHelper(
    contexto: Context? // this
) : SQLiteOpenHelper(
    contexto,
    "moviles",
    null,
    1
) {
    override fun onCreate(db: SQLiteDatabase?) {
        val scriptSQLCrearTablaCatalogoVideojuego =
            """
                CREATE TABLE CatalogoVideojuego(
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nombre VARCHAR(50),
                    descripcion VARCHAR(50),
                    tamanio INTEGER,
                    latitud DOUBLE,
                    longitud DOUBLE
                )
            """.trimIndent()
        db?.execSQL(scriptSQLCrearTablaCatalogoVideojuego)

        val scriptSQLCrearTablaVideojuego =
            """
                CREATE TABLE Videojuego(
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nombre VARCHAR(50),
                    tipo VARCHAR(50),
                    distancia_al_sol INTEGER,
                    sistema_solar_id INTEGER,
                    FOREIGN KEY(sistema_solar_id) REFERENCES CatalogoVideojuego(id) ON DELETE CASCADE
                )
            """.trimIndent()
        db?.execSQL(scriptSQLCrearTablaVideojuego)

    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        TODO("Not yet implemented")
    }

    fun registrarCatalogoVideojuego(
        CatalogoVideojuego: CatalogoVideojuego
    ): Boolean {
        val baseDatosEscritura = writableDatabase

        val valoresGuardar = ContentValues()
        valoresGuardar.put("nombre", CatalogoVideojuego.nombre)
        valoresGuardar.put("descripcion", CatalogoVideojuego.descripcion)
        valoresGuardar.put("tamanio", CatalogoVideojuego.tamanio)
        valoresGuardar.put("latitud", CatalogoVideojuego.latitud)
        valoresGuardar.put("longitud", CatalogoVideojuego.longitud)
        val resultadoGuardar = baseDatosEscritura
            .insert(
                "CatalogoVideojuego", // nombre tabla
                null,
                valoresGuardar // valores
            )
        baseDatosEscritura.close()
        return if (resultadoGuardar.toInt() == -1) false else true
    }

    fun listarSistemasSolares(): ArrayList<CatalogoVideojuego> {
        val scriptSQLConsultarCatalogoVideojuego = "SELECT * FROM CatalogoVideojuego"
        val baseDatosLectura = readableDatabase
        val resultadoConsultaLectura = baseDatosLectura.rawQuery(
            scriptSQLConsultarCatalogoVideojuego,
            null
        )
        val sistemasSolares = ArrayList<CatalogoVideojuego>()

        while (resultadoConsultaLectura.moveToNext()) {
            sistemasSolares.add(
                CatalogoVideojuego(
                    resultadoConsultaLectura.getInt(0),
                    resultadoConsultaLectura.getString(1),
                    resultadoConsultaLectura.getString(2),
                    resultadoConsultaLectura.getInt(3),
                    resultadoConsultaLectura.getDouble(4),
                    resultadoConsultaLectura.getDouble(5)
                )
            )
        }

        resultadoConsultaLectura.close()
        baseDatosLectura.close()
        return sistemasSolares

    }

    fun eliminarCatalogoVideojuego(id: Int) {
        val baseDatosEscritura = writableDatabase
        val scriptEliminarCatalogoVideojuego = "DELETE FROM CatalogoVideojuego WHERE id = ?"
        val parametrosEliminar = arrayOf(id.toString())
        baseDatosEscritura.execSQL(scriptEliminarCatalogoVideojuego, parametrosEliminar)
        baseDatosEscritura.close()

    }

    fun actualizarCatalogoVideojuego(CatalogoVideojuego: CatalogoVideojuego) {
        val baseDatosEscritura = writableDatabase
        val scriptActualizarCatalogoVideojuego =
            """
                UPDATE CatalogoVideojuego
                SET nombre = ?,
                    descripcion = ?,
                    tamanio = ?,
                    latitud = ?,
                    longitud = ?
                WHERE id = ?
            """.trimIndent()
        val parametrosActualizar = arrayOf(
            CatalogoVideojuego.nombre,
            CatalogoVideojuego.descripcion,
            CatalogoVideojuego.tamanio.toString(),
            CatalogoVideojuego.latitud.toString().toDouble(),
            CatalogoVideojuego.longitud.toString().toDouble(),
            CatalogoVideojuego.id.toString()
        )
        baseDatosEscritura.execSQL(scriptActualizarCatalogoVideojuego, parametrosActualizar)
        baseDatosEscritura.close()

    }


    fun crearVideojuego(Videojuego: Videojuego): Boolean {
        val baseDatosEscritura = writableDatabase
        val scriptCrearVideojuego =
            """
            INSERT INTO Videojuego(nombre, tipo, distancia_al_sol, sistema_solar_id)
            VALUES(?, ?, ?, ?)
        """.trimIndent()
        val parametrosCrear = arrayOf(
            Videojuego.nombre,
            Videojuego.tipo,
            Videojuego.distancia.toString(),
            Videojuego.CatalogoVideojuegoId.toString()
        )
        return try {
            baseDatosEscritura.execSQL(scriptCrearVideojuego, parametrosCrear)
            baseDatosEscritura.close()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            baseDatosEscritura.close()
            false
        }
    }

    fun createVideojuegoTableIfNotExists() {
        writableDatabase.execSQL("CREATE TABLE IF NOT EXISTS Videojuego(\n" +
                "                    id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "                    nombre VARCHAR(50),\n" +
                "                    tipo VARCHAR(50),\n" +
                "                    distancia_al_sol INTEGER,\n" +
                "                    sistema_solar_id INTEGER,\n" +
                "                    FOREIGN KEY(sistema_solar_id) REFERENCES CatalogoVideojuego(id) ON DELETE CASCADE\n" +
                "                )")

    }

    fun listarVideojuegos(CatalogoVideojuegoId: Int): ArrayList<Videojuego> {
        val scriptSQLConsultarVideojuego = "SELECT * FROM Videojuego WHERE sistema_solar_id = ?"
        val baseDatosLectura = readableDatabase
        val resultadoConsultaLectura = baseDatosLectura.rawQuery(
            scriptSQLConsultarVideojuego,
            arrayOf(CatalogoVideojuegoId.toString())
        )
        val Videojuegos = ArrayList<Videojuego>()

        while (resultadoConsultaLectura.moveToNext()) {
            Videojuegos.add(
                Videojuego(
                    resultadoConsultaLectura.getInt(0),
                    resultadoConsultaLectura.getString(1),
                    resultadoConsultaLectura.getString(2),
                    resultadoConsultaLectura.getInt(3),
                    resultadoConsultaLectura.getInt(4)
                )
            )
        }

        resultadoConsultaLectura.close()
        baseDatosLectura.close()
        return Videojuegos
    }

    fun eliminarVideojuego(Videojuego_id: Int, sistema_solar_id: Int) {
        val baseDatosEscritura = writableDatabase
        val scriptEliminarVideojuego = "DELETE FROM Videojuego WHERE id = ? AND sistema_solar_id = ?"
        val parametrosEliminar = arrayOf(Videojuego_id.toString(), sistema_solar_id.toString())
        baseDatosEscritura.execSQL(scriptEliminarVideojuego, parametrosEliminar)
        baseDatosEscritura.close()

    }

    fun actualizarVideojuego(Videojuego: Videojuego): Boolean {
        val baseDatosEscritura = writableDatabase
        val scriptActualizarVideojuego =
            """
                UPDATE Videojuego
                SET nombre = ?,
                    tipo = ?,
                    distancia_al_sol = ?
                WHERE id = ?
            """.trimIndent()
        val parametrosActualizar = arrayOf(
            Videojuego.nombre,
            Videojuego.tipo,
            Videojuego.distancia.toString(),
            Videojuego.id.toString()
        )
        return try {
            baseDatosEscritura.execSQL(scriptActualizarVideojuego, parametrosActualizar)
            baseDatosEscritura.close()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            baseDatosEscritura.close()
            false
        }


    }


}
