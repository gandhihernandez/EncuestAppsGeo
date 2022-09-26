package com.centrogeo.aplicadorEncuestas.ui.slideshow

import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.centrogeo.aplicadorEncuestas.MainActivity
import com.centrogeo.aplicadorEncuestas.R
import com.centrogeo.aplicadorEncuestas.databinding.FragmentPorfileBinding
import com.centrogeo.aplicadorEncuestas.datastore.SharedPreferences
import com.centrogeo.aplicadorEncuestas.utilidades.Conexion_db_enc
import com.centrogeo.aplicadorEncuestas.utilidades.Const
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth



class   PorfileFragment : Fragment() {
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var porfileViewModel: PorfileViewModel
    private var _binding: FragmentPorfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var prefs: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        porfileViewModel = ViewModelProvider(this).get(PorfileViewModel::class.java)
        _binding=FragmentPorfileBinding.inflate(inflater, container, false)
        prefs = SharedPreferences(requireContext())
        binding.imageViewPorfile.setImageResource(R.drawable.blank)
        recoverDatos()


        binding.button2.setOnClickListener {
            cerrarSesion()
        }

        return binding.root
    }



    private fun cerrarSesion(){
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
           LoginManager.getInstance().logOut()
            val preferences = this.requireActivity()
                .getSharedPreferences("preferenciasAplicador", Context.MODE_PRIVATE).edit()
            preferences.clear()
            preferences.apply()

            signOutFirebase()
            getincio()

        val conn = Conexion_db_enc(requireContext(), "db_usuarios", null, 1)
        val db: SQLiteDatabase = conn.writableDatabase
        db.execSQL("DELETE FROM ${Const.tabla_IdEncuestas}")
        db.execSQL("DELETE FROM ${Const.tabla_preguntas}")
        db.close()
        conn.close()

    }

    private fun signOutFirebase() {
        FirebaseAuth.getInstance().signOut()
        mGoogleSignInClient.signOut()
            .addOnCompleteListener(requireActivity()
            ) { }
    }

    private fun getincio() {
        val intentInicio = Intent(activity, MainActivity::class.java)
        intentInicio.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intentInicio.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intentInicio.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intentInicio)

    }

    private fun recoverDatos(){
        val prefs = this.activity?.getSharedPreferences(getString(R.string.sharedPrefences),Context.MODE_PRIVATE)
        val correo: String = prefs?.getString("Email", "") ?: ""
        val name: String = prefs?.getString("Name","")?:""

        binding.correoPorfile.text = correo
        binding.namePorfile.text = name
    }

    }

