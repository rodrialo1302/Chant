package com.chant.android.ui.album

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chant.android.R
import com.chant.android.databinding.ActivityEntradaAlbumBinding
import com.chant.android.model.dao.PlayerDAO
import com.chant.android.model.entities.Player
import com.chant.android.model.entities.Team
import kotlin.concurrent.thread
import android.util.Log
import android.view.MenuItem
import com.chant.android.model.dao.UserDAO

class EntradaAlbumActivity : AppCompatActivity() {
    private lateinit var binding : ActivityEntradaAlbumBinding
    private var sharedpreferences: SharedPreferences? = null

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEntradaAlbumBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        sharedpreferences = getSharedPreferences("shared_prefs", AppCompatActivity.MODE_PRIVATE)
        val equipo = intent.extras?.get("EQUIPO") as Team
        title = equipo.name

        thread{
            val username = sharedpreferences?.getString("user_key", null)
            val jugadoresObtenidosEquipo = PlayerDAO.getPlayersInTeamFromUser(UserDAO.getUser(username!!)!!.username, equipo.id)
            runOnUiThread {
                initRecyclerView(jugadoresObtenidosEquipo)
            }
        }

    }

    private fun initRecyclerView(jugadores : List<Player>){
        binding.recyclerEntradaAlbum.layoutManager = GridLayoutManager(this, 3)
        binding.recyclerEntradaAlbum.adapter = JugadorAdapter(jugadores)

        /*binding.recyclerEntradaAlbum.addItemDecoration(
            DividerItemDecoration(
                this,
                (binding.recyclerEntradaAlbum.layoutManager as GridLayoutManager).orientation
            )
        )*/

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home){
            onBackPressed()
            return true
        }
        return false
    }
}