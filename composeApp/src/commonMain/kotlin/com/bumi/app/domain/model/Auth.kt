package com.bumi.app.domain.model

data class AuthUser(
    val id: String,
    val username: String,
    val nama: String,
    val role: String
)

data class LoginInput(
    val email: String,
    val pass: String
)