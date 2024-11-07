package com.chant.android.ui.tienda

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import com.chant.android.databinding.ActivityAbrirSobreBinding
import com.chant.android.model.entities.Player
import com.chant.android.ui.album.JugadorAdapter
import kotlin.concurrent.thread

class AbrirSobreActivity : AppCompatActivity() {
    private lateinit var binding : ActivityAbrirSobreBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAbrirSobreBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val jugadoresObtenidos = intent.extras?.get("JUGADORES_SOBRE") as ArrayList<Player>

        thread{
            runOnUiThread{
                initRecyclerView(jugadoresObtenidos)
            }
        }
    }

    private fun initRecyclerView(jugadores : List<Player>){
        binding.recyclerAbrirSobre.layoutManager = GridLayoutManager(this, 3)
        binding.recyclerAbrirSobre.adapter = JugadorAdapter(jugadores)

        binding.recyclerAbrirSobre.addItemDecoration(
            DividerItemDecoration(
                this,
                (binding.recyclerAbrirSobre.layoutManager as GridLayoutManager).orientation
            )
        )

    }
}