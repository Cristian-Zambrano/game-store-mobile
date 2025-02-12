package com.example.game_store_mobile

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.game_store_mobile.modelo.Videojuego
import com.google.android.material.snackbar.Snackbar

class CrearVideojuegoActivity  : AppCompatActivity() {
    private var catalogoVideojuegosId: Int = 0
    private var videojuegoId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_crear_videojuego)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        catalogoVideojuegosId = intent.getIntExtra("catalogoVideojuegoId", -1)
        videojuegoId = intent.getIntExtra("videojuegoId", 0)

        val btnGuardarVideojuego = findViewById<Button>(R.id.btn_crear_videojuego)
        val itNombre = findViewById<EditText>(R.id.it_nombre_videojuego)
        val itDesarrollador = findViewById<EditText>(R.id.it_desarrollador_videojuego)

        // If editing an existing planet, populate the fields with the existing data
        if (videojuegoId != 0) {
            itNombre.setText(intent.getStringExtra("nombre"))
            itDesarrollador.setText(intent.getStringExtra("desarrollador"))
        }

        btnGuardarVideojuego.setOnClickListener {
            val nuevoNombre = itNombre.text.toString()
            val nuevoDesarrollador = itDesarrollador.text.toString()

            if (nuevoNombre.isEmpty() || nuevoDesarrollador.isEmpty()) {
                mostrarSnackbar("Por favor, llene todos los campos")
            } else {
                val videojuego = Videojuego(
                    id = videojuegoId,
                    nombre = nuevoNombre,
                    desarrollador = nuevoDesarrollador,
                    catalogoVideojuegoId = catalogoVideojuegosId
                )

                val exito = if (videojuegoId == 0) {
                    BDSQLite.bdsqLite?.crearVideojuego(videojuego) ?: false
                } else {
                    BDSQLite.bdsqLite?.actualizarVideojuego(videojuego) ?: false
                }

                if (exito) {
                    mostrarSnackbar("Videojuego ${if (videojuegoId == 0) "creado" else "actualizado"} exitosamente")
                } else {
                    mostrarSnackbar("Error al ${if (videojuegoId == 0) "crear" else "actualizar"} el videojuego")
                }
            }
        }
    }

    fun mostrarSnackbar(texto: String) {
        val snack = Snackbar.make(
            findViewById(R.id.main),
            texto,
            Snackbar.LENGTH_LONG
        )
        snack.show()
    }
}