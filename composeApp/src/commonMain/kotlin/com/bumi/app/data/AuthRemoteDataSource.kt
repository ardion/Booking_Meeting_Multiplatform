package com.bumi.app.data

import com.bumi.app.data.remote.AuthApiService
import com.bumi.app.data.response.LoginRequest
import com.bumi.app.data.response.LoginResponse
import org.koin.core.component.KoinComponent

// Kita hilangkan @Inject constructor dan ganti dengan constructor biasa
// Koin akan menangani penyuntikan AuthApiService secara otomatis
class AuthRemoteDataSource(
    private val apiService: AuthApiService
) {
    suspend fun login(request: LoginRequest): LoginResponse {
        // Kita tidak lagi mengembalikan Response<LoginResponse>
        // Ktor akan langsung melempar data LoginResponse jika sukses
        return apiService.login(request)
    }
}