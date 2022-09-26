package com.centrogeo.aplicadorEncuestas.utilidades

object Const {
    const val tabla_Respuestas = "Respuestas"
    const val tabla_RespuestasPendientes = "RESPUESTAS_PENDIENTES"
    const val tabla_preguntas = "ENCUESTA_PREG"
    const val tabla_IdEncuestas = "ENCUESTAS"
    const val id_encuesta ="ID_ENCUESTA"
    const val id_encuesta_abreviado = "ID_ENC"
    const val titulo = "TITLE"
    const val latitud = "LATITUD"
    const val longitud = "LONGITUD"
    const val correo = "CORREO"

    const val respuestas =
        "CREATE TABLE ${tabla_Respuestas}(ID_RESP INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, RESPUESTA TEXT NOT NULL,PREGUNTA TEXT NOT NULL," +
                " $id_encuesta_abreviado TEXT NOT NULL, ID_PREG TEXT NOT NULL)"

    const val encuestasT =
        "CREATE TABLE ${tabla_IdEncuestas}(${id_encuesta} TEXT NOT NULL PRIMARY KEY, $titulo TEXT NOT NULL , $latitud TEXT NOT NULL,$longitud TEXT NOT NULL)"

    const val encuestasP =
        "CREATE TABLE ${tabla_preguntas}(PREGUNTA TEXT NOT NULL, OPCIONES TEXT ,TIPO TEXT NOT NULL,${id_encuesta_abreviado} TEXT NOT NULL,ID_PREG TEXT NOT NULL)"


    const val respuestasID =
        "CREATE TABLE ${tabla_RespuestasPendientes}(${id_encuesta} TEXT NOT NULL PRIMARY KEY,${latitud} TEXT, ${longitud} TEXT, ${correo} TEXT)"


}