package com.centrogeo.aplicadorEncuestas.utilidades

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class Conexion_db_enc(context: Context?, name: String?, factory: SQLiteDatabase.CursorFactory?, version: Int) : SQLiteOpenHelper(context, name, factory, version) {

    override fun onCreate(db: SQLiteDatabase) {
        db.run {execSQL(Const.encuestasT)}
        db.run {execSQL(Const.encuestasP)}
        db.run {execSQL(Const.respuestas)}
        db.run {execSQL(Const.respuestasID)}
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS ${Const.tabla_preguntas}")
        db.execSQL("DROP TABLE IF EXISTS ${Const.tabla_IdEncuestas}")
        db.execSQL("DROP TABLE IF EXISTS ${Const.tabla_Respuestas}")
        db.execSQL("DROP TABLE IF EXISTS ${Const.tabla_RespuestasPendientes}")
        onCreate(db)
    }

}