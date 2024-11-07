package com.chant.android.ui.home

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.chant.android.databinding.ItemPartidoBinding
import com.chant.android.model.entities.Match
import java.time.format.DateTimeFormatter

class HomeViewHolder(view : View) : RecyclerView.ViewHolder(view) {

    private val bindingPartido = ItemPartidoBinding.bind(view)



    fun renderPartidos(partido: Match, onClickListener:(Match) -> Unit){
        bindingPartido.teamAName.text = partido.team1.name
        bindingPartido.teamBName.text = partido.team2.name
        bindingPartido.score.text = partido.goals1.toString()+ " - "+ partido.goals2.toString()
        //bindingPartido.timePassed.text = "..." // cambiar
        when (partido.status){
            0 -> bindingPartido.timePassed.text = partido.startDate.format(DateTimeFormatter.ofPattern("HH:mm • dd/MM/yyyy"))
            1 -> bindingPartido.timePassed.text = "\uD83D\uDD34LIVE: Primer Tiempo"
            2 -> bindingPartido.timePassed.text = "\uD83D\uDD34LIVE: Descanso"
            3 -> bindingPartido.timePassed.text = "\uD83D\uDD34LIVE: Segundo Tiempo"
            4 -> bindingPartido.timePassed.text = partido.startDate.format(DateTimeFormatter.ofPattern("HH:mm • dd/MM/yyyy"))
            else -> {
                bindingPartido.timePassed.text = ""
            }
        }


        val logo1 = bindingPartido.teamALogo.context.resources.getIdentifier("tm_${partido.team1.id}", "drawable", bindingPartido.teamALogo.context.packageName)
        bindingPartido.teamALogo.setImageResource(logo1)
        val logo2 = bindingPartido.teamALogo.context.resources.getIdentifier("tm_${partido.team2.id}", "drawable", bindingPartido.teamBLogo.context.packageName)
        bindingPartido.teamBLogo.setImageResource(logo2)

        itemView.setOnClickListener{ onClickListener(partido)}
    }


    }

