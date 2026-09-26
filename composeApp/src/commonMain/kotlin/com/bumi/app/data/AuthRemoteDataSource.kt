package com.bumi.app.data

import com.bumi.app.data.remote.AuthApiService
import com.bumi.app.data.response.LoginRequest
import com.bumi.app.data.response.LoginResponse

class AuthRemoteDataSource(
    private val apiService: AuthApiService
) {
    suspend fun login(request: LoginRequest): LoginResponse {
        return apiService.login(request)
    }
}
