package com.centrogeo.aplicadorEncuestas.io.response

import com.centrogeo.aplicadorEncuestas.model.Survey.SurveyApi
import com.google.gson.annotations.SerializedName

data class SurveyResponse (
    @SerializedName("survey") var survey:SurveyApi
)