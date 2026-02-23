package com.bumi.app.domain.Irepository

import com.bumi.app.data.response.LoginRequest
import com.bumi.app.domain.model.AuthUser
import com.bumi.app.utils.Resource

interface AuthRepository {
    // Return-nya sekarang AuthUser (Data yang sudah bersih)
    suspend fun login(request: LoginRequest): Resource<AuthUser>
}