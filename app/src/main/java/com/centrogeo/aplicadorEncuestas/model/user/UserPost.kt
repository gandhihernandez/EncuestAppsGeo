package com.centrogeo.aplicadorEncuestas.model.user

import com.google.gson.annotations.SerializedName

data class UserPost (
    @SerializedName("username") val username: String,
    @SerializedName("password") val password: String
)