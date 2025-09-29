package com.uvg.rachapp.ui.navigation


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import com.uvg.rachapp.PlaceholderScreen
import com.uvg.rachapp.R
import kotlinx.coroutines.launch


@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    onLogout: () -> Unit
) {
    val navController = rememberNavController()
    var selectedTab by remember { mutableStateOf(Screen.Home.route) }

    val tabItems = listOf(
        Screen.Journal to R.drawable.journal_con_fondo,
        Screen.News to R.drawable.noticias_con_fondo,
        Screen.Home to R.drawable.home_con_fondo,
        Screen.Profile to R.drawable.perfil_con_fondo
    )

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = MaterialTheme.colorScheme.primary) {
                tabItems.forEach { (screen, iconRes) ->
                    val isSelected = selectedTab == screen.route

                    NavigationBarItem(
                        selected = isSelected,
                        onClick = {
                            selectedTab = screen.route
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                val iconSize: Dp = if (isSelected) 20.dp else 24.dp
                                val iconAlpha = if (isSelected) 1f else 0.5f

                                Image(
                                    painter = painterResource(id = iconRes),
                                    contentDescription = screen.route,
                                    modifier = Modifier
                                        .size(iconSize)
                                        .alpha(iconAlpha),
                                    contentScale = ContentScale.Fit
                                )

                                if (isSelected) {
                                    Text(
                                        text = screen.route,
                                        color = Color.White,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        },
                        label = null
                    )
                }
            }
        }
    ) { padding ->
        AppNavHost(
            navController = navController,
            modifier = modifier.padding(padding),
            onLogout = onLogout
        )
    }
}