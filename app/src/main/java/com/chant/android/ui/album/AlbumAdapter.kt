package com.chant.android.ui.album

import android.content.DialogInterface.OnClickListener
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chant.android.model.entities.Team
import com.chant.android.R

class AlbumAdapter(private val equipos : List<Team>, private val onClickListener:(Team) -> Unit) : RecyclerView.Adapter<AlbumViewModel>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumViewModel {
        val layoutInflater = LayoutInflater.from(parent.context)

        return AlbumViewModel(layoutInflater.inflate(R.layout.item_equipo, parent, false))
    }

    override fun getItemCount(): Int {
        return equipos.size
    }

    override fun onBindViewHolder(holder: AlbumViewModel, position: Int) {
        val item = equipos[position]
        holder.renderEquipos(item, onClickListener)
    }

}