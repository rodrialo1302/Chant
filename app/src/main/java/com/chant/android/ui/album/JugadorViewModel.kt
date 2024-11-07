package com.chant.android.ui.album

import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.chant.android.R
import com.chant.android.model.dao.TeamDAO
import com.chant.android.model.dao.PlayerDAO
import com.chant.android.model.entities.Player
import com.chant.android.model.entities.Team
import com.chant.android.databinding.FragmentAlbumBinding
import com.chant.android.databinding.ItemJugadorBinding

class JugadorViewModel(view : View) : RecyclerView.ViewHolder(view) {

    private val bindingJugador = ItemJugadorBinding.bind(view)

    fun renderJugador(jugador : Player){
        val imagenJugador = bindingJugador.Jugador.context.resources.getIdentifier("pl_${jugador.id}", "drawable", bindingJugador.Jugador.context.packageName)
        bindingJugador.Jugador.setImageResource(imagenJugador)
        bindingJugador.JugadorNombre.text = jugador.name
    }
}