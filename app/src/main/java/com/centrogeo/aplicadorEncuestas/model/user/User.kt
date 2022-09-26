package com.centrogeo.aplicadorEncuestas.model.user

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("email")var email: String,
    @SerializedName("id") var id: String,
    @SerializedName("rol") var rol: String,
    @SerializedName("name") var name:String,
    @SerializedName("username") var username: String
)