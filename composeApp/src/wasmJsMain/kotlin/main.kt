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
    startKoin {
        modules(
            appModule,
            networkModule,
            wasmPlatformModule
        )
    }

    CanvasBasedWindow(
        title = "BumiApp",
        canvasElementId = "ComposeTarget"
    ) {
        App()
    }
}

val wasmPlatformModule = module {
    single<Settings> { StorageSettings() }
}
