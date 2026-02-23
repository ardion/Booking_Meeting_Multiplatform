package com.bumi.app.presentation

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource
import bumiapp.composeapp.generated.resources.Res // Import Resource KMP
import bumiapp.composeapp.generated.resources.ic_bumi // Nama filenya
import com.bumi.app.Screen
import com.bumi.app.utils.SessionManager
import org.koin.compose.koinInject

@Composable
fun SplashScreen(navController: NavHostController) {
    val sessionManager: SessionManager = koinInject()
    val scale = remember { Animatable(0f) }

    LaunchedEffect(key1 = true) {
        // Animasi membesar (KMP version)
        scale.animateTo(
            targetValue = 0.9f,
            animationSpec = tween(
                durationMillis = 800,
                easing = FastOutSlowInEasing // Pengganti Overshoot yang aman di KMP
            )
        )

        delay(2000L)

        // Cek login via SessionManager
        val destination = if (sessionManager.isLoggedIn()) {
            Screen.Calendar.route
        } else {
            Screen.Login.route
        }

        navController.navigate(destination) {
            popUpTo(Screen.Splash.route) { inclusive = true }
        }
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF4675A6))
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                // Di KMP cara panggil gambarnya seperti ini
                painter = painterResource(Res.drawable.ic_bumi),
                contentDescription = "Logo BUMI",
                modifier = Modifier
                    .size(300.dp)
                    .scale(scale.value)
            )
        }
    }
}