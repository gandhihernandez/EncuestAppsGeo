package com.centrogeo.aplicadorEncuestas.ui.gallery

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.centrogeo.aplicadorEncuestas.adapters.AdapterInsert
import com.centrogeo.aplicadorEncuestas.databinding.FragmentSurveysBinding
import com.centrogeo.aplicadorEncuestas.utilidades.Conexion_db_enc
import com.centrogeo.aplicadorEncuestas.utilidades.Const
import com.example.AplicadorEncuestasCG.model.EncuestaI
import java.lang.Exception

class SurveysFragment : Fragment() {

    private lateinit var surveyViewModel: SurveysViewModel
    private var _binding: FragmentSurveysBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter:AdapterInsert
    private lateinit var encuest:ArrayList<EncuestaI>


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
         surveyViewModel = ViewModelProvider(this).get(SurveysViewModel::class.java)
        _binding = FragmentSurveysBinding.inflate(inflater,container,false)

        initReciclerView()
        return binding.root
    }

    override fun onResume() {

        encuest = Buildlist()
        adapter= AdapterInsert(encuest)
        this.binding.ReciclerEncuesta.adapter = adapter
        super.onResume()

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }

    private fun initReciclerView() {
        this.binding.ReciclerEncuesta.layoutManager = LinearLayoutManager(activity)
        encuest= Buildlist()
        adapter = AdapterInsert(encuest)
        this.binding.ReciclerEncuesta.adapter = adapter
    }

    private fun Buildlist(): ArrayList<EncuestaI> {
        val encuest= ArrayList<EncuestaI>()
        val conn = Conexion_db_enc(activity, "db_usuarios", null, 1)
        val db: SQLiteDatabase = conn.readableDatabase
        val arr = arrayOf("")
        try {
           val cursor: Cursor = db.rawQuery("select * from ${Const.tabla_IdEncuestas} where ${Const.id_encuesta}!=?", arr)

            if ( cursor.count > 0) {
                cursor.moveToFirst()
                for (i in 1..cursor.count) {
                    encuest.add(EncuestaI(cursor.getString(0), cursor.getString(1)))
                    if (cursor.position != cursor.count) {
                        cursor.moveToNext()
                    }
                }
            }
            cursor.close()
        } catch (e: Exception) {
        }
       return encuest
    }
}