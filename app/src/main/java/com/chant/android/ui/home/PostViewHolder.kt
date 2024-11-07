package com.chant.android.ui.home

import android.graphics.Color
import android.graphics.PorterDuff
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.chant.android.databinding.ItemPostBinding
import com.chant.android.model.entities.Post
import java.time.format.DateTimeFormatter

class PostViewHolder(view : View) : RecyclerView.ViewHolder(view) {
    private val bindingPost = ItemPostBinding.bind(view)


    fun renderPost(post: Post, onClickListener:(Post) -> Unit, onClickListenerProfile: (String) -> Unit, onClickListenerLike: (ItemPostBinding, Post) -> Unit){
        bindingPost.username.text = post.author
        bindingPost.textoPost.text = post.text


        val username = bindingPost.username.context.getSharedPreferences("shared_prefs", AppCompatActivity.MODE_PRIVATE).getString("user_key", null)
        val found = post.likes.map{it.author }.any{it == username}



        if (post.author != "deleted") {
            bindingPost.textNumLikes.text = post.likes.size.toString()
            bindingPost.textoFechaHora.text = post.date.format(
                DateTimeFormatter.ofPattern("HH:mm • dd/MM/yyyy")
            )
            if (found) {
                bindingPost.imgLike.setColorFilter(
                    Color.parseColor("#FFAE1E36"),
                    PorterDuff.Mode.SRC_ATOP
                )
            } else {
                bindingPost.imgLike.setColorFilter(
                    Color.parseColor("#7E7D7D"),
                    PorterDuff.Mode.SRC_ATOP
                )
            }

            val pfp = bindingPost.imagePfp.context.resources.getIdentifier(
                "pfp_${post.pfp}",
                "drawable",
                bindingPost.imagePfp.context.packageName
            )
            bindingPost.imagePfp.setImageResource(pfp)


            bindingPost.username.setOnClickListener { onClickListenerProfile(post.author) }
            bindingPost.imagePfp.setOnClickListener { onClickListenerProfile(post.author) }
            bindingPost.imgLike.setOnClickListener { onClickListenerLike(bindingPost, post) }

        }
        else{
            bindingPost.textoFechaHora.visibility = View.INVISIBLE
            bindingPost.textNumLikes.visibility = View.INVISIBLE
            bindingPost.imgLike.visibility = View.INVISIBLE

        }

        itemView.setOnClickListener { onClickListener(post) }

    }
}