package com.bumi.app.data.remote

import com.bumi.app.data.response.LoginRequest
import com.bumi.app.data.response.LoginResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

class AuthApiService(private val client: HttpClient) {

    suspend fun login(request: LoginRequest): LoginResponse {
        return client.post("login") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }
}
