package com.bumi.app

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.bumi.app.presentation.SplashScreen
import com.bumi.app.presentation.login.LoginScreen
import com.bumi.app.presentation.CalendarScreen
import com.bumi.app.presentation.booking.FullCalendarScreen

/**
 * Definisi rute navigasi menggunakan Sealed Class.
 * Ini mencegah error typo saat memanggil route.
 */
sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object Login : Screen("login")
    data object Calendar : Screen("calendar")
}

@Composable
fun App() {
    MaterialTheme {
        val navController = rememberNavController()

        Surface(
            modifier = Modifier.fillMaxSize(),
            // Warna background biru sesuai kodemu
            color = Color(0xFF4675A6)
        ) {
            NavHost(
                navController = navController,
                startDestination = Screen.Splash.route
            ) {
                // 1. Splash Screen
                // Tidak perlu kirim SessionManager manual, biar Koin yang handle di dalamnya.
                composable(Screen.Splash.route) {
                    SplashScreen(navController = navController)
                }

                // 2. Login Screen
                composable(Screen.Login.route) {
                    LoginScreen(
                        onLoginSuccess = {
                            // Navigasi ke Calendar dan hapus Login dari backstack
                            navController.navigate(Screen.Calendar.route) {
                                popUpTo(Screen.Login.route) { inclusive = true }
                            }
                        }
                    )
                }

                // 3. Calendar Screen (Halaman Utama setelah Login)
                composable(Screen.Calendar.route) {
                    FullCalendarScreen(navController = navController)
                }
            }
        }
    }
}