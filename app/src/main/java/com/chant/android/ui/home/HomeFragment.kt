
package com.chant.android.ui.home

import android.app.ActionBar
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.chant.android.R
import com.chant.android.databinding.FragmentHomeBinding
import com.chant.android.model.dao.MatchDAO
import com.chant.android.model.dao.PostDAO
import com.chant.android.model.dao.UserDAO
import com.chant.android.model.entities.Match
import java.io.Serializable
import kotlin.concurrent.thread


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        return inflater.inflate(R.layout.fragment_home,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerPartidos)
        recyclerView.layoutManager = LinearLayoutManager(context)
       val swipeContainer = view.findViewById<SwipeRefreshLayout>(R.id.swiperefresh)


        swipeContainer.setOnRefreshListener {
            thread {
                val matchList= MatchDAO.getMatchList()
                activity?.runOnUiThread {
                    recyclerView.adapter = null
                    initRecyclerAdapter(recyclerView, matchList)

                    swipeContainer.isRefreshing = false
                }
            }
        }





        thread {

            var matchList: ArrayList<Match> = MatchDAO.getMatchList()


            activity?.runOnUiThread {
                val decor: DividerItemDecoration = DividerItemDecoration(
                    recyclerView.context,
                    (recyclerView.layoutManager as LinearLayoutManager).layoutDirection
                )
                recyclerView.addItemDecoration(decor)

                recyclerView.addItemDecoration(
                    DividerItemDecoration(
                        context,
                        (recyclerView.layoutManager as LinearLayoutManager).orientation
                    )
                )

                initRecyclerAdapter(recyclerView, matchList)


            }
        }
    }

    fun initRecyclerAdapter(recyclerView: RecyclerView, matchList : ArrayList<Match>){
        recyclerView.adapter = PartidoAdapter(matchList) { partido ->
            onItemSelected(
                partido
            )
        }
    }

    fun onItemSelected(partido : Match){
        val intent  : Intent = Intent(context, PostActivity::class.java)
        intent.putExtra("PARTIDO", partido as Serializable)
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}