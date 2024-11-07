package com.chant.android.ui.tienda

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.chant.android.R
import com.chant.android.databinding.FragmentPerfilBinding
import com.chant.android.databinding.FragmentTiendaBinding
import com.chant.android.model.dao.UserDAO
import com.chant.android.model.entities.User
import com.chant.android.ui.MainActivity
import com.chant.android.ui.perfil.PerfilFragment
import kotlin.concurrent.thread

class TiendaFragment : Fragment() {

    private var _binding: FragmentTiendaBinding? = null
    private val CANT_OFERTA_1 = 1
    private val CANT_OFERTA_2 = 5
    private val CANT_OFERTA_3 = 10
    private val PRECIO_OFERTA_1 = 100
    private val PRECIO_OFERTA_2 = 450
    private val PRECIO_OFERTA_3 = 850
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private var userName : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(arguments != null) {
            arguments?.let {
                userName = it.getString(PerfilFragment.USERNAME_BUNDLE)
            }
        }else{
            val pref : SharedPreferences? = activity?.getSharedPreferences("shared_prefs",
                Context.MODE_PRIVATE
            )
            userName = pref?.getString("user_key",null)
        }
        if(userName == null){
            val intent = Intent(activity, MainActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTiendaBinding.inflate(inflater, container, false)
        val view: View = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.cantOferta1.text = "$CANT_OFERTA_1 SOBRE"
        binding.precioOferta1.text = "$PRECIO_OFERTA_1 MONEDAS"
        binding.cantOferta2.text = "$CANT_OFERTA_2 SOBRES"
        binding.precioOferta2.text = "$PRECIO_OFERTA_2 MONEDAS"
        binding.cantOferta3.text = "$CANT_OFERTA_3 SOBRES"
        binding.precioOferta3.text = "$PRECIO_OFERTA_3 MONEDAS"

        binding.oferta1.setOnClickListener {
            showConfirmationDialog(PRECIO_OFERTA_1, CANT_OFERTA_1)
        }

        binding.oferta2.setOnClickListener {
            showConfirmationDialog(PRECIO_OFERTA_2, CANT_OFERTA_2)
        }

        binding.oferta3.setOnClickListener {
            showConfirmationDialog(PRECIO_OFERTA_3, CANT_OFERTA_3)
        }
        thread{
            val user : User? = userName?.let { UserDAO.getUser(it) }

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showConfirmationDialog(precio : Int, cantidad : Int) {
        val fragmentManager = requireActivity().supportFragmentManager
        val transaction = fragmentManager.beginTransaction()

        val confirmPurchaseFragment = TiendaConfirmarCompraFragment()
        val bundle = Bundle()
        bundle.putInt("precio", precio)
        bundle.putInt("cantidad", cantidad)
        confirmPurchaseFragment.arguments = bundle

        // Set a tag for the fragment, so we can find it later
        transaction.add(confirmPurchaseFragment, "confirmPurchaseFragment")
        transaction.addToBackStack(null)
        transaction.commit()
    }
}