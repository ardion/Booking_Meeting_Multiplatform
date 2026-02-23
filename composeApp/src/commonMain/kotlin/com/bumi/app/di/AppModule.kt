package com.bumi.app.di

import com.bumi.app.data.MeetingRemoteDataSource
import com.bumi.app.data.MeetingRemoteDataSourceImpl
import com.bumi.app.data.repository.AuthRepositoryImpl
import com.bumi.app.data.repository.MeetingRepositoryImpl
import com.bumi.app.domain.Irepository.AuthRepository
import com.bumi.app.domain.Irepository.MeetingRepository
import com.bumi.app.domain.usecase.CreateBookingUseCase
import com.bumi.app.domain.usecase.DeleteBookingUseCase
import com.bumi.app.domain.usecase.GetBookedSlotsUseCase
import com.bumi.app.domain.usecase.GetBookingsUseCase
import com.bumi.app.domain.usecase.LoginUseCase
import com.bumi.app.domain.usecase.MeetingUseCases
import com.bumi.app.domain.usecase.UpdateStatusUseCase
import com.bumi.app.presentation.booking.MeetingViewModel
import com.bumi.app.presentation.login.AuthViewModel
import com.bumi.app.utils.SessionManager
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

import com.russhwolf.settings.Settings

import com.bumi.app.data.*
import com.bumi.app.data.remote.AuthApiService
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging

import org.koin.core.module.dsl.viewModel // Sesuaikan dengan versi Koin KMP kamu

val appModule = module {

    // --- 1. CORE / INFRASTRUCTURE ---
    single {
        HttpClient {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    prettyPrint = true
                })
            }
            // --- TAMBAHKAN INI ---
            install(Logging) {
                level = LogLevel.ALL // Mencetak header, body, dan URL
                logger = object : Logger {
                    override fun log(message: String) {
                        println("KTOR_LOG: $message") // Filter ini di Logcat nanti
                    }
                }
            }
        }
    }

    // Settings & Session Management
    single { Settings() }
    single { SessionManager(get()) }
    single<CoroutineDispatcher> { Dispatchers.Default }

    // --- 2. API SERVICES ---
    // Pastikan AuthApiService kamu didaftarkan agar bisa dipakai RemoteDataSource
    single { AuthApiService(get()) }

    // --- 3. DATA SOURCES ---
    // AuthRemoteDataSource adalah class biasa (sesuai file yang kamu kirim)
    single { AuthRemoteDataSource(get()) }

    // MeetingRemoteDataSource biasanya berupa interface + impl
    singleOf(::MeetingRemoteDataSourceImpl) { bind<MeetingRemoteDataSource>() }

    // --- 4. REPOSITORIES ---
    // Di sini AuthRepositoryImpl akan otomatis mengambil AuthRemoteDataSource dari poin 3
    singleOf(::AuthRepositoryImpl) { bind<AuthRepository>() }
    singleOf(::MeetingRepositoryImpl) { bind<MeetingRepository>() }

    // --- 5. USE CASES ---
    single { LoginUseCase(get()) }
    single {
        MeetingUseCases(
            getBookings = GetBookingsUseCase(get()),
            createBooking = CreateBookingUseCase(get()),
            updateStatus = UpdateStatusUseCase(get()),
            deleteBooking = DeleteBookingUseCase(get()),
            getBookedSlots = GetBookedSlotsUseCase(get())
        )
    }

    // --- 6. VIEWMODELS (Selalu di paling bawah) ---
    // get() pertama untuk LoginUseCase, get() kedua untuk SessionManager
    viewModel { AuthViewModel(get(), get()) }
    viewModel { MeetingViewModel(get()) }
}