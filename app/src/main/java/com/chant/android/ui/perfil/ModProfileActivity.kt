package com.chant.android.ui.perfil

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.widget.ArrayAdapter
import com.chant.android.R
import com.chant.android.databinding.ActivityModProfileBinding
import com.chant.android.model.dao.TeamDAO
import com.chant.android.model.dao.UserDAO
import com.chant.android.model.entities.Team
import com.chant.android.model.entities.User
import com.chant.android.ui.home.CommentActivity
import java.io.Serializable
import kotlin.concurrent.thread

class ModProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityModProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityModProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        title = "Modificar Perfil"
        val username: String? = intent.extras?.getString("USERNAME")
        val spinner = binding.selectTeamLayout
        val fullNameEd = binding.fullName
        val bioEd = binding.editTextBiography
        val cancelBtn = binding.btnCancel
        val confirmBtn = binding.btnConfirm
        var newFullName: String?
        var newBio: String?
        var newTeam: Int
        var selectedTeam: Team
        var teamList : ArrayList<Team> = ArrayList()
        var user : User? = null



        binding.imageViewProfile.setOnClickListener {
            val intent : Intent = Intent(this, SelectImageActivity::class.java)
            startActivity(intent)
        }

        cancelBtn.setOnClickListener {
            finish()
        }
        thread {
             teamList = TeamDAO.getTeamList()
             user = username?.let { UserDAO.getUser(it) }
            runOnUiThread{
                val arrayAdapter: ArrayAdapter<Team> = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, teamList)
                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinner.adapter = arrayAdapter
                binding.fullName.text = SpannableStringBuilder(user?.fullname)
                binding.editTextBiography.text =SpannableStringBuilder(user?.bio)
                binding.imageViewProfile.setImageResource(resources.getIdentifier("pfp_${user?.pfp}", "drawable", packageName))
                spinner.setSelection(user?.team?.id!! -1)
            }
        }

        confirmBtn.setOnClickListener {
            if (TextUtils.isEmpty(fullNameEd.text.toString())) {
                newFullName = user?.fullname
            } else {
                newFullName = fullNameEd.text.toString()
            }
            if (TextUtils.isEmpty(bioEd.text.toString())) {
                newBio = user?.bio
            } else {
                newBio = bioEd.text.toString()
            }
            selectedTeam = spinner.selectedItem as Team
            newTeam = selectedTeam.id
            thread{
                UserDAO.updateUser(username!!, newFullName!!, newTeam, newBio!!)
            }
            setResult(Activity.RESULT_OK)
            finish()
        }
    }

    override fun onResume() {
        val sharedPreferences = getSharedPreferences("shared_prefs", AppCompatActivity.MODE_PRIVATE)
        val pfpChosen : Int? = sharedPreferences?.getInt("pfp_chosen", -1)
        if (pfpChosen != -1) {
            binding.imageViewProfile.setImageResource(
                resources.getIdentifier(
                    "pfp_${pfpChosen}",
                    "drawable",
                    packageName
                )
            )
            val editor = sharedPreferences?.edit()
            editor?.remove("pfpChosen")
            editor?.apply()
        }

        super.onResume()
    }
}


