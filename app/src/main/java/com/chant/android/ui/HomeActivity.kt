package com.chant.android.ui

import android.app.ActionBar
import android.media.Image
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.chant.android.R
import com.chant.android.databinding.ActivityMainBinding
import com.chant.android.model.dao.UserDAO
import org.w3c.dom.Text
import kotlin.concurrent.thread

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mCoinTextView : TextView
    private lateinit var mChantLogo : ImageView
    private lateinit var mUserText : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //enableEdgeToEdge()
        window.navigationBarColor = resources.getColor(R.color.red_primary)


        val mActionBar  = supportActionBar
        mActionBar?.setDisplayShowHomeEnabled(false)
        mActionBar?.setDisplayShowTitleEnabled(false)
        val mInflater = LayoutInflater.from(this)

        val mCustomView: View = mInflater.inflate(R.layout.actionbar_home, null)
        mCoinTextView = mCustomView.findViewById<View>(R.id.coinText) as TextView
        mChantLogo = mCustomView.findViewById<View>(R.id.chantLogo) as ImageView
        mUserText = mCustomView.findViewById<View>(R.id.userText) as TextView
        mCoinTextView.text = "0"
        setCoins()
        mActionBar?.customView = mCustomView;
        mActionBar?.setDisplayShowCustomEnabled(true);


        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(setOf(
                R.id.navigation_tienda,
                R.id.navigation_album,
                R.id.navigation_home,
                R.id.navigation_notifications,
                R.id.navigation_perfil
            )
        )

            setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    private fun setCoins(){
        thread {
            val username = getSharedPreferences(
                "shared_prefs",
                AppCompatActivity.MODE_PRIVATE
            )?.getString("user_key", null)
            val user = UserDAO.getUser(username!!)
            runOnUiThread {
                mCoinTextView.text = user?.coins.toString()
            }
        }
    }

    fun onPerfilSelected(){
        mChantLogo.visibility = View.INVISIBLE
        mUserText.text = getSharedPreferences(
            "shared_prefs",
            AppCompatActivity.MODE_PRIVATE
        )?.getString("user_key", null)

    }

    fun onPerfilUnselected(){
        mChantLogo.visibility = View.VISIBLE
        mUserText.text = ""
    }

    override fun onResume() {
        setCoins()
        super.onResume()
    }
}