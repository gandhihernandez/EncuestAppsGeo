package com.centrogeo.aplicadorEncuestas.utilidades;

public class constantes {

    //constantes tabla usuarios
    public static final String tabla_usuario="usuario";
    public static final String tabla_Respuestas="Respuestas";
    public static final String campo_id="id";
    public static final String campo_nombre="Nombre";
    public static final String campo_correoDB ="Correo";
    public static final String campo_contrasenaDB ="Contrasena";
    public static final String Res_id="ID_RESP";
    public static final String Res_Respuesta="RESPUESTA";
    public static final String Res_Correo="CORREO";
    public static final String Res_IdEnc="ID_ENC";
    public static final String Res_idPreg="ID_PREG";

    //public static final String Crear="CREATE TABLE "+tabla_usuario+" ("+campo_id+"INTEGER PRIMARY KEY AUTOINCREMENT, "+campo_nombre+" TEXT, "+campo_telefono+" TEXT ,PRYMARY KEY("+campo_id+");";
    public static final String Crear="CREATE TABLE ["+tabla_usuario+"] (\n" + "["+campo_id+"] INTEGER  NOT NULL PRIMARY KEY AUTOINCREMENT,\n"
            + "["+campo_nombre+"] TEXT UNIQUE NOT NULL,\n"
            + "["+ campo_correoDB +"] TEXT  NULL,\n"
            +"["+ campo_contrasenaDB +"] TEXT"  +")";

    public static final String respuestas="CREATE TABLE Respuestas(ID_RESP INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, RESPUESTA TEXT NOT NULL," +
            "CORREO TEXT NOT NULL, ID_ENC TEXT NOT NULL, ID_PREG TEXT NOT NULL,Longitud TEXT ,LATITUD TEXT)";

    public static final String encuestasT="CREATE TABLE ENCUESTA(ID_ENC TEXT NOT NULL PRIMARY KEY,TITLE TEXT NOT NULL)";

    public static final String encuestasP="CREATE TABLE ENCUESTA_PREG(PREGUNTA TEXT NOT NULL ,OPCIONES TEXT ,TIPO TEXT NOT NULL,ID_ENC TEXT NOT NULL,ID_PREG TEXT NOT NULL)";

    public static final String emcuestasId="CREATE TABLE ENCUESTAS_LIST(ID_ENCUESTA TEXT NOT NULL PRIMARY KEY, LATITUD TEXT, LONGITUD TEXT)";

    public static final String respuestasID="CREATE TABLE RESPUESTAS_PENDIENTES(ID_ENCUESTA TEXT NOT NULL PRIMARY KEY, TITLE TEXT)" ;

}
