package com.chant.android.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chant.android.R
import com.chant.android.model.entities.Match

class PartidoAdapter(private var partidoList:ArrayList<Match>, private val onClickListener:(Match) -> Unit ) : RecyclerView.Adapter<HomeViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return HomeViewHolder(layoutInflater.inflate(R.layout.item_partido, parent, false))
    }
    override fun getItemCount(): Int = partidoList.size


    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        val item = partidoList[position]
        holder.renderPartidos(item, onClickListener)

    }
}