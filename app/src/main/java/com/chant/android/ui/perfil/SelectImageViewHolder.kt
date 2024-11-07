package com.chant.android.ui.perfil

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.chant.android.databinding.ItemPfpBinding


class SelectImageViewHolder(view : View) : RecyclerView.ViewHolder(view) {

    private val bindingPfp = ItemPfpBinding.bind(view)



    fun renderPfps(pfp: Int, onClickListener:(Int) -> Unit){
        val image = bindingPfp.profilePhoto.context.resources.getIdentifier("pfp_${pfp}", "drawable", bindingPfp.profilePhoto.context.packageName)
        bindingPfp.profilePhoto.setImageResource(image)
        bindingPfp.profilePhoto.setOnClickListener{
            onClickListener(pfp)
        }

    }


}

