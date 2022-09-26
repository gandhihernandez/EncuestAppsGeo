package com.centrogeo.aplicadorEncuestas.model.encuesta

data class Pregunta(

    var respuestas: List<respuesta>,
    var tipoF: String,
    var tipoS: String,
    var pregunta: String
)