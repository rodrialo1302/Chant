package com.chant.android.ui.home

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.chant.android.R
import com.chant.android.databinding.ActivityCommentBinding
import com.chant.android.databinding.ItemPostBinding
import com.chant.android.model.dao.LikeDAO
import com.chant.android.model.dao.PostDAO
import com.chant.android.model.entities.Like
import com.chant.android.model.entities.Match
import com.chant.android.model.entities.Post
import com.chant.android.ui.perfil.ProfileAccessActivity
import java.io.Serializable
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.concurrent.thread

class CommentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCommentBinding
    private lateinit var recyclerView : RecyclerView
    private var postList : ArrayList<Post>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val post = intent.extras?.get("POST") as Post

        val swipeContainer = findViewById<SwipeRefreshLayout>(R.id.swiperefresh)
        swipeContainer.setOnRefreshListener {
            thread {
                postList= PostDAO.getReplyList(post.id)
                runOnUiThread {
                    recyclerView.adapter = null
                    initRecyclerAdapter()

                    swipeContainer.isRefreshing = false
                }
            }
        }
        val username = getSharedPreferences("shared_prefs", AppCompatActivity.MODE_PRIVATE).getString("user_key", null)

        binding.username.text = post.author
        binding.textoPost.text = post.text

        if (post.author != "deleted"){
        binding.textNumLikes.text = post.likes.size.toString()
        binding.textoFechaHora.text = post.date.format(
            DateTimeFormatter.ofPattern("HH:mm • dd/MM/yyyy"))
        binding.username.setOnClickListener { onUserSelected(post.author) }
        binding.userImage.setOnClickListener { onUserSelected(post.author) }
        val found = post.likes.map{it.author }.any{it == username}

        if (found){
            binding.imgLikePost.setColorFilter(
                Color.parseColor("#FFAE1E36"),
                PorterDuff.Mode.SRC_ATOP
            )
        }
        else{
            binding.imgLikePost.setColorFilter(
                Color.parseColor("#7E7D7D"),
                PorterDuff.Mode.SRC_ATOP
            )
        }
        binding.imgLikePost.setOnClickListener{onLikeSelected(binding,post)}}
        else{
            binding.textNumLikes.visibility = View.INVISIBLE
            binding.textoFechaHora.visibility = View.INVISIBLE
            binding.imgLikePost.visibility = View.INVISIBLE
        }
        binding.userImage.setImageResource(resources.getIdentifier("pfp_${post.pfp}", "drawable", packageName))


        if(username == post.author){
            binding.imgDelete.setOnClickListener { onDeleteSelected(post)}
            binding.imgDelete.visibility = View.VISIBLE
        }
        else{
            binding.imgDelete.visibility = View.INVISIBLE
        }


        binding.floatingActionButtonPost.setOnClickListener{ onFABSelected(post.matchId,post.id)}



        thread {
            postList= PostDAO.getReplyList(post.id)
            runOnUiThread {

                initRecyclerView()
            }
        }
    }
    private fun initRecyclerView(){
        recyclerView = binding.recyclerComments
        val fab = binding.floatingActionButtonPost
        recyclerView.layoutManager = LinearLayoutManager(this)

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy<0 && !fab.isShown())
                    fab.show()
                else if(dy>0 && fab.isShown())
                    fab.hide()
            }
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
            }
        })
        recyclerView.addItemDecoration(
            DividerItemDecoration(
                this,
                (recyclerView.layoutManager as LinearLayoutManager).orientation
            )
        )

        initRecyclerAdapter()
    }

    fun initRecyclerAdapter(){
        recyclerView.adapter = PostAdapter(postList , {post -> onItemSelected(post)}, {string -> onUserSelected(string)},{itemBindingPost, post -> onLikeSelected(itemBindingPost, post)} )
    }

    fun onFABSelected(matchId : Int , parentId : Int){
        val intent : Intent = Intent(this, MakePostActivity::class.java)
        intent.putExtra("MATCHID", matchId)
        intent.putExtra("PARENTID", parentId)
        startActivity(intent)

    }
    fun onItemSelected(selPost : Post){
        val intent  = Intent(this, CommentActivity::class.java)
        intent.putExtra("POST", selPost as Serializable)
        startActivity(intent)
    }
    fun onUserSelected(username : String){
        val intent : Intent = Intent(this, ProfileAccessActivity::class.java)
        intent.putExtra("USERNAME", username)
        startActivity(intent)
    }

    fun onLikeSelected(itemBinding : ItemPostBinding, selPost : Post){
        val sharedpreferences = getSharedPreferences("shared_prefs", AppCompatActivity.MODE_PRIVATE)
        val username = sharedpreferences?.getString("user_key", null)
        thread {
            val newPost = PostDAO.getPost(selPost.id)
            val found = newPost!!.likes.map{it.author }.any{it == username}

            if (found) {

                LikeDAO.deleteLike(selPost.id, username!!)
                val newVal = (itemBinding.textNumLikes.text.toString().toInt() + -1).toString()
                runOnUiThread {
                    itemBinding.imgLike.setColorFilter(
                        Color.parseColor("#7E7D7D"),
                        PorterDuff.Mode.SRC_ATOP
                    )
                    var anim =
                        TranslateAnimation(
                            0.0F,
                            0.0F,
                            itemBinding.textNumLikes.width.toFloat(),
                            0.0F
                        )
                    anim.duration = 300
                    anim.fillAfter = true

                    anim.setAnimationListener(object : Animation.AnimationListener {
                        override fun onAnimationStart(animation: Animation?) {
                            itemBinding.textNumLikes.text = newVal
                            itemBinding.textNumLikes.animate().alpha(1f).duration = 500
                        }

                        override fun onAnimationEnd(animation: Animation?) {
                            //itemBinding.textNumLikes.text = newVal

                            //itemBinding.textNumLikes.animate().alpha(1f).duration = 500
                        }

                        override fun onAnimationRepeat(animation: Animation?) {

                        }
                    })

                    itemBinding.textNumLikes.animate().alpha(0f).duration = 500
                    itemBinding.textNumLikes.startAnimation(anim)
                }


            } else {
                val tmpLike = Like(username!!, selPost.id, LocalDateTime.now())
                LikeDAO.addLike(tmpLike)
                val newVal = (itemBinding.textNumLikes.text.toString().toInt() + 1).toString()

                runOnUiThread {

                    itemBinding.imgLike.setColorFilter(
                        Color.parseColor("#FFAE1E36"),
                        PorterDuff.Mode.SRC_ATOP
                    )
                    var anim =
                        TranslateAnimation(
                            0.0F,
                            0.0F,
                            itemBinding.textNumLikes.width.toFloat(),
                            0.0F
                        )
                    anim.duration = 300
                    anim.fillAfter = true

                    anim.setAnimationListener(object : Animation.AnimationListener {
                        override fun onAnimationStart(animation: Animation?) {
                            itemBinding.textNumLikes.text = newVal
                            itemBinding.textNumLikes.animate().alpha(1f).duration = 500
                        }

                        override fun onAnimationEnd(animation: Animation?) {
                            //itemBinding.textNumLikes.text = newVal

                            //itemBinding.textNumLikes.animate().alpha(1f).duration = 500
                        }

                        override fun onAnimationRepeat(animation: Animation?) {

                        }
                    })

                    itemBinding.textNumLikes.animate().alpha(0f).duration = 500
                    itemBinding.textNumLikes.startAnimation(anim)



                }
            }
        }


    }

    fun onLikeSelected(commentBinding: ActivityCommentBinding,  selPost: Post){
        if(selPost.author != "deleted") {
            val sharedpreferences =
                getSharedPreferences("shared_prefs", AppCompatActivity.MODE_PRIVATE)
            val username = sharedpreferences?.getString("user_key", null)
            thread {
                val newPost = PostDAO.getPost(selPost.id)
                val found = newPost!!.likes.map { it.author }.any { it == username }

                if (found) {

                    LikeDAO.deleteLike(selPost.id, username!!)
                    val newVal =
                        (commentBinding.textNumLikes.text.toString().toInt() + -1).toString()
                    runOnUiThread {
                        commentBinding.imgLikePost.setColorFilter(
                            Color.parseColor("#7E7D7D"),
                            PorterDuff.Mode.SRC_ATOP
                        )
                        var anim =
                            TranslateAnimation(
                                0.0F,
                                0.0F,
                                commentBinding.textNumLikes.width.toFloat(),
                                0.0F
                            )
                        anim.duration = 300
                        anim.fillAfter = true

                        anim.setAnimationListener(object : Animation.AnimationListener {
                            override fun onAnimationStart(animation: Animation?) {
                                commentBinding.textNumLikes.text = newVal
                                commentBinding.textNumLikes.animate().alpha(1f).duration = 500
                            }

                            override fun onAnimationEnd(animation: Animation?) {
                                //itemBinding.textNumLikes.text = newVal

                                //itemBinding.textNumLikes.animate().alpha(1f).duration = 500
                            }

                            override fun onAnimationRepeat(animation: Animation?) {

                            }
                        })

                        commentBinding.textNumLikes.animate().alpha(0f).duration = 500
                        commentBinding.textNumLikes.startAnimation(anim)
                    }


                } else {
                    val tmpLike = Like(username!!, selPost.id, LocalDateTime.now())
                    LikeDAO.addLike(tmpLike)
                    val newVal =
                        (commentBinding.textNumLikes.text.toString().toInt() + 1).toString()

                    runOnUiThread {

                        commentBinding.imgLikePost.setColorFilter(
                            Color.parseColor("#FFAE1E36"),
                            PorterDuff.Mode.SRC_ATOP
                        )
                        var anim =
                            TranslateAnimation(
                                0.0F,
                                0.0F,
                                commentBinding.textNumLikes.width.toFloat(),
                                0.0F
                            )
                        anim.duration = 300
                        anim.fillAfter = true

                        anim.setAnimationListener(object : Animation.AnimationListener {
                            override fun onAnimationStart(animation: Animation?) {
                                commentBinding.textNumLikes.text = newVal
                                commentBinding.textNumLikes.animate().alpha(1f).duration = 500
                            }

                            override fun onAnimationEnd(animation: Animation?) {
                                //itemBinding.textNumLikes.text = newVal

                                //itemBinding.textNumLikes.animate().alpha(1f).duration = 500
                            }

                            override fun onAnimationRepeat(animation: Animation?) {

                            }
                        })

                        commentBinding.textNumLikes.animate().alpha(0f).duration = 500
                        commentBinding.textNumLikes.startAnimation(anim)


                    }
                }
            }
        }

    }

    fun onDeleteSelected(post :Post){
        val alertDialog: AlertDialog? = this.let {
            val builder = AlertDialog.Builder(it)
            builder.setMessage("¿Estas seguro de que quieres eliminar el post?")
            builder.setTitle("Alerta")
            builder.apply {
                setPositiveButton("CONFIRMAR",
                    DialogInterface.OnClickListener { dialog, id ->
                        thread {
                            LikeDAO.deleteAllLikes(post.id)
                            PostDAO.deletePost(post)
                            runOnUiThread {
                                val toast = Toast.makeText(context, "Post eliminado", Toast.LENGTH_SHORT).show()
                                finish()
                            }
                        }

                    })
                setNegativeButton("CANCELAR",
                    DialogInterface.OnClickListener { dialog, id ->
                        dialog.cancel()
                    })
            }.show()
            // Create the AlertDialog
            builder.create()
        }

    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home){
            onBackPressed()
            return true
        }
        return false
    }

    override fun onRestart() {
        thread {
            val post = intent.extras?.get("POST") as Post
            postList = PostDAO.getReplyList(post.id)
            runOnUiThread {
                recyclerView.adapter = null
                initRecyclerAdapter()

            }
        }
        super.onRestart()

    }

}