package com.bumi.app.data.repository

import com.bumi.app.data.AuthRemoteDataSource
import com.bumi.app.data.mapper.toDomain
import com.bumi.app.data.response.LoginRequest
import com.bumi.app.domain.Irepository.AuthRepository
import com.bumi.app.domain.model.AuthUser
import com.bumi.app.utils.Resource
import io.ktor.client.plugins.*

class AuthRepositoryImpl(
    private val remoteDataSource: AuthRemoteDataSource
) : AuthRepository {

    override suspend fun login(request: LoginRequest): Resource<AuthUser> {
        return try {
            val response = remoteDataSource.login(request)

            if (response.data != null) {
                Resource.Success(response.data.toDomain())
            } else {
                Resource.Error(response.message ?: "Username atau password salah")
            }
        } catch (e: ClientRequestException) {
            Resource.Error("Username atau password salah")
        } catch (e: ServerResponseException) {
            Resource.Error("Server sedang bermasalah")
        } catch (e: Exception) {
            Resource.Error("Koneksi gagal: ${e.message}")
        }
    }
}
