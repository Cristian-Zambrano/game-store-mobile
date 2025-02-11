package com.example.game_store_mobile

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.sistema_solar_crud.BDSQLite
import com.google.android.material.snackbar.Snackbar

class CrearVideojuegoActivity  : AppCompatActivity() {
    private var sistemaSolarId: Int = 0
    private var planetaId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_crear_videojuego)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        sistemaSolarId = intent.getIntExtra("sistemaSolarId", -1)
        planetaId = intent.getIntExtra("planetaId", 0)

        val btnGuardarPlaneta = findViewById<Button>(R.id.btn_guardar_p)
        val itNombre = findViewById<EditText>(R.id.it_nombre_p)
        val itTipo = findViewById<EditText>(R.id.it_tipo_p)
        val itDistancia = findViewById<EditText>(R.id.it_distancia_p)

        // If editing an existing planet, populate the fields with the existing data
        if (planetaId != 0) {
            itNombre.setText(intent.getStringExtra("nombre"))
            itTipo.setText(intent.getStringExtra("tipo"))
            itDistancia.setText(intent.getIntExtra("distancia", 0).toString())
        }

        btnGuardarPlaneta.setOnClickListener {
            val nuevoNombre = itNombre.text.toString()
            val nuevoTipo = itTipo.text.toString()
            val nuevaDistancia = itDistancia.text.toString()

            if (nuevoNombre.isEmpty() || nuevoTipo.isEmpty() || nuevaDistancia.isEmpty()) {
                mostrarSnackbar("Por favor, llene todos los campos")
            } else {
                val planeta = Planeta(
                    id = planetaId,
                    nombre = nuevoNombre,
                    tipo = nuevoTipo,
                    distancia = nuevaDistancia.toInt(),
                    sistemaSolarId = sistemaSolarId
                )

                val exito = if (planetaId == 0) {
                    BDSQLite.bdsqLite?.crearPlaneta(planeta) ?: false
                } else {
                    BDSQLite.bdsqLite?.actualizarPlaneta(planeta) ?: false
                }

                if (exito) {
                    mostrarSnackbar("Planeta ${if (planetaId == 0) "creado" else "actualizado"} exitosamente")
                } else {
                    mostrarSnackbar("Error al ${if (planetaId == 0) "crear" else "actualizar"} el planeta")
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