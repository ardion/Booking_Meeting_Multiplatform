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
            // Ktor langsung mengembalikan LoginResponse (bukan Response wrapper)
            val response = remoteDataSource.login(request)

            // Karena Ktor sudah melakukan deserialisasi otomatis,
            // kita tinggal cek datanya
            if (response.data != null) {
                Resource.Success(response.data.toDomain())
            } else {
                Resource.Error(response.message ?: "Username atau password salah")
            }
        } catch (e: ClientRequestException) {
            // Menangkap error 4xx (Misal: 401 Unauthorized)
            Resource.Error("Username atau password salah")
        } catch (e: ServerResponseException) {
            // Menangkap error 5xx (Server mati)
            Resource.Error("Server sedang bermasalah")
        } catch (e: Exception) {
            // Menangkap error koneksi/internet
            Resource.Error("Koneksi gagal: ${e.message}")
        }
    }
}