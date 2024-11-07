package com.chant.android.ui.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.chant.android.databinding.ActivityMakePostBinding
import com.chant.android.model.dao.MatchDAO
import com.chant.android.model.dao.PostDAO
import com.chant.android.model.dao.UserDAO
import com.chant.android.model.entities.Like
import com.chant.android.model.entities.Match
import com.chant.android.model.entities.Post
import com.chant.android.ui.HomeActivity
import java.io.Serializable
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Calendar
import java.util.Date
import kotlin.concurrent.thread

class MakePostActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMakePostBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMakePostBinding.inflate(layoutInflater)
        setContentView(binding.root)
        title = "Post"

        val postButton = binding.idBtnPost
        val postEditText = binding.idEdtPostText
        val pref = getSharedPreferences("shared_prefs", MODE_PRIVATE)
        val authorUsrName = pref.getString("user_key",null)
        val matchId : Int? = intent.extras?.getInt("MATCHID")
        val parentId : Int? = intent.extras?.getInt("PARENTID")
        val date : Date = Calendar.getInstance().time
        val localDateTime : LocalDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()

        postButton.setOnClickListener{
            if (TextUtils.isEmpty(postEditText.text.toString())) {
                // this method will call when email and password fields are empty.
                Toast.makeText(
                    this,
                    "Escribe una publicación",
                    Toast.LENGTH_SHORT
                ).show()
            }else{
                thread{
                    val user = UserDAO.getUser(authorUsrName!!)
                    val post : Post = Post(0,matchId!!,parentId!!, user!!.username,1,localDateTime,postEditText.text.toString(),ArrayList<Like>(), user.pfp)
                    PostDAO.addPost(post)
                    finish()
                }
            }
        }


    }
}