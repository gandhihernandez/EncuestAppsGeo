package com.centrogeo.aplicadorEncuestas.model.Answers

data class AnswersDataModel(
    var Location: List<Location>,
    var Respuestas: List<Respuesta>,
    var _id: Id,
    var nombre_encuesta: String,
    var surveyId: String
)