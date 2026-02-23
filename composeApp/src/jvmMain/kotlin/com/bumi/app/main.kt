package com.bumi.app

import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import bumiapp.composeapp.generated.resources.Res
import bumiapp.composeapp.generated.resources.ic_bumi
import com.bumi.app.di.initKoin
import org.jetbrains.compose.resources.painterResource

fun main() {
    initKoin()
    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "BumiApp",
            icon = painterResource(Res.drawable.ic_bumi)
        ) {
            App()
        }
    }
}