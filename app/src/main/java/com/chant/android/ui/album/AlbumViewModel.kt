package com.chant.android.ui.album

import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.chant.android.R
import com.chant.android.model.dao.TeamDAO
import com.chant.android.model.dao.PlayerDAO
import com.chant.android.model.entities.Player
import com.chant.android.model.entities.Team
import com.chant.android.databinding.FragmentAlbumBinding
import com.chant.android.databinding.ItemEquipoBinding

class AlbumViewModel(view : View) : RecyclerView.ViewHolder(view) {

    private val bindingTeam = ItemEquipoBinding.bind(view)

    fun renderEquipos(equipo: Team, onClickListener:(Team) -> Unit){
        val logoEquipo = bindingTeam.TeamLogo.context.resources.getIdentifier("tm_${equipo.id}", "drawable", bindingTeam.TeamLogo.context.packageName)
        bindingTeam.TeamName.text = equipo.name
        bindingTeam.TeamLogo.setImageResource(logoEquipo)
        itemView.setOnClickListener { onClickListener(equipo) }

    }

}