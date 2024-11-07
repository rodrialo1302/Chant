package com.chant.android.ui.home

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.chant.android.R
import com.chant.android.databinding.ActivityPostBinding
import com.chant.android.databinding.ItemPostBinding
import com.chant.android.model.dao.BetDAO
import com.chant.android.model.dao.LikeDAO
import com.chant.android.model.dao.PostDAO
import com.chant.android.model.dao.UserDAO
import com.chant.android.model.entities.Bet
import com.chant.android.model.entities.Like
import com.chant.android.model.entities.Match
import com.chant.android.model.entities.Post
import com.chant.android.ui.perfil.ProfileAccessActivity
import java.io.Serializable
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.concurrent.thread


class PostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPostBinding
    private var postList : ArrayList<Post>? = null
    private lateinit var recyclerView: RecyclerView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        recyclerView = binding.recyclerPosts
        val partido = intent.extras?.get("PARTIDO") as Match

        val swipeContainer = findViewById<SwipeRefreshLayout>(R.id.swiperefresh)
        swipeContainer.setOnRefreshListener {
            thread {


                postList = PostDAO.getPostList(partido.id)
                runOnUiThread {
                    recyclerView.adapter = null
                    initRecyclerAdapter()

                    swipeContainer.isRefreshing = false
                    // aqui lo que hay que hacer cuando le das
                }
            }
        }

        binding.teamAName.text = partido.team1.name
        binding.teamBName.text = partido.team2.name
        binding.score1.text = partido.goals1.toString()
        binding.score2.text = partido.goals2.toString()
        binding.separator.text = "-"
        //binding.timePassed.text = "..." // cambiar



        when (partido.status){
            0 -> {binding.timePassed.text = partido.startDate.format(DateTimeFormatter.ofPattern("HH:mm • dd/MM/yyyy"))
            }
            1 -> {
                binding.timePassed.text = "\uD83D\uDD34LIVE: Primer Tiempo"
                grayBets()
            }
            2 -> {
                binding.timePassed.text = "\uD83D\uDD34LIVE: Descanso"
                grayBets()
            }
            3 -> {binding.timePassed.text = "\uD83D\uDD34LIVE: Segundo Tiempo"
                grayBets()
            }
            4 -> {
                binding.timePassed.text =
                    partido.startDate.format(DateTimeFormatter.ofPattern("HH:mm • dd/MM/yyyy"))
                grayBets()
            }
            else -> {
                binding.timePassed.text = ""
            }
        }

        binding.betTeam1.text = partido.quota1.toString()
        binding.betEmpate.text = partido.quotaTie.toString()
        binding.betTeam2.text = partido.quota2.toString()



        binding.teamALogo.setImageResource(resources.getIdentifier("tm_${partido.team1.id}", "drawable", packageName))
        binding.teamBLogo.setImageResource(resources.getIdentifier("tm_${partido.team2.id}", "drawable", packageName))
        binding.floatingActionButtonMatch.setOnClickListener { onFABSelected(partido.id, -1) }


        thread {
            postList = PostDAO.getPostList(partido.id)
            var betList = BetDAO.getBetList(partido.id)

            val username = getSharedPreferences("shared_prefs", AppCompatActivity.MODE_PRIVATE).getString("user_key", null)

            val bet = betList.firstOrNull{it.author == username && it.postId == partido.id}

            runOnUiThread {
                if (partido.status == 0)
                    renderBets(bet, partido)

                initRecyclerView()
            }
        }



    }


    private fun initRecyclerView(){
        val fab = binding.floatingActionButtonMatch
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




    private fun initRecyclerAdapter(){
        recyclerView.adapter = PostAdapter(postList , {post -> onItemSelected(post)}, {string -> onUserSelected(string)},{itemBindingPost, post -> onLikeSelected(itemBindingPost, post)} )

    }


    private fun showBetDialog(team : Int, partido : Match){


        val username = getSharedPreferences("shared_prefs", AppCompatActivity.MODE_PRIVATE).getString("user_key", null)


        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.item_bet, null)
        val editText = dialogLayout.findViewById<EditText>(R.id.betDialogText)

        with(builder){
            when(team){
                1 -> setTitle("Apostar al " + partido.team1.name+":")
                2 -> setTitle("Apostar al Empate:")
                3 -> setTitle("Apostar al " + partido.team2.name+":")
                else ->{
                    setTitle("")
                }
            }

            setPositiveButton("Confirmar"){ dialog, which ->

                if (!TextUtils.isEmpty(editText.text.toString()) && editText.text.toString().toInt() > 0){
                    thread {
                        val currentUser = UserDAO.getUser(username!!)
                        if ( currentUser!!.coins >= editText.text.toString().toInt()){
                            BetDAO.addBet(currentUser, partido, team, editText.text.toString().toInt())
                            runOnUiThread {
                                renderBets(Bet(currentUser.username, partido.id,editText.text.toString().toInt(), team), partido)
                                Toast.makeText(
                                    this@PostActivity,
                                    "Apuesta realizada correctamente",
                                    Toast.LENGTH_SHORT
                                ).show()

                            }
                            }

                        else{
                            runOnUiThread {
                                Toast.makeText(
                                    this@PostActivity,
                                    "Monedas insuficientes",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }


                }
                else{
                    Toast.makeText(
                        this@PostActivity,
                        "Introduzca un número válido de monedas",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }

            setNegativeButton("Cancelar") {dialog, which ->
                Log.d("BetDialog", "Apuesta cancelada")
            }
            setView(dialogLayout)
            show()




        }



    }

    private fun showCancelDialog(bet : Bet, partido: Match){
        val builder = AlertDialog.Builder(this)
        with(builder){
            setTitle("¿Desea cancelar la apuesta?")
            setPositiveButton("Confirmar"){dialog, which ->
                thread{BetDAO.deleteBet(bet)
                val newBet : Bet? = null
                runOnUiThread { renderBets(newBet, partido) }
                }
            }
            setNegativeButton("Cancelar") {dialog, which ->
                Log.d("BetDialog", "Apuesta cancelada")
            }
            show()
        }




    }


    private fun renderBets(bet : Bet?, partido : Match){
        //val bet = BetDAO.getBetList(partido.id).firstOrNull{it.author == bet?.author && it.postId == partido.id}
        if (bet==null){
            binding.betTeam1.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FFAE1E36"))
            binding.betTeam1.setOnClickListener{
                showBetDialog(1, partido)
            }
            binding.betEmpate.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FFAE1E36"))
            binding.betEmpate.setOnClickListener{
                showBetDialog(2, partido)
            }
            binding.betTeam2.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FFAE1E36"))
            binding.betTeam2.setOnClickListener{
                showBetDialog(3, partido)
            }


        }
        else{
            when(bet.team){
                1 -> {
                    binding.betTeam1.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#3bb143"))
                    binding.betTeam1.setOnClickListener{
                        showCancelDialog(bet, partido)
                    }
                    binding.betEmpate.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#7E7D7D"))
                    binding.betEmpate.setOnClickListener{
                    }
                    binding.betTeam2.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#7E7D7D"))
                    binding.betTeam2.setOnClickListener{

                    }
                }
                2 -> {
                    binding.betEmpate.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#3bb143"))
                    binding.betEmpate.setOnClickListener{
                        showCancelDialog(bet, partido)
                    }
                    binding.betTeam1.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#7E7D7D"))
                    binding.betTeam1.setOnClickListener{
                    }
                    binding.betTeam2.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#7E7D7D"))
                    binding.betTeam2.setOnClickListener{

                    }
                }
                3 -> {
                    binding.betTeam2.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#3bb143"))
                    binding.betTeam2.setOnClickListener{
                        showCancelDialog(bet, partido)
                    }
                    binding.betTeam1.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#7E7D7D"))
                    binding.betTeam1.setOnClickListener{
                    }
                    binding.betEmpate.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#7E7D7D"))
                    binding.betEmpate.setOnClickListener{

                    }
                }

            }

        }


    }
    private fun grayBets(){
        binding.betTeam2.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#7E7D7D"))
        binding.betTeam2.setOnClickListener{
        }
        binding.betTeam1.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#7E7D7D"))
        binding.betTeam1.setOnClickListener{
        }
        binding.betEmpate.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#7E7D7D"))
        binding.betEmpate.setOnClickListener{

        }
    }


    fun onFABSelected(matchId : Int , parentId : Int){
        val intent : Intent = Intent(this, MakePostActivity::class.java)
        intent.putExtra("MATCHID", matchId)
        intent.putExtra("PARENTID", parentId)
        startActivity(intent)

    }
    fun onItemSelected(post: Post){
        val intent : Intent = Intent(this, CommentActivity::class.java)
        intent.putExtra("POST", post as Serializable)
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

    override fun onRestart() {
        thread {
            val partido = intent.extras?.get("PARTIDO") as Match
            postList = PostDAO.getPostList(partido.id)
            runOnUiThread {
                recyclerView.adapter = null
                initRecyclerAdapter()

            }
        }
        super.onRestart()
    }

    override fun onResume() {
        onRestart()
        super.onResume()
    }


}