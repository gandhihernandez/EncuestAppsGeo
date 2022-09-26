package com.centrogeo.aplicadorEncuestas.model.user

import com.google.gson.annotations.SerializedName

data class UserAuth(
    @SerializedName("token") var token: String,
    @SerializedName("user") var user: User
)