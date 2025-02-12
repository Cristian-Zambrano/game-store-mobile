package com.example.game_store_mobile

import android.content.Intent
import android.os.Bundle
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.snackbar.Snackbar

class VideojuegoActivity  : AppCompatActivity() {
    private var catalogoVideojuegoId: Int? = null
    private lateinit var videojuegos: ArrayList<Videojuegos>
    private lateinit var btnAgregarPlaneta: Button
    private lateinit var listViewVideojuegos: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_planeta)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Check if the table exists and create it if necessary
        BDSQLite.bdsqLite?.createPlanetaTableIfNotExists()

        catalogoVideojuegoId = intent.getIntExtra("catalogovideojuegoId", 0)
        mostrarSnackbar("Catalogo Videojuego ID: $catalogoVideojuegoId")
        listViewVideojuegos = findViewById(R.id.ls_planetas)
        btnAgregarPlaneta = findViewById(R.id.btn_crear_v)

        registerForContextMenu(listViewVideojuegos)
        actualizarLista()



        btnAgregarPlaneta.setOnClickListener {
            val intent = Intent(this, CrearPlanetaActivity::class.java)
            intent.putExtra("catalogoVideojuegoId", catalogoVideojuegoId)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        actualizarLista()
    }

    private fun actualizarLista() {
        videojuegos = catalogoVideojuegoId?.let { BDSQLite.bdsqLite?.listarPlanetas(it) } ?: ArrayList()
        if (videojuegos.isEmpty()) {
            println("No se encontraron videojuegos para el catalogo con ID: $catalogoVideojuegoId")
        } else {
            println("Planetas encontrados: ${videojuegos.map { it.nombre }}")
        }
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            videojuegos.map { it.nombre }
        )
        listViewVideojuegos.adapter = adapter
    }

    override fun onCreateContextMenu(
        menu: ContextMenu?,
        v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menuInflater.inflate(R.menu.menu_videojuego, menu)

    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val info = item.menuInfo as? AdapterView.AdapterContextMenuInfo
        val videojuegoIndex = info?.position
        val vdeojuegoSeleccionado = videojuegoIndex?.let { videojuegos[it] }

        when (item.itemId) {
            R.id.m_eliminar_videojuego -> {
                vdeojuegoSeleccionado?.let {
                    BDSQLite.bdsqLite?.eliminarPlaneta(it.id, videojuegoId ?: 0)
                    mostrarSnackbar("Videojuego ${it.nombre} eliminado")
                    actualizarLista()
                }
            }
            R.id.m_editar_videojuego -> {
                vdeojuegoSeleccionado?.let {
                    mostrarSnackbar("Editar videojuego ${it.nombre}")
                    val intent = Intent(this, CrearVideojuegoActivity::class.java)
                    intent.putExtra("videojuegoId", it.id)
                    intent.putExtra("nombre", it.nombre)
                    intent.putExtra("desarrollador", it.desarrollador)
                    intent.putExtra("catalogoVideojuegoId", catalogoVideojuegoId)
                    startActivity(intent)
                }
            }
        }
        return super.onContextItemSelected(item)
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