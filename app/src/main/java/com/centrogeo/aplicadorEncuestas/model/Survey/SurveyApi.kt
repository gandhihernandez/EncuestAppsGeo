package com.centrogeo.aplicadorEncuestas.model.Survey

import com.google.gson.annotations.SerializedName

data class SurveyApi(
    @SerializedName("_id") var _id: Id,
    @SerializedName("codigo") var codigo: String,
    @SerializedName("nombre") var nombre: String,
    @SerializedName("preguntas") var preguntas: List<Pregunta>,
    @SerializedName("userId") var userId: String
)