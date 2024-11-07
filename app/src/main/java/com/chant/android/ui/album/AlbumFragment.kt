package com.chant.android.ui.album

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.chant.android.databinding.FragmentAlbumBinding
import android.content.Intent
import android.widget.Toast
import com.chant.android.model.entities.Team
import com.chant.android.model.entities.Player
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.chant.android.R
import com.chant.android.model.dao.TeamDAO
import com.chant.android.model.dao.PlayerDAO
import java.io.Serializable
import kotlin.concurrent.thread

class AlbumFragment : Fragment() {

    private var _binding: FragmentAlbumBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_album,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val equipoRecyclerView = view.findViewById<RecyclerView>(R.id.recyclerAlbum)
        equipoRecyclerView.layoutManager = LinearLayoutManager(context)

        thread {
            var equipos : ArrayList<Team> = TeamDAO.getTeamList()
            activity?.runOnUiThread {
                val decor: DividerItemDecoration = DividerItemDecoration(
                    equipoRecyclerView.context,
                    (equipoRecyclerView.layoutManager as LinearLayoutManager).layoutDirection
                )
                equipoRecyclerView.addItemDecoration(decor)

                equipoRecyclerView.adapter = AlbumAdapter(equipos) { equipo ->
                    onItemSelected(
                        equipo
                    )
                }

                equipoRecyclerView.addItemDecoration(decor)
            }
        }
    }

    fun onItemSelected(equipo: Team){
        val intent : Intent = Intent(context, EntradaAlbumActivity::class.java)
        intent.putExtra("EQUIPO", equipo as Serializable)
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}