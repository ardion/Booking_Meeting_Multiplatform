package com.bumi.app.di

import com.bumi.app.data.remote.AuthApiService
import com.bumi.app.data.remote.MeetingApiService
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.koin.dsl.module

val networkModule = module {
    single {
        HttpClient {
            install(Logging) {
                // Gunakan ALL agar header dan body terlihat jelas
                level = LogLevel.ALL
                logger = object : Logger {
                    override fun log(message: String) {
                        // Gunakan tag KTOR_LOG agar mudah di-filter di Logcat
                        println("KTOR_LOG: $message")
                    }
                }
            }

            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    prettyPrint = true
                    isLenient = true
                })
            }

            install(HttpTimeout) {
                requestTimeoutMillis = 30_000
                connectTimeoutMillis = 30_000
                socketTimeoutMillis = 30_000
            }

            defaultRequest {
                url("https://chatbot-bristle.online/")
                // Memastikan setiap request mengirim header JSON
                contentType(io.ktor.http.ContentType.Application.Json)
            }
        }
    }

    single { MeetingApiService(get()) }
    single { AuthApiService(get()) }
}