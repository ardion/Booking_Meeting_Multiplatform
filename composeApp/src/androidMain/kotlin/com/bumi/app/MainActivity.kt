package com.bumi.app

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.bumi.app.di.appModule
import com.bumi.app.di.networkModule
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.dsl.module

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        try {
            startKoin {
                androidContext(this@MainActivity)
                modules(
                    appModule,
                    networkModule,
                    androidPlatformModule(this@MainActivity)
                )
            }
        } catch (e: Exception) {
        }

        setContent {
            App()
        }
    }
}

fun androidPlatformModule(context: Context) = module {
    single<Settings> {
        val sharedPrefs = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)
        SharedPreferencesSettings(sharedPrefs)
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}