package com.chant.android.ui.album

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chant.android.model.entities.Team
import com.chant.android.R
import com.chant.android.model.entities.Player

class JugadorAdapter(private val jugadores : List<Player>) : RecyclerView.Adapter<JugadorViewModel>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JugadorViewModel {
        val layoutInflater = LayoutInflater.from(parent.context)
        return JugadorViewModel(layoutInflater.inflate(R.layout.item_jugador, parent, false))
    }

    override fun getItemCount(): Int {
        return jugadores.size
    }

    override fun onBindViewHolder(holder: JugadorViewModel, position: Int) {
        val item = jugadores[position]
        holder.renderJugador(item)
    }


}