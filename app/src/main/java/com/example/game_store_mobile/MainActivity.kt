package com.example.game_store_mobile

import android.content.Intent
import android.os.Bundle
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.game_store_mobile.modelo.CatalogoVideojuego
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {
    private lateinit var catalogoVideojuegos: ArrayList<CatalogoVideojuego>
    private lateinit var btnAgregarCatalogoVideojuego: Button
    private lateinit var listViewCatalogoVideojuego: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //inicializar bd
        BDSQLite.bdsqLite = SQLiteHelper(this)

        listViewCatalogoVideojuego = findViewById(R.id.ls_sistemas_solares)
        btnAgregarCatalogoVideojuego = findViewById(R.id.btn_crear_catalogo)

        registerForContextMenu(listViewCatalogoVideojuego)

        btnAgregarCatalogoVideojuego.setOnClickListener {
            startActivity(Intent(this, CrearCatalogoVideojuegoActivity::class.java))
        }

        //mostrar-actualizar la lista
        actualizarLista()

    }

    private fun actualizarLista() {
        catalogoVideojuegos = BDSQLite.bdsqLite?.listarCatalogoVideojuego() ?: ArrayList()
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            catalogoVideojuegos.map { it.nombre }
        )
        listViewCatalogoVideojuego.adapter = adapter
    }


    override fun onCreateContextMenu(
        menu: ContextMenu?,
        v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menuInflater.inflate(R.menu.menu_catalogo_videojuego, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val info = item.menuInfo as? AdapterView.AdapterContextMenuInfo
        val catalogoVideojuegoIndex = info?.position
        val catalogoVideojuegoSeleccionado = catalogoVideojuegoIndex?.let { catalogoVideojuegos[it] }

        when (item.itemId) {
            R.id.m_eliminar_catalogo_videojuego -> {
                catalogoVideojuegoSeleccionado?.let {
                    BDSQLite.bdsqLite?.eliminarCatalogoVideojuego(it.id)
                    mostrarSnackbar("Catalogo Videojuego ${it.nombre} eliminado")
                    actualizarLista()
                }
            }
            R.id.m_ver_catalogo_videojuego -> {
                catalogoVideojuegoSeleccionado?.let {
                    val intent = Intent(this, VideojuegoActivity::class.java)
                    intent.putExtra("catalogoVideojuegoId", it.id)
                    mostrarSnackbar("Ver videojuegos del catalogo ${it.id}")
                    startActivity(intent)
                }
            }
            R.id.m_editar_catalogo_videojuego -> {
                catalogoVideojuegoSeleccionado?.let {
                    mostrarSnackbar("Editar catalogo de videojuego ${it.nombre}")
                    val intent = Intent(this, CrearCatalogoVideojuegoActivity::class.java)
                    intent.putExtra("catalogoId", it.id)
                    intent.putExtra("nombre", it.nombre)
                    intent.putExtra("desarrollador", it.desarrollador)
                    startActivity(intent)
                }
            }
        }
        return super.onContextItemSelected(item)

    }

    override fun onResume() {
        super.onResume()
        actualizarLista()
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

