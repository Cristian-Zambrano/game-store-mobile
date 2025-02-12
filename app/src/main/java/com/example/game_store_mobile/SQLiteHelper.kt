package com.example.game_store_mobile

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
                    maximoNumeroDeJuegos INTEGER
                )
            """.trimIndent()
        db?.execSQL(scriptSQLCrearTablaCatalogoVideojuego)

        val scriptSQLCrearTablaVideojuego =
            """
                CREATE TABLE Videojuego(
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nombre VARCHAR(50),
                    desarrollador VARCHAR(50),
                    catalogoVideojuegoId INTEGER,
                    FOREIGN KEY(catalogoVideojuegoId) REFERENCES CatalogoVideojuego(id) ON DELETE CASCADE
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
        valoresGuardar.put("maximoNumeroDeJuegos", CatalogoVideojuego.maximoNumeroDeJuegos)
        val resultadoGuardar = baseDatosEscritura
            .insert(
                "CatalogoVideojuego", // nombre tabla
                null,
                valoresGuardar // valores
            )
        baseDatosEscritura.close()
        return if (resultadoGuardar.toInt() == -1) false else true
    }

    fun listarCatalogoVideojuego(): ArrayList<CatalogoVideojuego> {
        val scriptSQLConsultarCatalogoVideojuego = "SELECT * FROM CatalogoVideojuego"
        val baseDatosLectura = readableDatabase
        val resultadoConsultaLectura = baseDatosLectura.rawQuery(
            scriptSQLConsultarCatalogoVideojuego,
            null
        )
        val catalogoVideojuego = ArrayList<CatalogoVideojuego>()

        while (resultadoConsultaLectura.moveToNext()) {
            catalogoVideojuego.add(
                CatalogoVideojuego(
                    resultadoConsultaLectura.getInt(0),
                    resultadoConsultaLectura.getString(1),
                    resultadoConsultaLectura.getInt(2),
                )
            )
        }

        resultadoConsultaLectura.close()
        baseDatosLectura.close()
        return catalogoVideojuego

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
                    maximoNumeroDeJuegos = ?
                WHERE id = ?
            """.trimIndent()
        val parametrosActualizar = arrayOf(
            CatalogoVideojuego.nombre,
            CatalogoVideojuego.maximoNumeroDeJuegos.toString(),
            CatalogoVideojuego.id.toString()
        )
        baseDatosEscritura.execSQL(scriptActualizarCatalogoVideojuego, parametrosActualizar)
        baseDatosEscritura.close()

    }


    fun crearVideojuego(Videojuego: Videojuego): Boolean {
        val baseDatosEscritura = writableDatabase
        val scriptCrearVideojuego =
            """
            INSERT INTO Videojuego(nombre, desarrollador, catalogoVideojuegoId)
            VALUES(?, ?, ?)
        """.trimIndent()
        val parametrosCrear = arrayOf(
            Videojuego.nombre,
            Videojuego.desarrollador,
            Videojuego.catalogoVideojuegoId.toString()
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
                "                    desarrollador VARCHAR(50),\n" +
                "                    catalogoVideojuegoId INTEGER,\n" +
                "                    FOREIGN KEY(catalogoVideojuegoId) REFERENCES CatalogoVideojuego(id) ON DELETE CASCADE\n" +
                "                )")

    }

    fun listarVideojuegos(catalogoVideojuegoId: Int): ArrayList<Videojuego> {
        val scriptSQLConsultarVideojuego = "SELECT * FROM Videojuego WHERE catalogoVideojuegoId = ?"
        val baseDatosLectura = readableDatabase
        val resultadoConsultaLectura = baseDatosLectura.rawQuery(
            scriptSQLConsultarVideojuego,
            arrayOf(catalogoVideojuegoId.toString())
        )
        val Videojuegos = ArrayList<Videojuego>()

        while (resultadoConsultaLectura.moveToNext()) {
            Videojuegos.add(
                Videojuego(
                    resultadoConsultaLectura.getInt(0),
                    resultadoConsultaLectura.getString(1),
                    resultadoConsultaLectura.getString(2),
                    resultadoConsultaLectura.getInt(3),
                )
            )
        }

        resultadoConsultaLectura.close()
        baseDatosLectura.close()
        return Videojuegos
    }

    fun eliminarVideojuego(Videojuego_id: Int, catalogoVideojuegoId: Int) {
        val baseDatosEscritura = writableDatabase
        val scriptEliminarVideojuego = "DELETE FROM Videojuego WHERE id = ? AND catalogoVideojuegoId = ?"
        val parametrosEliminar = arrayOf(Videojuego_id.toString(), catalogoVideojuegoId.toString())
        baseDatosEscritura.execSQL(scriptEliminarVideojuego, parametrosEliminar)
        baseDatosEscritura.close()

    }

    fun actualizarVideojuego(Videojuego: Videojuego): Boolean {
        val baseDatosEscritura = writableDatabase
        val scriptActualizarVideojuego =
            """
                UPDATE Videojuego
                SET nombre = ?,
                    desarrollador = ?
                WHERE id = ?
            """.trimIndent()
        val parametrosActualizar = arrayOf(
            Videojuego.nombre,
            Videojuego.desarrollador,
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
