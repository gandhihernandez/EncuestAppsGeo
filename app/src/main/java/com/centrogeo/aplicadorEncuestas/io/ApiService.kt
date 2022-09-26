package com.centrogeo.aplicadorEncuestas.io

import com.centrogeo.aplicadorEncuestas.io.response.AnswerResponse
import com.centrogeo.aplicadorEncuestas.io.response.RegisterResponse
import com.centrogeo.aplicadorEncuestas.io.response.SurveyResponse
import com.centrogeo.aplicadorEncuestas.model.Survey.SurveyApi
import com.centrogeo.aplicadorEncuestas.model.user.UserAuth
import com.centrogeo.aplicadorEncuestas.model.user.UserPost
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @GET("surveyC/{id}")
    @Headers("Content-Type: application/json" )
    fun getSurvey(@Header("Authorization") Authorization:String, @Path("id") id:String): Call<SurveyApi>

    @Headers("Content-Type: application/json" )
    @POST("login")
    fun login(@Body data:UserPost): Call<UserAuth>

    @Headers("Content-Type: application/json" )
    @POST("anwers")
    fun send(@Header("Authorization") Authorization:String, @Body data:AnswerResponse): Call<AnswerResponse>

    @Headers("Content-Type: application/json" )
    @POST("users")
    fun register( @Body data:RegisterResponse): Call<RegisterResponse>

}