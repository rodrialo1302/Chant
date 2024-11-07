package com.chant.android.ui.perfil

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chant.android.R
import com.chant.android.model.dao.UserDAO
import kotlin.concurrent.thread

class SelectImageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_image)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerPfp)
        recyclerView.layoutManager = GridLayoutManager(this, 3)
                val pfpList = arrayListOf(1,2,3,4,5,6,7,8,1,10,11,12,13,14,15)
                recyclerView.adapter = SelectImageAdapter(pfpList) { image ->
                    onItemSelected(
                        image
                    )
                }

            }
    fun onItemSelected(id : Int){
        val sharedPreferences = getSharedPreferences("shared_prefs", AppCompatActivity.MODE_PRIVATE)
        val username = sharedPreferences?.getString("user_key", null)
        val editor = sharedPreferences?.edit()

        editor?.putInt("pfp_chosen", id)

        editor?.apply()
        thread {
            UserDAO.updatePfp(username!!, id)
        }
        finish()

    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home){
            onBackPressed()
            return true
        }
        return false
    }


}


