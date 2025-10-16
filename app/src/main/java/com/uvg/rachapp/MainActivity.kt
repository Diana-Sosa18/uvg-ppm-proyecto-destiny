package com.uvg.rachapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.uvg.rachapp.ui.navigation.AppNavigation
import com.uvg.rachapp.ui.navigation.AuthNavHost
import com.uvg.rachapp.ui.theme.RachAppTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import dagger.hilt.android.AndroidEntryPoint

//import androidx.media3.common.util.Log

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
//    override fun onDestroy() {
//        super.onDestroy()
//        Log.d("MainActivity", "onDestroy llamado - la app se está cerrando")
//    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RachAppTheme {
                var isLoggedIn by remember { mutableStateOf(false) } // Estado de autenticación
                val navController = rememberNavController()

                if (isLoggedIn) {
                    AppNavigation (
                        modifier = Modifier.fillMaxSize(),
                        onLogout = { isLoggedIn = false }
                    )
                } else {
                    AuthNavHost(
                        navController = navController,
                        modifier = Modifier.fillMaxSize(),
                        onLoginSuccess = { isLoggedIn = true })
                }
            }
        }
    }
}
