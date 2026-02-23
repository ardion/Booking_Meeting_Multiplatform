package com.bumi.app

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.bumi.app.di.appModule         // Module yang berisi UseCase & Repo
import com.bumi.app.di.networkModule     // Module yang berisi Ktor
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.dsl.module

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // --- INISIALISASI KOIN ---
        // Kita cek dulu apakah Koin sudah jalan (untuk menghindari error 'Koin already started')
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
            // Jika sudah start, tidak apa-apa
        }

        setContent {
            App()
        }
    }
}

// Module khusus Android untuk menyediakan Settings (SharedPreferences)
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