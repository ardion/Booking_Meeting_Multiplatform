package com.bumi.app.data.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val username: String,
    val password: String
)

@Serializable
data class LoginResponse(
    // Ganti @SerializedName menjadi @SerialName
    @SerialName("status") val status: String,
    @SerialName("message") val message: String,
    @SerialName("data") val data: UserData?
)

@Serializable
data class UserData(
    @SerialName("id") val id: String,
    @SerialName("username") val username: String,
    @SerialName("name") val nama: String,
    @SerialName("role") val role: String
)
