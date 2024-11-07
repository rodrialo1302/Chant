package com.chant.android.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chant.android.R
import com.chant.android.databinding.ItemPostBinding
import com.chant.android.model.entities.Post

class PostAdapter(private val postList:List<Post>?,private val onClickListener: (Post) -> Unit,
                  private val onClickListenerProfile: (String) -> Unit,
                  private val onClickListenerLike: (ItemPostBinding, Post) -> Unit) : RecyclerView.Adapter<PostViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return PostViewHolder(layoutInflater.inflate(R.layout.item_post, parent,false))
    }

    override fun getItemCount(): Int {
        if(postList!= null){
            return postList.size
        }
        return 0
    }


    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val item = postList?.get(position)
        if (item != null) {
            holder.renderPost(item, onClickListener, onClickListenerProfile, onClickListenerLike)
        }
    }

}