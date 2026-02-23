package com.bumi.app

import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.bumi.app.di.initKoin
import org.jetbrains.compose.resources.painterResource

fun main() {
    initKoin()
    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "BumiApp"
        ) {
            App()
        }
    }
}