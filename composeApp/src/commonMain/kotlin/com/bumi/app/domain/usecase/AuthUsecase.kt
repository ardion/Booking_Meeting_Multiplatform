package com.bumi.app.domain.usecase

import com.bumi.app.data.response.LoginRequest
import com.bumi.app.domain.Irepository.AuthRepository
import com.bumi.app.domain.model.AuthUser
import com.bumi.app.utils.Resource

class LoginUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(input: LoginRequest): Resource<AuthUser> {
        if (input.username.isBlank()) {
            return Resource.Error("Username tidak boleh kosong")
        }
        if (input.password.length < 6) {
            return Resource.Error("Password minimal 6 karakter")
        }

        return repository.login(input)
    }
}
