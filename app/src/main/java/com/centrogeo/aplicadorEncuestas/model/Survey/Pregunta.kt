package com.centrogeo.aplicadorEncuestas.model.Survey

data class Pregunta(
    var opciones: List<Opciones>,
    var pregunta: String,
    var tipoF: String,
    var tipoS: String
)