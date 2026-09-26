package com.bumi.app

import androidx.compose.ui.window.ComposeUIViewController
import com.bumi.app.di.initKoin

fun MainViewController() = ComposeUIViewController(
    configure = {
        initKoin()
    }
) { App() }