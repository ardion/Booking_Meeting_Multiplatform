package com.bumi.app

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow
import com.bumi.app.di.appModule
import com.bumi.app.di.networkModule
import com.russhwolf.settings.Settings
import com.russhwolf.settings.StorageSettings
import org.koin.core.context.startKoin
import org.koin.dsl.module

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    // 1. Inisialisasi Koin untuk Web
    startKoin {
        modules(
            appModule,
            networkModule,
            wasmPlatformModule
        )
    }

    // 2. Jalankan UI Compose
    CanvasBasedWindow(
        title = "BumiApp",
        canvasElementId = "ComposeTarget"
    ) {
        App()
    }
}

// Module khusus Web untuk menyediakan Settings (LocalStorage)
val wasmPlatformModule = module {
    single<Settings> { StorageSettings() }
}