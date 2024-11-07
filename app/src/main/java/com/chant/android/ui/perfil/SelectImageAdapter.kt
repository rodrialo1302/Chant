package com.chant.android.ui.perfil

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chant.android.R


class SelectImageAdapter(private var pfpList:ArrayList<Int>, private val onClickListener:(Int) -> Unit ) : RecyclerView.Adapter<SelectImageViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectImageViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return SelectImageViewHolder(layoutInflater.inflate(R.layout.item_pfp, parent, false))
    }
    override fun getItemCount(): Int = pfpList.size


    override fun onBindViewHolder(holder: SelectImageViewHolder, position: Int) {
        val item = pfpList[position]
        holder.renderPfps(item, onClickListener)

    }
}