package com.uvg.rachapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        auth.signOut()

        setContent {
            RachAppTheme {
                var isLoggedIn by remember { mutableStateOf(auth.currentUser != null) }

                // Mantiene sincronizado el estado con FirebaseAuth
                LaunchedEffect(Unit) {
                    auth.addAuthStateListener { firebaseAuth ->
                        isLoggedIn = firebaseAuth.currentUser != null
                    }
                }

                val authNavController = rememberNavController()

                if (isLoggedIn) {
                    AppNavigation(
                        modifier = Modifier.fillMaxSize(),
                        onLogout = {
                            auth.signOut()
                        }
                    )
                } else {
                    AuthNavHost(
                        navController = authNavController,
                        modifier = Modifier.fillMaxSize(),
                        onLoginSuccess = {
                            // No se cambia isLoggedIn, ya que FirebaseAuthListener lo hará
                        }
                    )
                }
            }
        }
    }
}
