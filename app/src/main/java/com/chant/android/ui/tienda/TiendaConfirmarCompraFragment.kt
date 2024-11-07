package com.chant.android.ui.tienda

import android.content.Intent
import android.content.SharedPreferences
import android.widget.Toast
import kotlin.concurrent.thread
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.chant.android.R
import com.chant.android.databinding.FragmentAlbumBinding
import com.chant.android.databinding.FragmentTiendaConfirmarCompraBinding
import com.chant.android.model.dao.PlayerDAO
import com.chant.android.model.entities.Player
import com.chant.android.model.dao.PurchaseDAO
import com.chant.android.model.dao.PurchaseLineDAO
import com.chant.android.model.dao.UserDAO
import com.chant.android.model.entities.Purchase
import com.chant.android.model.entities.PurchaseLine
import com.chant.android.model.entities.User
import com.chant.android.ui.MainActivity
import com.chant.android.ui.album.EntradaAlbumActivity
import java.io.Serializable

class TiendaConfirmarCompraFragment : BottomSheetDialogFragment() {

    private var sharedpreferences: SharedPreferences? = null
    private var _binding: FragmentTiendaConfirmarCompraBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedpreferences = requireActivity().getSharedPreferences("shared_prefs", AppCompatActivity.MODE_PRIVATE)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTiendaConfirmarCompraBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // You can initialize views and set click listeners here
        // For example, you can set the text for PrecioCompra using arguments.
        val precioCompra = view.findViewById<TextView>(R.id.PrecioCompra)
        arguments?.getInt("precio")?.let {
            precioCompra.text = "$it monedas"
        }
        val cantidad = arguments?.getInt("cantidad")
        val precio = arguments?.getInt("precio")

        thread{
            val username = sharedpreferences?.getString("user_key", null)
            val usuario = UserDAO.getUser(username!!)
            binding.btnCancelarCompra.setOnClickListener{
                activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit()
            }
            activity?.runOnUiThread {
                binding.btnConfirmarCompra.setOnClickListener() {
                    compraJugador(cantidad!!, precio!!, usuario!!)
                }
            }
        }


    }

    fun compraJugador(cantidad : Int, precio : Int, user : User){


        val userCoins = user?.coins ?: 0
        //Restar las monedas al jugador
        if (userCoins < precio){
            val message = "No tienes suficientes monedas"
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            return
        }
        thread {
            UserDAO.updateCoins(user!!.username, -precio)

            //Crear el purchase y purchase line

            val purchaseId = PurchaseDAO.createPurchase(user!!.username, precio)
            var indice = 0
            var playerList = ArrayList<Player>()

                while (indice < cantidad) {
                    var tmpPlayer = PlayerDAO.getRandPlayer()
                    PurchaseLineDAO.createPurchaseLine(purchaseId, tmpPlayer!!.id)
                    playerList.add(tmpPlayer)
                    indice++
                }
                val intent: Intent = Intent(context, AbrirSobreActivity::class.java)
                intent.putExtra("JUGADORES_SOBRE", playerList as Serializable)
                startActivity(intent)

        }



        //Pasar a una nueva vista con Recycler view que enseñe a los jugadores obtenidos
    }
}