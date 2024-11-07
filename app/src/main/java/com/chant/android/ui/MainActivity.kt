package com.chant.android.ui

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.chant.android.R
import com.chant.android.model.dao.ConnectionPool
import com.chant.android.model.dao.UserDAO
import com.chant.android.ui.notifications.NotificationService
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {
    // variable for shared preferences.
    private var sharedpreferences: SharedPreferences? = null
    private var username: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        window.statusBarColor = Color.TRANSPARENT
        supportActionBar?.hide()

        // Initializing EditTexts and our Button
        val userEdt = findViewById<EditText>(R.id.idEdtEmail)
        val passwordEdt = findViewById<EditText>(R.id.idEdtPassword)
        val loginBtn = findViewById<Button>(R.id.idBtnLogin)
        val regBtn = findViewById<Button>(R.id.idBtnRegister)

        // getting the data which is stored in shared preferences.
        sharedpreferences = getSharedPreferences("shared_prefs", MODE_PRIVATE)

        // in shared prefs inside get string method
        // we are passing key value as EMAIL_KEY and
        // default value is
        // set to null if not present.
        username = this.sharedpreferences?.getString("user_key", null)

        regBtn.setOnClickListener{
            val i = Intent(this@MainActivity, RegisterActivity::class.java)
            startActivity(i)

        }

        // calling on click listener for login button.
        loginBtn.setOnClickListener {
            // to check if the user fields are empty or not.
            if (TextUtils.isEmpty(userEdt.text.toString()) && TextUtils.isEmpty(passwordEdt.text.toString())) {
                // this method will call when email and password fields are empty.
                Toast.makeText(
                    this@MainActivity,
                    "Please Enter Email and Password",
                    Toast.LENGTH_SHORT
                ).show()
            } else {

                thread {
                    if (UserDAO.isUser(userEdt.text.toString(), passwordEdt.text.toString())) {
                        val editor = sharedpreferences?.edit()

                        // below two lines will put values for
                        // email and password in shared preferences.
                        editor?.putString("user_key", userEdt.text.toString())

                        // to save our data with key and value.
                        editor?.apply()

                        // starting new activity.
                        val i = Intent(this@MainActivity, HomeActivity::class.java)
                        startActivity(i)
                        finish()
                    } else {
                        runOnUiThread {
                        Toast.makeText(
                            this@MainActivity,
                            "Usuario o contraseña incorrecto",
                            Toast.LENGTH_SHORT
                        ).show()}
                    }
                }
            }
        }
    }

    override fun onStart() {
        createNotificationChannel()
        startService(Intent(this, NotificationService::class.java))
        super.onStart()
        if (this.sharedpreferences?.getString("user_key", null) != null) {
            val i = Intent(this@MainActivity, HomeActivity::class.java)
            startActivity(i)
        }
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is not in the Support Library.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Notificaciones"
            val descriptionText = ""
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("INTERACTIONS", name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system.
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }


}