package com.bumi.app.di

import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

fun initKoin(appDeclaration: KoinAppDeclaration = {}) = startKoin {
    appDeclaration()
    // Masukkan SEMUA module agar dependensi lengkap
    modules(
        networkModule,
        appModule
    )
}