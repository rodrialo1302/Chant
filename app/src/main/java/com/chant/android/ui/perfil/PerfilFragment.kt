package com.chant.android.ui.perfil


import android.app.Activity
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chant.android.R
import com.chant.android.databinding.FragmentPerfilBinding
import com.chant.android.databinding.ItemPostBinding
import com.chant.android.model.dao.LikeDAO
import com.chant.android.model.dao.PostDAO
import com.chant.android.model.dao.UserDAO
import com.chant.android.model.entities.Like
import com.chant.android.model.entities.Post
import com.chant.android.model.entities.User
import com.chant.android.ui.HomeActivity
import com.chant.android.ui.MainActivity
import com.chant.android.ui.home.CommentActivity
import com.chant.android.ui.home.PostAdapter
import java.io.Serializable
import java.time.LocalDateTime
import kotlin.concurrent.thread


class PerfilFragment() : Fragment() {

    private  var _binding : FragmentPerfilBinding? = null
    // This property is only valid between onCrea teView and
    // onDestroyView.
    private val binding get() = _binding!!
    private var userName : String? = null
    private var propio : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(arguments != null) {
            arguments?.let {
                userName = it.getString(USERNAME_BUNDLE)
            }
        }else{
            val pref : SharedPreferences? = activity?.getSharedPreferences("shared_prefs", MODE_PRIVATE)
            userName = pref?.getString("user_key",null)
            propio = true
        }
        if(userName == null){
            val intent = Intent(activity, MainActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        _binding = FragmentPerfilBinding.inflate(inflater, container, false)
        val view: View = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (activity is HomeActivity){
            (activity as HomeActivity?)?.onPerfilSelected()
        }

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerPerfil)
        recyclerView.layoutManager = LinearLayoutManager(context)

        thread {
            val user : User? = userName?.let { UserDAO.getUser(it) };
            var postList: ArrayList<Post>? = userName?.let { PostDAO.getPostList(it) }
            //postList?.reverse()
            activity?.runOnUiThread {

                binding.profilePhoto.setImageResource(resources.getIdentifier("pfp_${user?.pfp}", "drawable", activity?.packageName))
                binding.fullName.text = user?.fullname
                binding.biography.text = user?.bio
                val userTeamLogo = binding.userTeam.context.resources.getIdentifier("tm_${user?.team?.id}","drawable",binding.userTeam.context.packageName)
                binding.userTeam.setImageResource(userTeamLogo)
                binding.userTeamName.text = user?.team?.name


                if (propio){
                    binding.buttonsLayout.visibility = View.VISIBLE
                    binding.logoutButton.setOnClickListener{ onLogoutSelected()}
                    binding.editProfileButton.setOnClickListener{onEditSelected(userName)}
                }else{
                    binding.buttonsLayout.visibility = View.GONE
                }

                val decor  = DividerItemDecoration(
                    recyclerView.context,
                    (recyclerView.layoutManager as LinearLayoutManager).layoutDirection
                )
                recyclerView.addItemDecoration(decor)
                recyclerView.adapter = PostAdapter(postList , {post -> onItemSelected(post)}, {string -> onUserSelected(string)},{itemBindingPost, post -> onLikeSelected(itemBindingPost, post)} )
                recyclerView.addItemDecoration(
                    DividerItemDecoration(
                        context,
                        (recyclerView.layoutManager as LinearLayoutManager).orientation
                    )
                )
            }
        }

    }

    fun onItemSelected(profilePost : Post){
        val intent  = Intent(activity, CommentActivity::class.java)
        intent.putExtra("POST", profilePost as Serializable)
        startActivity(intent)
    }
    fun onUserSelected(username : String){

    }

    fun onEditSelected(username : String?){
        val intent  = Intent(activity, ModProfileActivity::class.java)
        intent.putExtra("USERNAME", username)
        startActivityForResult(intent, 10001)
    }

    fun onLogoutSelected(){
        val sharedpreferences = activity?.getSharedPreferences("shared_prefs", MODE_PRIVATE)

        val editor = sharedpreferences?.edit()

        editor?.putString("user_key", null)

        editor?.apply()

        val i = Intent(context, MainActivity::class.java)
        startActivity(i)
        activity?.finish()


    }

    fun onLikeSelected(itemBinding : ItemPostBinding, selPost : Post){
        val sharedpreferences = activity?.getSharedPreferences("shared_prefs", AppCompatActivity.MODE_PRIVATE)
        val username = sharedpreferences?.getString("user_key", null)
        thread {
            val newPost = PostDAO.getPost(selPost.id)
            val found = newPost!!.likes.map{it.author }.any{it == username}

            if (found) {

                LikeDAO.deleteLike(selPost.id, username!!)
                val newVal = (itemBinding.textNumLikes.text.toString().toInt() + -1).toString()
                activity?.runOnUiThread {
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

                activity?.runOnUiThread {

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

   /* private fun updateItems(){
        thread {
            val updatedUser = UserDAO.getUser(userName!!)
            activity?.runOnUiThread {
                binding.fullName.text = updatedUser?.fullname
                binding.biography.text = updatedUser?.bio
                val userTeamLogo = binding.userTeam.context.resources.getIdentifier(
                    "tm_${updatedUser?.team}",
                    "drawable",
                    binding.userTeam.context.packageName
                )
                binding.userTeam.setImageResource(userTeamLogo)
            }
        }
    }*/





    companion object{
        const val  USERNAME_BUNDLE = "username_bundle"

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if((requestCode ==10001 ) && (resultCode == Activity.RESULT_OK)){
            //updateItems()
            fragmentManager?.beginTransaction()?.detach(this)?.commit()
            fragmentManager?.executePendingTransactions()
            fragmentManager?.beginTransaction()?.attach(this)?.commit()
            var fragment = activity?.supportFragmentManager?.findFragmentById(R.id.fragmentContainerView)
            if (fragment is PerfilFragment) {
                var  fragTransaction =   activity?.supportFragmentManager?.beginTransaction()
                fragTransaction?.detach(fragment);
                fragTransaction?.attach(fragment);
                fragTransaction?.commit();
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onPause() {
        if (activity is HomeActivity){
            (activity as HomeActivity?)?.onPerfilUnselected()
        }
        super.onPause()
    }

    override fun onResume() {
        if (activity is HomeActivity){
            //updateItems()
            (activity as HomeActivity?)?.onPerfilSelected()
        }
        super.onResume()
    }


}