
package com.chant.android.ui.notifications
import android.content.Intent
import com.chant.android.model.entities.Match
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.chant.android.R
import com.chant.android.databinding.FragmentNotificationsBinding
import com.chant.android.model.dao.MatchDAO
import com.chant.android.model.dao.NotificationDAO
import com.chant.android.model.dao.PostDAO
import com.chant.android.model.entities.Notification
import com.chant.android.ui.home.CommentActivity
import com.chant.android.ui.home.PostActivity
import java.io.Serializable
import kotlin.concurrent.thread

class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return inflater.inflate(R.layout.fragment_notifications,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedpreferences = activity?.getSharedPreferences("shared_prefs", AppCompatActivity.MODE_PRIVATE)


        val username = sharedpreferences?.getString("user_key", null)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerNotifications)
        recyclerView.layoutManager = LinearLayoutManager(context)
        val swipeContainer = view.findViewById<SwipeRefreshLayout>(R.id.swiperefresh)
        swipeContainer.setOnRefreshListener {
            swipeContainer.isRefreshing = false
            // aqui lo que hay que hacer cuando le das
        }



        thread {

            var notiList: ArrayList<Notification> = NotificationDAO.getNotifications(username!!)

            activity?.runOnUiThread {
                val decor: DividerItemDecoration = DividerItemDecoration(
                    recyclerView.context,
                    (recyclerView.layoutManager as LinearLayoutManager).layoutDirection
                )
                recyclerView.addItemDecoration(decor)



                recyclerView.adapter = NotificationAdapter(notiList) { post ->
                    onItemSelected(
                        post
                    )
                }
                recyclerView.addItemDecoration(
                    DividerItemDecoration(
                        context,
                        (recyclerView.layoutManager as LinearLayoutManager).orientation
                    )
                )
            }
        }
    }



    fun onItemSelected(notification : Notification){
        thread {
            val tmpPost = PostDAO.getPost(notification.id)
            val intent: Intent = Intent(context, CommentActivity::class.java)
            intent.putExtra("POST", tmpPost as Serializable)
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}