package com.chant.android.ui.perfil

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.add
import androidx.fragment.app.commit
import com.chant.android.R
import com.chant.android.ui.perfil.PerfilFragment.Companion.USERNAME_BUNDLE

class ProfileAccessActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_access)

        var username = intent.extras?.getString("USERNAME")
        title = username

        if(savedInstanceState == null){
            val bundle = bundleOf(USERNAME_BUNDLE to username)
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                add<PerfilFragment>(R.id.fragmentContainerView, args = bundle)
            }
        }

    }
}