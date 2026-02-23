package com.bumi.app.domain.usecase

import com.bumi.app.data.response.LoginRequest
import com.bumi.app.domain.Irepository.AuthRepository
import com.bumi.app.domain.model.AuthUser
import com.bumi.app.utils.Resource

class LoginUseCase(
    private val repository: AuthRepository
) {
    // Invoke tetap bisa digunakan seperti biasa
    suspend operator fun invoke(input: LoginRequest): Resource<AuthUser> {

        // --- LOGIKA BISNIS (Domain Logic) ---
        if (input.username.isBlank()) {
            return Resource.Error("Username tidak boleh kosong")
        }
        if (input.password.length < 6) {
            return Resource.Error("Password minimal 6 karakter")
        }

        // Jika validasi lolos, panggil repository
        return repository.login(input)
    }
}