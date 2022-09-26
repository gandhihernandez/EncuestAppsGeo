package com.centrogeo.aplicadorEncuestas.model.respuestas

data class Answers(
    var Answers: List<Answer>,
    var Email: String="",
    var Location: Location,
    var Survey: String=""
)