package com.centrogeo.aplicadorEncuestas.utilidades

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.centrogeo.aplicadorEncuestas.MainActivity
import com.centrogeo.aplicadorEncuestas.model.encuesta.Survey
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson

class SurveysServices(mainActivity: MainActivity) {
    val context =mainActivity

    fun update(){
        try{
            val database = FirebaseFirestore.getInstance()
            val conexionDB = Conexion_db_enc(context,"db_usuarios",null,1)
            val dbWritable: SQLiteDatabase = conexionDB.writableDatabase
            val dbReadable: SQLiteDatabase = conexionDB.readableDatabase
            val gson= Gson()
            val param = arrayOf( "")
            val cursor: Cursor = dbReadable.rawQuery("SELECT ${Const.id_encuesta} FROM ${Const.tabla_IdEncuestas} WHERE ${Const.id_encuesta} != ?", param)
            cursor.moveToFirst()

            if (cursor.count!=0) {

                for (i in 0 until cursor.count) {
                    cursor.moveToPosition(i)
                    database.collection("Surveys").document(cursor.getString(0))
                        .get()
                        .addOnSuccessListener { document ->
                            val surveys:String=gson.toJson(document.data)
                            val survey: Survey =gson.fromJson(surveys, Survey::class.java)


                            dbWritable.execSQL("UPDATE ${Const.tabla_IdEncuestas} SET ${Const.titulo} = '${survey.nombre}' WHERE ${Const.id_encuesta} == '${document.id}'")
                            dbWritable.execSQL("DELETE FROM ${Const.tabla_preguntas} WHERE ${Const.id_encuesta_abreviado}='${document.id}'")
                            for (preguntas in survey.preguntas.indices) {
                                var opciones=""
                                for (respuestas in survey.preguntas[preguntas].respuestas.indices){
                                    opciones += "${survey.preguntas[preguntas].respuestas[respuestas].respuesta},"
                                }
                                if (opciones.endsWith(",", false)) {
                                    //    uno=uno.substring(0, uno.length - 1)
                                    opciones= opciones.removeRange(opciones.length-1,opciones.length)
                                }
                                dbWritable.execSQL("INSERT INTO ENCUESTA_PREG(PREGUNTA,OPCIONES,TIPO,ID_ENC,ID_PREG) VALUES " +
                                        "('${survey.preguntas[preguntas].pregunta}','${opciones}','${survey.preguntas[preguntas].tipoS}','" +
                                        "${document.id}','${document.id}_${preguntas}')")
                            }

                        }
                        .addOnFailureListener {

                        }
                    dbReadable.close()
                    dbWritable.close()
                    conexionDB.close()
                    database.clearPersistence()

                }



            }
            cursor.close()
            Log.i("hola mundo Services","surveyServices")

        }catch (e:Exception){
            Log.i("prueba2", ""+e)
        }
    }

}