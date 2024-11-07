package com.chant.android.ui

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.chant.android.R
import com.chant.android.model.dao.UserDAO
import com.chant.android.model.entities.Team
import com.chant.android.model.entities.User
import java.util.regex.Pattern
import kotlin.concurrent.thread

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val username = findViewById<EditText>(R.id.regUsername)
        val email = findViewById<EditText>(R.id.regEmail)
        val password = findViewById<EditText>(R.id.regPassword)
        val loginBtn = findViewById<Button>(R.id.idBtnLogin)

        loginBtn.setOnClickListener {

            if (TextUtils.isEmpty(username.text.toString()) || TextUtils.isEmpty(email.text.toString()) || TextUtils.isEmpty(
                    password.text.toString()
                )
            ) {
                // this method will call when email and password fields are empty.
                Toast.makeText(
                    this@RegisterActivity,
                    "Por favor, rellene todos los campos",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                if (!isValidEmail(email.text.toString())) {
                    Toast.makeText(
                        this@RegisterActivity,
                        "Introduzca un correo válido",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else if (!isValidUsername(username.text.toString())) {
                    Toast.makeText(
                        this@RegisterActivity,
                        "El nombre de usuario solo puede incluir letras, números, . y _",
                        Toast.LENGTH_SHORT
                    ).show()

                }
                else if (!isValidUsernameLength(username.text.toString())) {
                    Toast.makeText(
                        this@RegisterActivity,
                        "El nombre de usuario debe tener entre 4 y 15 caracteres",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else if (!isValidPassword(password.text.toString())) {
                    Toast.makeText(
                        this@RegisterActivity,
                        "La contraseña debe tener 8 caracteres o más",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else{
                    thread{
                        if(UserDAO.checkUser(username.text.toString())){
                            runOnUiThread{
                                Toast.makeText(
                                    this@RegisterActivity,
                                    "Nombre de usuario ocupado. Por favor, elija otro",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        else {
                            val newUser = User(
                                username.text.toString(),
                                username.text.toString(),
                                email.text.toString(),
                                password.text.toString(),
                                1000,
                                Team(1,""),
                                "",
                                1
                            )
                            UserDAO.addUser(newUser)
                            runOnUiThread{
                                Toast.makeText(
                                    this@RegisterActivity,
                                    "Usuario registrado correctamente",
                                    Toast.LENGTH_SHORT
                                ).show()
                                finish()

                            }
                        }

                    }
                }




            }
        }
    }

    private fun isValidEmail(target: CharSequence): Boolean {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }

    private fun isValidUsername(target: CharSequence): Boolean {
        return !TextUtils.isEmpty(target) && Pattern.compile("^[A-z0-9._]+$").matcher(target).matches()
    }
    private fun isValidUsernameLength(target: CharSequence): Boolean {
        return !TextUtils.isEmpty(target) && Pattern.compile("^.{4,15}$").matcher(target).matches()
    }

    private fun isValidPassword(target: CharSequence): Boolean {
        return !TextUtils.isEmpty(target) && Pattern.compile("^.{8,}$").matcher(target).matches()
    }



}