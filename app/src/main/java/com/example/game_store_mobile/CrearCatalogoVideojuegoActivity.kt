package com.example.game_store_mobile

import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.game_store_mobile.modelo.CatalogoVideojuego
import com.google.android.material.snackbar.Snackbar


class CrearCatalogoVideojuegoActivity : AppCompatActivity() {
    private var catalogoVideojuegoId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_crear_catalogo_videojuego)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnCatalogoVideojuego = findViewById<Button>(R.id.btn_guardar_catalogo)
        val itNombre = findViewById<EditText>(R.id.it_nombre_catalogo)
        val itMaximoVideojuegos = findViewById<EditText>(R.id.it_maximo_videojuego)

        catalogoVideojuegoId = intent.getIntExtra("catalogoVideojuegoId", -1)
        val nombre = intent.getStringExtra("nombre")
        val maximoNumeroDeJuegos = intent.getIntExtra("maximoNumeroDeJuegos", -1)
        if (catalogoVideojuegoId != -1 && nombre != null && maximoNumeroDeJuegos != null) {
            itNombre.setText(nombre)
            itMaximoVideojuegos.setText(maximoNumeroDeJuegos.toString())
        }


        btnCatalogoVideojuego.setOnClickListener {
            val nuevoNombre = itNombre.text.toString()
            val nuevaMaximoVideojuego = itMaximoVideojuegos.text.toString().toInt()

            if (nuevoNombre.isEmpty() || nuevaMaximoVideojuego == 0) {
                mostrarSnackbar("Por favor, llene todos los campos")
            } else {
                val catalogoVideojuego = CatalogoVideojuego(
                    id = catalogoVideojuegoId ?: 0,
                    nombre = nuevoNombre,
                    maximoNumeroDeJuegos = nuevaMaximoVideojuego
                )

                if (catalogoVideojuegoId != -1) {
                    BDSQLite.bdsqLite?.actualizarCatalogoVideojuego(catalogoVideojuego)
                    mostrarSnackbar("Catalogo de videojuego actualizado")
                } else {
                    BDSQLite.bdsqLite?.registrarCatalogoVideojuego(catalogoVideojuego)
                    mostrarSnackbar("Catalogo de videojuego guardado")
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