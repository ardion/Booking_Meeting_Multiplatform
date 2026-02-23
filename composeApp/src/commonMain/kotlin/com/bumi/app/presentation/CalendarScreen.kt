package com.bumi.app.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.bumi.app.Screen
import com.bumi.app.utils.SessionManager
import org.koin.compose.koinInject


@Composable
fun CalendarScreen(
    navController: NavHostController
) {
    val sessionManager: SessionManager = koinInject()
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Halaman Kalender (Coming Soon)", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(20.dp))

        Button(onClick = {

            navController.navigate(Screen.Login.route) {
                popUpTo(Screen.Calendar.route) { inclusive = true }
            }
        }) {
            Text("Logout")
        }
    }
}