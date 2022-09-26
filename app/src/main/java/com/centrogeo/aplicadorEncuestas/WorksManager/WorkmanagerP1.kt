package com.centrogeo.aplicadorEncuestas.WorksManager

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.centrogeo.aplicadorEncuestas.model.encuesta.Survey
import com.centrogeo.aplicadorEncuestas.model.respuestas.Answer
import com.centrogeo.aplicadorEncuestas.model.respuestas.Answers

import com.centrogeo.aplicadorEncuestas.utilidades.Conexion_db_enc
import com.centrogeo.aplicadorEncuestas.utilidades.Const
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import java.lang.Exception


class WorkmanagerP1(context: Context, params: WorkerParameters) : Worker(context, params) {


    override fun doWork(): Result {
        try {
            val database = FirebaseFirestore.getInstance()
            val conexionReadeable = Conexion_db_enc(applicationContext,"db_usuarios",null,1)
            val dbRd:SQLiteDatabase = conexionReadeable.readableDatabase
            val gson=Gson()
            val param = arrayOf( "")
            lateinit var respuestaEnv: Answers
            lateinit var cursorPreguntas : Cursor

            val cursorEncP: Cursor = dbRd.rawQuery("SELECT * FROM ${Const.tabla_RespuestasPendientes} WHERE ${Const.id_encuesta} != ?", param)

            if (cursorEncP.count > 0){
                cursorEncP.moveToFirst()
                for (i in 1..cursorEncP.count) {
                    val param2= arrayOf(cursorEncP.getString(0))
                    cursorPreguntas = dbRd.rawQuery("SELECT * FROM ${Const.tabla_Respuestas} WHERE ${Const.id_encuesta_abreviado} == ?",param2)
                    respuestaEnv.Survey=cursorEncP.getString(0)
                    respuestaEnv.Location.longitude = cursorEncP.getString(2)
                    respuestaEnv.Location.latitude = cursorEncP.getString(1)
                    respuestaEnv.Email=cursorEncP.getString(3)
                    if(cursorPreguntas.count > 0)
                    {
                        val answer=ArrayList<Answer>()
                        cursorPreguntas.moveToFirst()
                        for(j in 1..cursorPreguntas.count){
                             answer.add(Answer(cursorPreguntas.getString(1),cursorPreguntas.getString(2)))
                             cursorPreguntas.moveToNext()
                        }

                        answer.clear()
                        respuestaEnv.Answers = answer
                    }
                    cursorEncP.moveToNext()
                    database.collection("Respuestas").add(respuestaEnv)
                }
            }

            cursorPreguntas.close()
            cursorEncP.close()
            database.clearPersistence()
            dbRd.close()
            conexionReadeable.close()

            return Result.success()
        } catch (e: Exception) {

            return Result.failure()
        }
    }

}