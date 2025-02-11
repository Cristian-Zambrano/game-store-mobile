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
import com.example.sistema_solar_crud.BDSQLite
import com.google.android.material.snackbar.Snackbar

class VideojuegoActivity  : AppCompatActivity() {
    private var sistemaSolarId: Int? = null
    private lateinit var planetas: ArrayList<Planeta>
    private lateinit var btnAgregarPlaneta: Button
    private lateinit var listViewPlanetas: ListView

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

        sistemaSolarId = intent.getIntExtra("sistemaSolarId", 0)
        mostrarSnackbar("Sistema Solar ID: $sistemaSolarId")
        listViewPlanetas = findViewById(R.id.ls_planetas)
        btnAgregarPlaneta = findViewById(R.id.btn_crear_p)

        registerForContextMenu(listViewPlanetas)
        actualizarLista()



        btnAgregarPlaneta.setOnClickListener {
            val intent = Intent(this, CrearPlanetaActivity::class.java)
            intent.putExtra("sistemaSolarId", sistemaSolarId)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        actualizarLista()
    }

    private fun actualizarLista() {
        planetas = sistemaSolarId?.let { BDSQLite.bdsqLite?.listarPlanetas(it) } ?: ArrayList()
        if (planetas.isEmpty()) {
            println("No se encontraron planetas para el sistema solar con ID: $sistemaSolarId")
        } else {
            println("Planetas encontrados: ${planetas.map { it.nombre }}")
        }
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            planetas.map { it.nombre }
        )
        listViewPlanetas.adapter = adapter
    }

    override fun onCreateContextMenu(
        menu: ContextMenu?,
        v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menuInflater.inflate(R.menu.menu_p, menu)

    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val info = item.menuInfo as? AdapterView.AdapterContextMenuInfo
        val planetaIndex = info?.position
        val planetaSeleccionado = planetaIndex?.let { planetas[it] }

        when (item.itemId) {
            R.id.m_eliminar_p -> {
                planetaSeleccionado?.let {
                    BDSQLite.bdsqLite?.eliminarPlaneta(it.id, sistemaSolarId ?: 0)
                    mostrarSnackbar("Planeta ${it.nombre} eliminado")
                    actualizarLista()
                }
            }
            R.id.m_editar_p -> {
                planetaSeleccionado?.let {
                    mostrarSnackbar("Editar planeta ${it.nombre}")
                    val intent = Intent(this, CrearPlanetaActivity::class.java)
                    intent.putExtra("planetaId", it.id)
                    intent.putExtra("nombre", it.nombre)
                    intent.putExtra("tipo", it.tipo)
                    intent.putExtra("distancia", it.distancia)
                    intent.putExtra("sistemaSolarId", sistemaSolarId)
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