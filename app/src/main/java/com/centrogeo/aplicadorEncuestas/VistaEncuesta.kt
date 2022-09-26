package com.centrogeo.aplicadorEncuestas

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.centrogeo.aplicadorEncuestas.adapters.AdapterEncuestas
import com.centrogeo.aplicadorEncuestas.databinding.ActivityVistaEncuestaBinding
import com.centrogeo.aplicadorEncuestas.model.PregEncuesta
import com.centrogeo.aplicadorEncuestas.utilidades.Conexion_db_enc
import com.centrogeo.aplicadorEncuestas.utilidades.Const
import java.lang.Exception

class VistaEncuesta : AppCompatActivity() {
    private lateinit var binding :ActivityVistaEncuestaBinding
    private var longitud = ""
    private var latitud = ""


    @SuppressLint("Recycle")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityVistaEncuestaBinding.inflate(layoutInflater)
        setContentView(binding.root)


        title = "Encuesta"
        val bundle = intent.extras
        val idEnc = bundle?.getString("idEncuesta") ?: ""
        val prefs = getSharedPreferences(getString(R.string.sharedPrefences), Context.MODE_PRIVATE)
        val correo: String = prefs.getString("Email", "") ?: ""


        val encuest=ArrayList<PregEncuesta>()


                val conectionDB = Conexion_db_enc(applicationContext, "db_usuarios", null, 1)
                val dbReadable: SQLiteDatabase = conectionDB.readableDatabase
                val paramsConsulta = arrayOf(bundle?.getString("idEncuesta") ?: "")
                try {
                    val cursor: Cursor = dbReadable.rawQuery("select * from ${Const.tabla_preguntas} where ID_ENC==?", paramsConsulta)
                    if ( cursor.count > 0) {
                        cursor.moveToFirst()

                        for (i in 1..cursor.count) {
                            val preg = cursor.getString(0)
                            val option = cursor.getString(1)
                            val type = cursor.getString(2)
                            //   Toast.makeText(this,preg+option+tipe,Toast.LENGTH_SHORT).show()
                            encuest.add(PregEncuesta(preg, option, type))
                            if (cursor.position != cursor.count) {
                                cursor.moveToNext()
                            }
                        }
                        cursor.close()
                    }
                } catch (e: Exception) {
                }
        try {
            val cursorTitle:Cursor = dbReadable.rawQuery("Select * from ${Const.tabla_IdEncuestas} where ${Const.id_encuesta} == ?", paramsConsulta)
            if (cursorTitle.count >0){
                cursorTitle.moveToFirst()
                binding.textView4.text=cursorTitle.getString(1)
                latitud=cursorTitle.getString(2)
                longitud=cursorTitle.getString(3)
                cursorTitle.close()
                dbReadable.close()
                conectionDB.close()
            }
        }catch (e:Exception){

        }


                encuest.add(PregEncuesta("", "", "Submit"))
                binding.RecEncuesta.layoutManager = LinearLayoutManager(this)
                Log.i("ubicacion","$longitud y $latitud")
                binding.RecEncuesta.adapter = AdapterEncuestas(encuest,longitud, latitud,correo,idEnc)
                binding.RecEncuesta.setItemViewCacheSize(25)
    }



}